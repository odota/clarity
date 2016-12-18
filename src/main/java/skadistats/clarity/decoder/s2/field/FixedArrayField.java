package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.ClarityException;
import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.S2UnpackerFactory;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.MutableFieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public class FixedArrayField extends Field {

    private final int length;

    private final FieldType elementType;
    private final Unpacker elementUnpacker;

    private final Accessor elementAccessor;
    private final Accessor accessor;

    public FixedArrayField(FieldProperties properties, int length) {
        super(properties);
        this.length = length;

        elementType = FieldType.forString(properties.getType().getBaseType());
        elementUnpacker = S2UnpackerFactory.createUnpacker(properties, elementType.getBaseType());
        elementAccessor = new Accessor() {
            @Override
            public int getNeededMemorySize() {
                return elementUnpacker.getNeededMemorySize();
            }
            @Override
            public String getNameSegment(int i) {
                return Util.arrayIdxToString(i);
            }
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
            public int getNeededMemorySize() {
                return FixedArrayField.this.length * elementAccessor.getNeededMemorySize();
            }
            @Override
            public String getNameSegment(int i) {
                return getProperties().getName();
            }
            @Override
            public Accessor getSubAccessor(int i) {
                return elementAccessor;
            }
            @Override
            public Integer getSubStateLength() {
                return FixedArrayField.this.length;
            }
        };
    }

    @Override
    public Accessor getAccessor() {
        return accessor;
    }

    @Override
    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        assert fp.getLast() == pos || fp.getLast() == pos - 1;
        addBasePropertyName(parts);
        if (fp.getLast() == pos) {
            parts.add(Util.arrayIdxToString(fp.getElement(pos)));
        }
    }

    @Override
    public FieldPath getFieldPathForName(MutableFieldPath fp, String property) {
        if (property.length() != 4) {
            throw new ClarityException("unresolvable fieldpath");
        }
        fp.path[fp.last] = Integer.valueOf(property);
        return fp.toImmutable();
    }

}
