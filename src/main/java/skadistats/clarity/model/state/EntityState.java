package skadistats.clarity.model.state;

import skadistats.clarity.model.FieldPath;

import java.util.List;

public interface EntityState {

    Cursor cursorForFieldPath(FieldPath fp);

    EntityState copy();

    List<DumpEntry> collectDump();
    List<FieldPath> collectFieldPaths();

}
