package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.ClarityException;
import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.DumpEntry;
import skadistats.clarity.decoder.s2.S2UnpackerFactory;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public class VarArrayField extends Field {

    private final FieldType baseType;
    private final Unpacker baseUnpacker;

    private final FieldType elementType;
    private final Unpacker elementUnpacker;

    private final Accessor elementAccessor;
    private final Accessor accessor;

    public VarArrayField(FieldProperties properties) {
        super(properties);

        baseType = FieldType.forString("uint32");
        baseUnpacker = S2UnpackerFactory.createUnpacker(properties, baseType.getBaseType());

        elementType = FieldType.forString(properties.getType().getBaseType());
        elementUnpacker = S2UnpackerFactory.createUnpacker(properties, elementType.getBaseType());

        elementAccessor = new Accessor() {
            @Override
            public Unpacker getUnpacker() {
                return elementUnpacker;
            }
            @Override
            public FieldType getType() {
                return elementType;
            }
        };

        accessor = new Accessor() {
            @Override
            public boolean isVariableArray() {
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
                return elementAccessor;
            }
        };
    }

    @Override
    public Accessor getAccessor() {
        return accessor;
    }

    @Override
    public Object getInitialState() {
        return null;
    }

    @Override
    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        assert fp.last == pos || fp.last == pos - 1;
        addBasePropertyName(parts);
        if (fp.last == pos) {
            parts.add(Util.arrayIdxToString(fp.path[pos]));
        }
    }

    @Override
    public Unpacker getUnpackerForFieldPath(FieldPath fp, int pos) {
        assert fp.last == pos || fp.last == pos - 1;
        if (pos == fp.last) {
            return elementUnpacker;
        } else {
            return baseUnpacker;
        }
    }

    @Override
    public Field getFieldForFieldPath(FieldPath fp, int pos) {
        assert fp.last == pos || fp.last == pos - 1;
        return this;
    }

    @Override
    public FieldType getTypeForFieldPath(FieldPath fp, int pos) {
        assert fp.last == pos || fp.last == pos - 1;
        if (pos == fp.last) {
            return properties.getType().getGenericType();
        } else {
            return properties.getType();
        }
    }

    @Override
    public Object getValueForFieldPath(FieldPath fp, int pos, Object[] state) {
        assert fp.last == pos;
        Object[] subState = (Object[]) state[fp.path[pos - 1]];
        return subState[fp.path[pos]];
    }

    @Override
    public void setValueForFieldPath(FieldPath fp, int pos, Object[] state, Object value) {
        assert fp.last == pos || fp.last == pos - 1;
        int i = fp.path[pos - 1];
        if (fp.last == pos) {
            Object[] subState = ensureSubStateCapacity(state, i, fp.path[pos] + 1, false);
            subState[fp.path[pos]] = value;
        } else {
            ensureSubStateCapacity(state, i, (Integer) value, true);
        }
    }

    @Override
    public FieldPath getFieldPathForName(FieldPath fp, String property) {
        if (property.length() != 4) {
            throw new ClarityException("unresolvable fieldpath");
        }
        fp.path[fp.last] = Integer.valueOf(property);
        return fp;
    }

    @Override
    public void collectDump(FieldPath fp, String namePrefix, List<DumpEntry> entries, Object[] state) {
        Object[] subState = (Object[]) state[fp.path[fp.last]];
        fp.last++;
        for (int i = 0; i < subState.length; i++) {
            if (subState[i] != null) {
                fp.path[fp.last] = i;
                entries.add(new DumpEntry(fp, joinPropertyName(namePrefix, properties.getName(), Util.arrayIdxToString(i)), subState[i]));
            }
        }
        fp.last--;
    }

    @Override
    public void collectFieldPaths(FieldPath fp, List<FieldPath> entries, Object[] state) {
        Object[] subState = (Object[]) state[fp.path[fp.last]];
        fp.last++;
        for (int i = 0; i < subState.length; i++) {
            if (subState[i] != null) {
                fp.path[fp.last] = i;
                entries.add(new FieldPath(fp));
            }
        }
        fp.last--;
    }

}
