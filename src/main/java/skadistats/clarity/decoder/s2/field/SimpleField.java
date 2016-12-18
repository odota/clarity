package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.s2.DumpEntry;
import skadistats.clarity.decoder.s2.S2UnpackerFactory;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public class SimpleField extends Field {

    private final Unpacker unpacker;
    private final Accessor accessor;

    public SimpleField(FieldProperties properties) {
        super(properties);
        unpacker = S2UnpackerFactory.createUnpacker(properties, properties.getType().getBaseType());
        accessor = new Accessor() {
            @Override
            public String getNameSegment(int i) {
                return getProperties().getName();
            }
            @Override
            public Unpacker getUnpacker() {
                return unpacker;
            }
            @Override
            public FieldType getType() {
                return getProperties().getType();
            }
        };
    }

    @Override
    public Accessor getAccessor() {
        return accessor;
    }

    @Override
    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        assert fp.last == pos - 1;
        addBasePropertyName(parts);
    }

    @Override
    public FieldPath getFieldPathForName(FieldPath fp, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void collectDump(FieldPath fp, String namePrefix, List<DumpEntry> entries, Object[] state) {
        entries.add(new DumpEntry(fp, joinPropertyName(namePrefix, properties.getName()), state[fp.path[fp.last]]));
    }

    @Override
    public void collectFieldPaths(FieldPath fp, List<FieldPath> entries, Object[] state) {
        entries.add(new FieldPath(fp));
    }

}
