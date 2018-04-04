package skadistats.clarity.model.engine;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.ZeroCopy;
import skadistats.clarity.decoder.FieldReader;
import skadistats.clarity.decoder.bitstream.BitStream;
import skadistats.clarity.decoder.s1.CsGoFieldReader;
import skadistats.clarity.event.Insert;
import skadistats.clarity.model.DemoHeader;
import skadistats.clarity.model.EngineId;
import skadistats.clarity.processor.reader.OnMessage;
import skadistats.clarity.processor.reader.OnPostEmbeddedMessage;
import skadistats.clarity.processor.reader.PacketInstance;
import skadistats.clarity.processor.runner.Context;
import skadistats.clarity.source.Source;
import skadistats.clarity.wire.common.proto.Demo;
import skadistats.clarity.wire.csgo.EmbeddedPackets;
import skadistats.clarity.wire.csgo.UserMessagePackets;
import skadistats.clarity.wire.s1.proto.S1NetMessages;

import java.io.IOException;

public class CsGoEngineType extends AbstractEngineType {

    @Insert
    private Context ctx;

    public CsGoEngineType(EngineId identifier) {
        super(identifier,
                0x100,
                true,   // CDemoSendTables is container
                11, 10
        );
    }

    @Override
    public boolean handleDeletions() {
        return false;
    }

    @Override
    public Class<? extends GeneratedMessage> embeddedPacketClassForKind(int kind) {
        return EmbeddedPackets.classForKind(kind);
    }

    @Override
    public Class<? extends GeneratedMessage> userMessagePacketClassForKind(int kind) {
        return UserMessagePackets.classForKind(kind);
    }

    @Override
    public boolean isUserMessage(Class<? extends GeneratedMessage> clazz) {
        return UserMessagePackets.isKnownClass(clazz);
    }

    @Override
    public FieldReader getNewFieldReader() {
        return new CsGoFieldReader();
    }

    @Override
    public DemoHeader readHeader(Source source) throws IOException {
//            int32	demoprotocol;					// Should be DEMO_PROTOCOL
        source.skipBytes(4);
//            int32	networkprotocol;				// Should be PROTOCOL_VERSION
        source.skipBytes(4);
//            char	servername[ MAX_OSPATH ];		// Name of server
        source.skipBytes(260);
//            char	clientname[ MAX_OSPATH ];		// Name of client who recorded the game
        source.skipBytes(260);
//            char	mapname[ MAX_OSPATH ];			// Name of map
        source.skipBytes(260);
//            char	gamedirectory[ MAX_OSPATH ];	// Name of game directory (com_gamedir)
        source.skipBytes(260);
//            float	playback_time;					// Time of track
        source.skipBytes(4);
//            int32   playback_ticks;					// # of ticks in track
        source.skipBytes(4);
//            int32   playback_frames;				// # of frames in track
        source.skipBytes(4);
//            int32	signonlength;					// length of sigondata in bytes
        source.skipBytes(4);
        return null;
    }

    @Override
    public int readKind(Source source) throws IOException {
        return source.readByte() & 0xFF;
    }

    @Override
    public int readTick(Source source) throws IOException {
        return source.readFixedInt32();
    }

    @Override
    public int readPlayerSlot(Source source) throws IOException {
        return source.readByte() & 0xFF;
    }

    @Override
    public int readSize(Source source) throws IOException {
        return source.readFixedInt32();
    }

    @Override
    public int readEmbeddedKind(BitStream bs) {
        return bs.readVarUInt();
    }

    @Override
    public void readCommandInfo(Source source) throws IOException {
        source.skipBytes(152);
    }

    public <T extends GeneratedMessage> PacketInstance<T> getNextPacketInstance(final Source source) throws IOException {
        final int kind = readKind(source);
        final int tick = readTick(source);
        readPlayerSlot(source);
        final Class<T> cls;
        switch (kind) {
            case 1:
            case 2:
                cls = (Class<T>) Demo.CDemoPacket.class;
                break;
            case 3:
                cls = (Class<T>) Demo.CDemoSyncTick.class;
                break;
            case 5:
                cls = (Class<T>) Demo.CDemoUserCmd.class;
                break;
            case 6:
                cls = (Class<T>) Demo.CDemoSendTables.class;
                break;
            case 7:
                cls = (Class<T>) Demo.CDemoStop.class;
                break;
            case 9:
                cls = null;
                break;

            default:
                throw new UnsupportedOperationException("kind " + kind + " not implemented");
        }
        return new PacketInstance<T>() {
            @Override
            public int getKind() {
                return kind;
            }
            @Override
            public int getTick() {
                return tick;
            }
            @Override
            public Class<T> getMessageClass() {
                return cls;
            }
            @Override
            public T parse() throws IOException {
                byte[] buf;
                int size;
                switch (kind) {
                    case 1:
                    case 2:
                        readCommandInfo(source);
                        source.skipBytes(8);
                        size = readSize(source);
                        buf = new byte[size];
                        source.readBytes(buf, 0, size);
                        return (T) Demo.CDemoPacket.newBuilder()
                                .setData(ZeroCopy.wrap(buf))
                                .build();
                    case 3:
                        return (T) Demo.CDemoSyncTick.newBuilder()
                                .build();
                    case 5:
                        source.skipBytes(4);
                        size = readSize(source);
                        buf = new byte[size];
                        source.readBytes(buf, 0, size);
                        return (T) Demo.CDemoUserCmd.newBuilder()
                                .setData(ZeroCopy.wrap(buf))
                                .build();
                    case 6:
                        size = readSize(source);
                        buf = new byte[size];
                        source.readBytes(buf, 0, size);
                        return (T) Demo.CDemoSendTables.newBuilder()
                                .setData(ZeroCopy.wrap(buf))
                                .build();
                    case 7:
                        return (T) Demo.CDemoStop.newBuilder()
                                .build();
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            @Override
            public void skip() throws IOException {
                switch (kind) {
                    case 1:
                    case 2:
                        readCommandInfo(source);
                        source.skipBytes(8);
                        source.skipBytes(readSize(source));
                        break;
                    case 5:
                        source.skipBytes(4);
                        source.skipBytes(readSize(source));
                        break;
                    case 6:
                        source.skipBytes(readSize(source));
                        break;
                    case 9:
                        source.skipBytes(readSize(source));
                        break;
                    case 3:
                    case 7:
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        };
    }



    @OnPostEmbeddedMessage(S1NetMessages.CSVCMsg_SendTable.class)
    public void onPostSendTable(S1NetMessages.CSVCMsg_SendTable message, BitStream bs) {
        if (message.getIsEnd()) {
            Demo.CDemoClassInfo.Builder b = Demo.CDemoClassInfo.newBuilder();
            int n = bs.readSBitInt(16);
            for (int i = 0; i < n; i++) {
                Demo.CDemoClassInfo.class_t.Builder cb = Demo.CDemoClassInfo.class_t.newBuilder();
                b.addClasses(cb
                        .setClassId(bs.readSBitInt(16))
                        .setNetworkName(bs.readString(255))
                        .setTableName(bs.readString(255))
                        .build()
                );
            }
            ctx.createEvent(OnMessage.class, Demo.CDemoClassInfo.class).raise(b.build());
        }
    }



}
