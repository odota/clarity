package skadistats.clarity.model.state;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;

public class Accessor implements AccessorFactory {

    public Unpacker getUnpacker() {
        throw new UnsupportedOperationException();
    }

    public FieldType getType() {
        throw new UnsupportedOperationException();
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
