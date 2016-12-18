package skadistats.clarity.model.state;

import skadistats.clarity.model.FieldPath;

public interface CursorGenerator {

    FieldPath getFieldPath();

    Cursor current();

    int getDepth();

    void push(int i);
    void pop(int n);
    void add(int i);

}
