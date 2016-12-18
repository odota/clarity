package skadistats.clarity.decoder.s2;

import skadistats.clarity.decoder.s2.field.Field;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;
import skadistats.clarity.model.state.AccessorFactory;

import java.util.List;

public class Serializer implements AccessorFactory, AddressLayoutable {

    private final SerializerId id;
    private final Field[] fields;
    private final int[] offsets;
    private final int sizeOf;

    public Serializer(SerializerId id, Field[] fields) {
        this.id = id;
        this.fields = fields;

        offsets = new int[fields.length];
        int o = 0;
        for (int i = 0; i < fields.length; i++) {
            offsets[i] = o;
            o += fields[i].sizeOf();
        }
        sizeOf = o;

    }

    public SerializerId getId() {
        return id;
    }

    public Field[] getFields() {
        return fields;
    }

    public void accumulateName(FieldPath fp, int pos, List<String> parts) {
        fields[fp.path[pos]].accumulateName(fp, pos + 1, parts);
    }

    private FieldPath getFieldPathForNameInternal(FieldPath fp, String property) {
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getProperties().getName();
            if (property.startsWith(fieldName)) {
                fp.path[fp.last] = i;
                if (property.length() == fieldName.length()) {
                    return fp;
                } else {
                    if (property.charAt(fieldName.length()) != '.') {
                        continue;
                    }
                    property = property.substring(fieldName.length() + 1);
                    fp.last++;
                    return field.getFieldPathForName(fp, property);
                }
            }
        }
        return null;
    }

    public FieldPath getFieldPathForName(FieldPath fp, String property) {
        return getFieldPathForNameInternal(fp, property);
    }

    @Override
    public Accessor getSubAccessor(int i) {
        return fields[i].getAccessor();
    }

    @Override
    public Integer getSubStateLength() {
        return fields.length;
    }

    @Override
    public int sizeOf() {
        return sizeOf;
    }

}
