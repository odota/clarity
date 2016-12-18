package skadistats.clarity.model;

import skadistats.clarity.model.state.AccessorFactory;
import skadistats.clarity.model.state.EntityState;

import java.util.List;

public interface DTClass extends AccessorFactory {

    String getDtName();

    int getClassId();
    void setClassId(int classId);

    Object[] getEmptyStateArray();

    String getNameForFieldPath(FieldPath fp);
    FieldPath getFieldPathForName(String property);

    <T> T getValueForFieldPath(FieldPath fp, Object[] state);

    List<FieldPath> collectFieldPaths(Object[] state);

    String dumpState(String title, EntityState state);

}

