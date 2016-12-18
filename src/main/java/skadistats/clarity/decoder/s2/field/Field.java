package skadistats.clarity.decoder.s2.field;

import skadistats.clarity.decoder.s2.DumpEntry;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.List;

public abstract class Field {

    protected final FieldProperties properties;

    public Field(FieldProperties properties) {
        this.properties = properties;
    }

    public abstract void accumulateName(FieldPath fp, int pos, List<String> parts);

    public abstract FieldPath getFieldPathForName(FieldPath fp, String property);
    public abstract void collectDump(FieldPath fp, String namePrefix, List<DumpEntry> entries, Object[] state);
    public abstract void collectFieldPaths(FieldPath fp, List<FieldPath> entries, Object[] state);

    public abstract Accessor getAccessor();

    protected void addBasePropertyName(List<String> parts) {
        parts.add(properties.getName());
    }

    protected String joinPropertyName(String... parts) {
        StringBuilder b = new StringBuilder();
        for (String part : parts) {
            if (b.length() != 0) {
                b.append('.');
            }
            b.append(part);
        }
        return b.toString();
    }

    public FieldProperties getProperties() {
        return properties;
    }

}
