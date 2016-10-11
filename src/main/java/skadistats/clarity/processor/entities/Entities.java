package skadistats.clarity.processor.entities;

import com.google.protobuf.ByteString;
import skadistats.clarity.ClarityException;
import skadistats.clarity.LogChannel;
import skadistats.clarity.decoder.FieldReader;
import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.bitstream.BitStream;
import skadistats.clarity.event.Event;
import skadistats.clarity.event.Insert;
import skadistats.clarity.event.InsertEvent;
import skadistats.clarity.event.Provides;
import skadistats.clarity.logger.Logger;
import skadistats.clarity.logger.Logging;
import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.EngineType;
import skadistats.clarity.model.Entity;
import skadistats.clarity.model.StringTable;
import skadistats.clarity.processor.reader.OnMessage;
import skadistats.clarity.processor.reader.OnReset;
import skadistats.clarity.processor.reader.ResetPhase;
import skadistats.clarity.processor.runner.OnInit;
import skadistats.clarity.processor.sendtables.DTClasses;
import skadistats.clarity.processor.sendtables.OnDTClassesComplete;
import skadistats.clarity.processor.sendtables.UsesDTClasses;
import skadistats.clarity.processor.stringtables.OnStringTableEntry;
import skadistats.clarity.processor.stringtables.StringTables;
import skadistats.clarity.util.Predicate;
import skadistats.clarity.util.SimpleIterator;
import skadistats.clarity.wire.common.proto.Demo;
import skadistats.clarity.wire.common.proto.NetMessages;
import skadistats.clarity.wire.common.proto.NetworkBaseTypes;

import java.util.Iterator;
import java.util.LinkedList;

@Provides({ UsesEntities.class, OnEntityCreated.class, OnEntityUpdated.class, OnEntityDeleted.class, OnEntityEntered.class, OnEntityLeft.class, OnEntityUpdatesCompleted.class })
@UsesDTClasses
public class Entities {

    public static final String BASELINE_TABLE = "instancebaseline";

    private static final Logger log = Logging.getLogger(LogChannel.entities);

    private Entity[] entities;
    private int[] deletions;
    private FieldReader fieldReader;

    private class ClassBaseline {
        private ByteString raw;
        private Object[] cooked;
    }
    private ClassBaseline[] classBaselines;

    private class Baseline {
        private int dtClassId;
        private Object[] propertyState;
    }
    private Baseline[][] baselines;

    @Insert
    EngineType engineType;
    @Insert
    DTClasses dtClasses;
    @Insert
    StringTables stringTables;

    @InsertEvent
    Event<OnEntityCreated> evCreated;
    @InsertEvent
    Event<OnEntityUpdated> evUpdated;
    @InsertEvent
    Event<OnEntityDeleted> evDeleted;
    @InsertEvent
    Event<OnEntityEntered> evEntered;
    @InsertEvent
    Event<OnEntityLeft> evLeft;
    @InsertEvent
    Event<OnEntityUpdatesCompleted> evUpdatesCompleted;

    @OnInit
    public void onInit() {
        fieldReader = engineType.getNewFieldReader();

        int n = 1 << engineType.getIndexBits();

        entities = new Entity[n];
        for (int i = 0; i < n; i++) {
            entities[i] = new Entity(i) {
                @Override
                protected EngineType getEngineType() {
                    return engineType;
                }
                @Override
                protected DTClasses getDtClasses() {
                    return dtClasses;
                }
            };
        }

        baselines = new Baseline[n][2];
        for (int i = 0; i < n; i++) {
            baselines[i][0] = new Baseline();
            baselines[i][1] = new Baseline();
        }

        deletions = new int[n];
    }

    @OnReset
    public void onReset(Demo.CDemoStringTables packet, ResetPhase phase) {
        if (phase == ResetPhase.CLEAR) {
            for (int i = 0; i < classBaselines.length; i++) {
                classBaselines[i].raw = null;
                classBaselines[i].cooked = null;
            }
            for (int i = 0; i < entities.length; i++) {
                entities[i].updateClientFrame(null);
            }
            for (int i = 0; i < baselines.length; i++) {
                baselines[i][0].dtClassId = -1;
                baselines[i][1].dtClassId = -1;
            }
        }
    }

    @OnDTClassesComplete
    public void onDTClassesComplete() {
        classBaselines = new ClassBaseline[dtClasses.getClassCount()];
        for (int i = 0; i < classBaselines.length; i++) {
            classBaselines[i] = new ClassBaseline();
        }
    }

    @OnStringTableEntry(BASELINE_TABLE)
    public void onBaselineEntry(StringTable table, int index, String key, ByteString value) {
        if (classBaselines != null) {
            int i = Integer.valueOf(key);
            classBaselines[i].raw = value;
            classBaselines[i].cooked = null;
        }
    }

    private int serverTick;
    private LinkedList<ClientFrame> clientFrames = new LinkedList<>();
    boolean debug = false;

    @OnMessage(NetworkBaseTypes.CNETMsg_Tick.class)
    public void onMessage(NetworkBaseTypes.CNETMsg_Tick message) {
        serverTick = message.getTick();
    }

    @OnMessage(NetMessages.CSVCMsg_PacketEntities.class)
    public void onPacketEntities(NetMessages.CSVCMsg_PacketEntities message) {
        if (log.isDebugEnabled()) {
            log.debug("processing packet entities: now: %6d, delta-from: %6d, update-count: %5d, baseline: %d, update-baseline: %5s", serverTick, message.getDeltaFrom(), message.getUpdatedEntries(), message.getBaseline(), message.getUpdateBaseline());
        }

        ClientFrame newFrame = new ClientFrame(engineType, serverTick);
        ClientFrame oldFrame = null;

        if (message.getIsDelta()) {
            if (serverTick == message.getDeltaFrom()) {
                throw new ClarityException("received self-referential delta update for tick %d", serverTick);
            }
            oldFrame = getClientFrame(message.getDeltaFrom(), false);
            if (oldFrame == null) {
                log.warn("missing client frame for delta update from tick %d", message.getDeltaFrom());
                return;
            }
            log.debug("performing delta update, using old frame from tick %d", oldFrame.tick);
        } else {
            for (int i = 0; i < entities.length; i++) {
                entities[i].updateClientFrame(null);
            }
            log.debug("performing full update");
        }

        if (message.getUpdateBaseline()) {
            for (Baseline[] baseline : baselines) {
                baseline[1 - message.getBaseline()] = baseline[message.getBaseline()];
            }
        }

        BitStream stream = BitStream.createBitStream(message.getEntityData());

        int eCount = 1 << engineType.getIndexBits();
        int eIdx;
        int updateCount = message.getUpdatedEntries();
        int updateIndex = -1;
        int updateType;

        for (eIdx = 0; eIdx < eCount; eIdx++) {
            if (updateCount > 0 && updateIndex == -1) {
                updateIndex = eIdx + stream.readUBitVar();
                updateCount--;
            }
            if (updateIndex == eIdx) {
                updateType = stream.readUBitInt(2);
                updateIndex = -1;
            } else {
                updateType = 4;
            }

            EntityState eState;

            entities[eIdx].updateClientFrame(newFrame);

            switch (updateType) {
                case 2:
                    // CREATE ENTITY
                    int dtClassId = stream.readUBitInt(dtClasses.getClassBits());
                    DTClass dtClass = dtClasses.forClassId(dtClassId);
                    if (dtClass == null) {
                        throw new ClarityException("class for new entity %d is %d, but no dtClass found!.", eIdx, dtClassId);
                    }
                    int serial = stream.readUBitInt(engineType.getSerialBits());
                    if (engineType == EngineType.SOURCE2) {
                        // TODO: there is an extra VarInt encoded here for S2, figure out what it is
                        stream.readVarUInt();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("\tcreate entity: index: %4d, serial: %x class: %s", eIdx, serial, dtClass.getDtName());
                    }

                    Object[] propertyState = Util.clone(getBaseline(eIdx, dtClassId, message.getBaseline()));
                    fieldReader.readFields(stream, dtClass, propertyState, debug);
                    if (message.getUpdateBaseline()) {
                        Baseline baseline = baselines[eIdx][1 - message.getBaseline()];
                        baseline.dtClassId = dtClassId;
                        baseline.propertyState = Util.clone(propertyState);
                    }
                    eState = new EntityState(dtClassId, serial, true, propertyState);
                    newFrame.setState(eState, eIdx);
                    evCreated.raise(entities[eIdx]);
                    evEntered.raise(entities[eIdx]);
                    break;

                case 0:
                    // UPDATE ENTITY
                    eState = newFrame.cloneState(oldFrame, eIdx);
                    if (eState == null) {
                        throw new ClarityException("entity at index %d was not found for update.", eIdx);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("\tupdate entity: index: %4d, class: %s", eIdx, dtClasses.forClassId(eState.clsId).getDtName());
                    }
                    int nChanged = fieldReader.readFields(stream, dtClasses.forClassId(eState.clsId), eState.propertyState, debug);
                    evUpdated.raise(entities[eIdx], fieldReader.getFieldPaths(), nChanged);
                    if (!eState.active) {
                        eState.active = true;
                        evEntered.raise(entities[eIdx]);
                    }
                    break;

                case 3:
                    // DELETE ENTITY
                case 1:
                    // LEAVE ENTITY
                    eState = newFrame.copyState(oldFrame, eIdx);
                    if (eState == null) {
                        log.warn("entity at index %d was not found when ordered to leave.", eIdx);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("\t%s entity: index: %4d, class: %s", updateType == 3 ? "deleting" : "removing", eIdx, dtClasses.forClassId(eState.clsId).getDtName());
                        }
                        if (eState.active) {
                            eState.active = false;
                            evLeft.raise(entities[eIdx]);
                        }
                        if (updateType == 3) {
                            evDeleted.raise(entities[eIdx]);
                            newFrame.setState(null, eIdx);
                        }
                    }

                    break;

                case 4:
                    // PRESERVE ENTITY
                    newFrame.copyState(oldFrame, eIdx);
                    break;
            }

        }

        if (message.getIsDelta()) {
            int n = fieldReader.readDeletions(stream, engineType.getIndexBits(), deletions);
            for (int i = 0; i < n; i++) {
                eIdx = deletions[i];
                EntityState state = newFrame.getState(eIdx);
                if (state != null) {
                    log.debug("entity at index %d was ACTUALLY found when ordered to delete, tell the press!", eIdx);
                    if (state.active) {
                        state.active = false;
                        evLeft.raise(entities[eIdx]);
                    }
                    evDeleted.raise(entities[eIdx]);
                } else {
                    log.debug("entity at index %d was not found when ordered to delete.", eIdx);
                }
                newFrame.setState(null, eIdx);
            }
        }

        Iterator<ClientFrame> iter = clientFrames.iterator();
        while(iter.hasNext()) {
            ClientFrame frame = iter.next();
            if (frame.tick >= message.getDeltaFrom()) {
                break;
            }
            log.debug("deleting client frame for tick %d", frame.tick);
            iter.remove();
        }

        clientFrames.add(newFrame);

        evUpdatesCompleted.raise();
    }

    private ClientFrame getClientFrame(int tick, boolean exact) {
        Iterator<ClientFrame> iter = clientFrames.iterator();
        ClientFrame lastFrame = clientFrames.peekFirst();
        while (iter.hasNext()) {
            ClientFrame frame = iter.next();
            if (frame.getTick() >= tick) {
                if (frame.getTick() == tick) {
                    return frame;
                }
                if (exact) {
                    return null;
                }
                return lastFrame;
            }
            lastFrame = frame;
        }
        if (exact) {
            return null;
        }
        return lastFrame;
    }

    private Object[] getBaseline(int entityIdx, int clsId, int baseline) {
        Baseline b = baselines[entityIdx][baseline];
        if (b.dtClassId == clsId && b.propertyState != null) {
            return b.propertyState;
        }
        ClassBaseline be = classBaselines[clsId];
        if (be != null && be.cooked != null) {
            return be.cooked;
        }
        DTClass cls = dtClasses.forClassId(clsId);
        if (cls == null) {
            throw new ClarityException("DTClass for id %d not found.", clsId);
        }
        if (be == null) {
            throw new ClarityException("Baseline for class %s (%d) not found.", cls.getDtName(), clsId);
        }
        be.cooked = cls.getEmptyStateArray();
        if (be.raw != null) {
            BitStream stream = BitStream.createBitStream(be.raw);
            fieldReader.readFields(stream, cls, be.cooked, false);
        }
        return be.cooked;
    }

    public Entity getByIndex(int index) {
        return entities[index];
    }

    public Entity getByHandle(int handle) {
        Entity e = entities[engineType.indexForHandle(handle)];
        return e == null || e.getSerial() != engineType.serialForHandle(handle) ? null : e;
    }

    public Iterator<Entity> getAllByPredicate(final Predicate<Entity> predicate) {
        return new SimpleIterator<Entity>() {
            int i = -1;
            @Override
            public Entity readNext() {
                while(++i < entities.length) {
                    Entity e = entities[i];
                    if (e != null && predicate.apply(e)) {
                        return e;
                    }
                }
                return null;
            }
        };
    }

    public Entity getByPredicate(Predicate<Entity> predicate) {
        Iterator<Entity> iter = getAllByPredicate(predicate);
        return iter.hasNext() ? iter.next() : null;
    }

    public Iterator<Entity> getAllByDtName(final String dtClassName) {
        return getAllByPredicate(
            new Predicate<Entity>() {
                @Override
                public boolean apply(Entity e) {
                    return dtClassName.equals(e.getDtClass().getDtName());
                }
            });
    }

    public Entity getByDtName(final String dtClassName) {
        Iterator<Entity> iter = getAllByDtName(dtClassName);
        return iter.hasNext() ? iter.next() : null;
    }

}
