package skadistats.clarity.model.state;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.s2.field.FieldProperties;
import skadistats.clarity.decoder.unpacker.Unpacker;

public abstract class Accessor implements AccessorFactory {

    public abstract String getNameSegment(int i);

    public Unpacker getUnpacker() {
        throw new UnsupportedOperationException();
    }

    public FieldType getType() {
        throw new UnsupportedOperationException();
    }

    public FieldProperties getFieldProperties() {
        return null;
    }

    public boolean isPointer() {
        return false;
    }

    public boolean isVariableArray() {
        return false;
    }

    @Override
    public Accessor getSubAccessor(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getSubStateLength() {
        return null;
    }

}
