package skadistats.clarity.model.state;

import skadistats.clarity.model.FieldPath;

public interface EntityState {

    Cursor cursorForFieldPath(FieldPath fp);
    Object[] getState();

    EntityState copy();

}
