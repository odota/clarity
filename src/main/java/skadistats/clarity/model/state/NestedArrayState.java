package skadistats.clarity.model.state;

import skadistats.clarity.decoder.s2.S2DTClass;
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
    public Cursor cursorForFieldPath(final FieldPath fp) {
        NestedArrayCursor c = new NestedArrayCursor();
        for (int i = 0; i <= fp.last; i++) {
            c.push(fp.path[i]);
        }
        return c;
    }

    @Override
    public Object[] getState() {
        return state;
    }

    public class NestedArrayCursor implements Cursor {

        private final Addressable[] field;
        private final Object[] state;
        private final int[] idx;
        private int last;

        public NestedArrayCursor() {
            field = new Addressable[7];
            state = new Object[7];
            idx = new int[7];
            field[0] = ((S2DTClass) dtClass).getSerializer();
            state[0] = NestedArrayState.this.state;
            last = 0;
        }

        @Override
        public <T> T getValue() {
            return (T) state[last];
        }

        @Override
        public void push(int i) {
            int next = last + 1;
            field[next] = field[last].getSubAddressable(i);
            state[next] = ((Object[]) state[last])[i];
            idx[next] = i;
            last = next;
        }

        @Override
        public void pop() {
            field[last] = null;
            state[last] = null;
            last--;
        }

        @Override
        public void add(int i) {
            i += idx[last];
            field[last] = field[last - 1].getSubAddressable(i);
            state[last] = ((Object[]) state[last - 1])[i];
            idx[last] = i;
        }
    }

}
