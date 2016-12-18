package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.s2.DumpEntry;
import skadistats.clarity.decoder.s2.S2UnpackerFactory;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public class FixedSubTableField extends Field {

    private final FieldType baseType;
    private final Unpacker baseUnpacker;

    private final Accessor accessor;

    public FixedSubTableField(FieldProperties properties) {
        super(properties);

        baseType = FieldType.forString("bool");
        baseUnpacker = S2UnpackerFactory.createUnpacker(properties, baseType.getBaseType());

        accessor = new Accessor() {
            @Override
            public String getNameSegment(int i) {
                return getProperties().getName();
            }
            @Override
            public boolean isPointer() {
                return true;
            }
            @Override
            public Unpacker getUnpacker() {
                return baseUnpacker;
            }
            @Override
            public FieldType getType() {
                return baseType;
            }
            @Override
            public Accessor getSubAccessor(int i) {
                return getProperties().getSerializer().getSubAccessor(i);
            }
            @Override
            public Integer getSubStateLength() {
                return getProperties().getSerializer().getSubStateLength();
            }
        };
    }

    @Override
    public Accessor getAccessor() {
        return accessor;
    }

    @Override
    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        addBasePropertyName(parts);
        if (fp.last >= pos) {
            properties.getSerializer().accumulateName(fp, pos, parts);
        }
    }

    @Override
    public FieldPath getFieldPathForName(FieldPath fp, String property) {
        return properties.getSerializer().getFieldPathForName(fp, property);
    }

    @Override
    public void collectDump(FieldPath fp, String namePrefix, List<DumpEntry> entries, Object[] state) {
        Object[] subState = (Object[]) state[fp.path[fp.last]];
        String name = joinPropertyName(namePrefix, properties.getName());
        if (subState != null) {
            fp.last++;
            properties.getSerializer().collectDump(fp, name, entries, subState);
            fp.last--;
        }
    }

    @Override
    public void collectFieldPaths(FieldPath fp, List<FieldPath> entries, Object[] state) {
        Object[] subState = (Object[]) state[fp.path[fp.last]];
        if (subState != null) {
            fp.last++;
            properties.getSerializer().collectFieldPaths(fp, entries, subState);
            fp.last--;
        }
    }

}
