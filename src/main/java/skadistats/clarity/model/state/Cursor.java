package skadistats.clarity.model.state;

import skadistats.clarity.decoder.s2.field.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.FieldPath;

public interface Cursor {
    <T> T getValue();

    Cursor copy();
    FieldPath getFieldPath();

    void push(int i);
    void pop(int n);
    void add(int i);

    int getDepth();

    Unpacker getUnpacker();
    FieldType getType();

    void setValue(Object data);
}
