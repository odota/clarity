package skadistats.clarity.model.state;

import skadistats.clarity.decoder.s2.field.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;

public class Accessor implements AccessorFactory {

    public Unpacker getUnpacker() {
        throw new UnsupportedOperationException();
    }

    public FieldType getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Accessor getAccessor(int i) {
        throw new UnsupportedOperationException();
    }

}
