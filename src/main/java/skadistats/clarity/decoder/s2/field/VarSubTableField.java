package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.S2UnpackerFactory;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public class VarSubTableField extends Field {

    private final int maxLength;

    private final FieldType baseType;
    private final Unpacker baseUnpacker;

    private final Accessor elementAccessor;
    private final Accessor accessor;

    public VarSubTableField(FieldProperties properties, int maxLength) {
        super(properties);
        this.maxLength = maxLength;

        baseType = FieldType.forString("uint32");
        baseUnpacker = S2UnpackerFactory.createUnpacker(properties, baseType.getBaseType());

        elementAccessor = new Accessor() {
            @Override
            public int getNeededMemorySize() {
                return getProperties().getSerializer().getNeededMemorySize();
            }
            @Override
            public String getNameSegment(int i) {
                return Util.arrayIdxToString(i);
            }
            @Override
            public Unpacker getUnpacker() {
                throw new UnsupportedOperationException();
            }
            @Override
            public FieldType getType() {
                throw new UnsupportedOperationException();
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

        accessor = new Accessor() {
            @Override
            public int getNeededMemorySize() {
                return VarSubTableField.this.maxLength * elementAccessor.getNeededMemorySize();
            }
            @Override
            public String getNameSegment(int i) {
                return getProperties().getName();
            }
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
    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        addBasePropertyName(parts);
        if (fp.last != pos - 1) {
            parts.add(Util.arrayIdxToString(fp.path[pos]));
            if (fp.last != pos) {
                properties.getSerializer().accumulateName(fp, pos + 1, parts);
            }
        }
    }

    @Override
    public FieldPath getFieldPathForName(FieldPath fp, String property) {
        String idx = property.substring(0, 4);
        fp.path[fp.last] = Integer.valueOf(idx);
        fp.last++;
        return properties.getSerializer().getFieldPathForName(fp, property.substring(5));
    }

}
