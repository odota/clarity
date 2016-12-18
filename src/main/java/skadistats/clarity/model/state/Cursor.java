package skadistats.clarity.model.state;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;

public interface Cursor {

    <T> T getValue();

    FieldPath getFieldPath();

    Unpacker getUnpacker();
    FieldType getType();

    void setValue(Object data);

}
