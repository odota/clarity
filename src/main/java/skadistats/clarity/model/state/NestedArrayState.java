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
    public CursorGenerator emptyCursor() {
        return new NestedArrayCursorGenerator();
    }

    @Override
    public Cursor cursorForFieldPath(final FieldPath fp) {
        NestedArrayCursorGenerator c = new NestedArrayCursorGenerator();
        c.add(fp.path[0] + 1);
        for (int i = 1; i <= fp.last; i++) {
            c.push(fp.path[i]);
        }
        return new NestedArrayCursor(c);
    }

    @Override
    public Object[] getState() {
        return state;
    }

    public class NestedArrayCursorGenerator implements CursorGenerator {

        private int SL;             // Stack Last
        private final Accessor[] F; // Accessor
        private final int[] FP;     // FieldPath

        private NestedArrayCursorGenerator() {
            SL = 0;
            F = new Accessor[6];
            FP = new int[6]; FP[0] = -1;
        }

        private NestedArrayCursorGenerator(NestedArrayCursorGenerator other) {
            SL = other.SL;
            F = new Accessor[6];
            FP = new int[6];
            System.arraycopy(other.F, 0, F, 0, SL + 1);
            System.arraycopy(other.FP, 0, FP, 0, SL + 1);
        }

        @Override
        public Cursor current() {
            return new NestedArrayCursor(this);
        }

        @Override
        public FieldPath getFieldPath() {
            return new FieldPath(FP, 0, SL + 1);
        }

        @Override
        public int getDepth() {
            return SL + 1;
        }

        @Override
        public void push(int i) {
            AccessorFactory factory = SL < 0 ? ((S2DTClass) dtClass).getSerializer() : F[SL];
            int n = SL + 1;
            F[n] = factory.getSubAccessor(i);
            FP[n] = i;
            SL = n;
        }

        @Override
        public void pop(int n) {
            while (n > 0) {
                F[SL] = null;
                SL--;
                n--;
            }
        }

        @Override
        public void add(int i) {
            AccessorFactory prev = SL == 0 ? ((S2DTClass) dtClass).getSerializer() : F[SL - 1];
            i += FP[SL];
            F[SL] = prev.getSubAccessor(i);
            FP[SL] = i;
        }

    }

    public class NestedArrayCursor implements Cursor {

        private int SL;             // Stack Last
        private final Accessor[] F; // Accessor
        private final int[] FP;     // FieldPath

        private NestedArrayCursor(NestedArrayCursorGenerator cg) {
            SL = cg.SL;
            F = new Accessor[6];
            FP = new int[6];
            System.arraycopy(cg.F, 0, F, 0, SL + 1);
            System.arraycopy(cg.FP, 0, FP, 0, SL + 1);
        }

        @Override
        public Unpacker getUnpacker() {
            return F[SL].getUnpacker();
        }

        @Override
        public FieldType getType() {
            return F[SL].getType();
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
            return new FieldPath(FP, 0, SL + 1);
        }

    }

}
