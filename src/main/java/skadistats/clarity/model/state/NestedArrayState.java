package skadistats.clarity.model.state;

import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.S2DTClass;
import skadistats.clarity.decoder.s2.field.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;

public class NestedArrayState implements EntityState {

    private final DTClass dtClass;
    private Object[] state;

    public NestedArrayState(DTClass dtClass) {
        this.dtClass = dtClass;
    }

    @Override
    public EntityState copy() {
        NestedArrayState copy = new NestedArrayState(dtClass);
        copy.state = Util.clone(state);
        return copy;
    }

    @Override
    public Cursor cursorForFieldPath(FieldPath fp) {
        return new CursorImpl(fp);
    }

    @Override
    public Object[] getState() {
        return state;
    }

    public class CursorImpl implements Cursor {

        private final Accessor[] accessor;
        private final FieldPath fieldPath;

        private CursorImpl(FieldPath fp) {
            this.fieldPath = new FieldPath(fp);
            this.accessor = new Accessor[fp.last + 1];
            AccessorFactory factory = ((S2DTClass) dtClass).getSerializer();
            for (int i = 0; i <= fp.last; i++) {
                Accessor subAccessor = factory.getSubAccessor(fp.path[i]);
                accessor[i] = subAccessor;
                factory = subAccessor;
            }
        }

        @Override
        public Unpacker getUnpacker() {
            return accessor[fieldPath.last].getUnpacker();
        }

        @Override
        public FieldType getType() {
            return accessor[fieldPath.last].getType();
        }

        @Override
        public <T> T getValue() {
            // TODO
            return null;
        }

        @Override
        public void setValue(Object data) {
            // TODO
        }

        @Override
        public FieldPath getFieldPath() {
            return fieldPath;
        }

    }

}
