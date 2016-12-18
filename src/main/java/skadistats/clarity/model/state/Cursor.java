package skadistats.clarity.model.state;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.s2.field.FieldProperties;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;

public interface Cursor {

    <T> T getValue();

    FieldPath getFieldPath();

    Unpacker getUnpacker();
    FieldType getType();
    FieldProperties getFieldProperties();

    void setValue(Object data);

}
