package skadistats.clarity.model.state;

import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.S2DTClass;
import skadistats.clarity.decoder.s2.field.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;

public class NestedArrayState implements EntityState {

    private final DTClass dtClass;
    private final Object[] state;

    public NestedArrayState(DTClass dtClass, Object[] state) {
        this.dtClass = dtClass;
        this.state = state;
    }

    @Override
    public EntityState copy() {
        return new NestedArrayState(
                dtClass,
                Util.clone(state)
        );
    }

    @Override
    public Cursor emptyCursor() {
        return new NestedArrayCursor();
    }

    @Override
    public Cursor cursorForFieldPath(final FieldPath fp) {
        NestedArrayCursor c = new NestedArrayCursor();
        c.add(fp.path[0] + 1);
        for (int i = 1; i <= fp.last; i++) {
            c.push(fp.path[i]);
        }
        return c;
    }

    @Override
    public Object[] getState() {
        return state;
    }

    public class NestedArrayCursor implements Cursor {

        private final Accessor[] field;
        private final int[] idx;
        private int last;

        private NestedArrayCursor() {
            field = new Accessor[6];
            idx = new int[6];
            idx[0] = -1;
            last = 0;
        }

        private NestedArrayCursor(NestedArrayCursor other) {
            field = new Accessor[6];
            idx = new int[6];
            last = other.last;
            System.arraycopy(other.field, 0, field, 0, last + 1);
            System.arraycopy(other.idx, 0, idx, 0, last + 1);
        }

        @Override
        public Cursor copy() {
            return new NestedArrayCursor(this);
        }

        @Override
        public FieldPath getFieldPath() {
            return new FieldPath(idx, 0, last + 1);
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
        public int getDepth() {
            return last + 1;
        }

        @Override
        public void push(int i) {
            AccessorFactory cur = last < 0 ? ((S2DTClass) dtClass).getSerializer() : field[last];
            int next = last + 1;
            field[next] = cur.getAccessor(i);
            idx[next] = i;
            last = next;
        }

        @Override
        public void pop(int n) {
            while (n > 0) {
                field[last] = null;
                last--;
                n--;
            }
        }

        @Override
        public void add(int i) {
            AccessorFactory prev = last == 0 ? ((S2DTClass) dtClass).getSerializer() : field[last - 1];
            i += idx[last];
            field[last] = prev.getAccessor(i);
            idx[last] = i;
        }

        @Override
        public Unpacker getUnpacker() {
            return field[last].getUnpacker();
        }

        @Override
        public FieldType getType() {
            return field[last].getType();
        }

    }



}
