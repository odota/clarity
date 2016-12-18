package skadistats.clarity.decoder.s2;

import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.MutableFieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public int getNeededMemorySize() {
        return serializer.getNeededMemorySize();
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
        return serializer.getFieldPathForName(new MutableFieldPath(), property);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", serializer.getId(), classId);
    }

}
