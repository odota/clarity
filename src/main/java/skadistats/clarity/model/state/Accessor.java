package skadistats.clarity.model.state;

import skadistats.clarity.decoder.s2.field.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;

public interface Accessor extends AccessorFactory {

    Unpacker getUnpacker();
    FieldType getType();

}
