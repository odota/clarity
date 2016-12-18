package skadistats.clarity.model.state;

import skadistats.clarity.ClarityException;
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
        this.state = new Object[((S2DTClass) dtClass).getSerializer().getSubStateLength()];
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
            Object[] s = state;
            for (int n = 0; n <= fieldPath.last; n++) {
                Accessor a = accessor[n];
                Object si = s[fieldPath.path[n]];

                if (n == fieldPath.last) {
                    if (a.isPointer()) {
                        Boolean v = si != null;
                        return (T) v;
                    } else if (a.isVariableArray()) {
                        Integer v = si == null ? 0 : ((Object[]) si).length;
                        return (T) v;
                    } else {
                        return (T) si;
                    }
                } else {
                    if (a.isVariableArray()) {
                        si = ensureSubStateCapacity(s, fieldPath.path[n], fieldPath.path[n + 1] + 1, false);
                    } else if (si == null) {
                        si = new Object[a.getSubStateLength()];
                        s[fieldPath.path[n]] = si;
                    }
                }
                s = (Object[]) si;
            }
            throw new ClarityException("should not reach");
        }

        @Override
        public void setValue(Object data) {
            Object[] s = state;
            for (int n = 0; n <= fieldPath.last; n++) {
                Accessor a = accessor[n];
                Object si = s[fieldPath.path[n]];

                if (n == fieldPath.last) {
                    if (a.isPointer()) {
                        boolean b = (Boolean) data;
                        if (b & si == null) {
                            s[fieldPath.path[n]] = new Object[a.getSubStateLength()];
                        } else if (!b & si != null) {
                            s[fieldPath.path[n]] = null;
                        }
                    } else if (a.isVariableArray()) {
                        ensureSubStateCapacity(s, fieldPath.path[n], (Integer) data, true);
                    } else {
                        s[fieldPath.path[n]] = data;
                    }
                    return;
                } else {
                    if (a.isVariableArray()) {
                        si = ensureSubStateCapacity(s, fieldPath.path[n], fieldPath.path[n + 1] + 1, false);
                    } else if (si == null) {
                        si = new Object[a.getSubStateLength()];
                        s[fieldPath.path[n]] = si;
                    }
                }
                s = (Object[]) si;
            }
        }

        private Object[] ensureSubStateCapacity(Object[] state, int i, int wantedSize, boolean shrinkIfNeeded) {
            Object[] subState = (Object[]) state[i];
            int curSize = subState == null ? 0 : subState.length;
            if (subState == null && wantedSize > 0) {
                state[i] = new Object[wantedSize];
            } else if (shrinkIfNeeded && wantedSize == 0) {
                state[i] = null;
            } else if (wantedSize != curSize) {
                if (shrinkIfNeeded || wantedSize > curSize) {
                    state[i] = new Object[wantedSize];
                    curSize = wantedSize;
                    System.arraycopy(subState, 0, state[i], 0, Math.min(subState.length, curSize));
                }
            }
            return (Object[]) state[i];
        }


        @Override
        public FieldPath getFieldPath() {
            return fieldPath;
        }

    }

}
