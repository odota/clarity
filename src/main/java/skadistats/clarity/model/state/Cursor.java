package skadistats.clarity.model.state;

import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.s2.field.FieldProperties;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;

public interface Cursor {

    FieldPath getFieldPath();

    <T> T getValue();
    void setValue(Object data);

    Unpacker getUnpacker();
    FieldType getFieldType();
    FieldProperties getFieldProperties();
    String getPropertyName();

}
