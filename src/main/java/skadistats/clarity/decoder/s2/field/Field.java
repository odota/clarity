package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.MutableFieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public abstract class Field {

    protected final FieldProperties properties;

    public Field(FieldProperties properties) {
        this.properties = properties;
    }

    public abstract Accessor getAccessor();

    public abstract void accumulateName(FieldPath fp, int pos, List<String> parts);
    public abstract FieldPath getFieldPathForName(MutableFieldPath fp, String property);

    protected void addBasePropertyName(List<String> parts) {
        parts.add(properties.getName());
    }

    public FieldProperties getProperties() {
        return properties;
    }

}
