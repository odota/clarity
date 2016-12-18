package skadistats.clarity.decoder.s2;

import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;
import skadistats.clarity.model.state.DumpEntry;
import skadistats.clarity.model.state.EntityState;
import skadistats.clarity.util.TextTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class S2DTClass implements DTClass {

    private final Serializer serializer;
    private int classId = -1;

    public S2DTClass(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int getClassId() {
        return classId;
    }

    @Override
    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Override
    public String getDtName() {
        return serializer.getId().getName();
    }

    @Override
    public Accessor getSubAccessor(int i) {
        return serializer.getSubAccessor(i);
    }

    @Override
    public Integer getSubStateLength() {
        return serializer.getSubStateLength();
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public String getNameForFieldPath(FieldPath fp) {
        List<String> parts = new ArrayList<>();
        serializer.accumulateName(fp, 0, parts);
        StringBuilder b = new StringBuilder();
        for (String part : parts) {
            if (b.length() != 0) {
                b.append('.');
            }
            b.append(part);
        }
        return b.toString();
    }

    @Override
    public FieldPath getFieldPathForName(String property) {
        FieldPath fp = new FieldPath();
        return serializer.getFieldPathForName(fp, property);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", serializer.getId(), classId);
    }

}
