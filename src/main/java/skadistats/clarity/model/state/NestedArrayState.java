package skadistats.clarity.model.state;

import skadistats.clarity.ClarityException;
import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.Util;
import skadistats.clarity.decoder.s2.field.FieldProperties;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;

import java.util.ArrayList;
import java.util.List;

public class NestedArrayState implements EntityState {

    private final DTClass dtClass;
    private Object[] state;

    NestedArrayState(DTClass dtClass) {
        this.dtClass = dtClass;
        this.state = new Object[dtClass.getSubStateLength()];
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
    public List<DumpEntry> collectDump() {
        List<DumpEntry> result = new ArrayList<>();
        dumpInternal(dtClass, state, new FieldPath(), "", result);
        return result;
    }

    private void dumpInternal(AccessorFactory factory, Object[] state, FieldPath fp, String namePrefix, List<DumpEntry> result) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == null) continue;

            Accessor subAccessor = factory.getSubAccessor(i);
            fp.path[fp.last] = i;
            if (state[i] instanceof Object[]) {
                fp.last++;
                dumpInternal(
                        subAccessor,
                        (Object[]) state[i],
                        fp,
                        namePrefix + subAccessor.getNameSegment(i) + ".",
                        result
                );
                fp.last--;
            } else {
                result.add(new DumpEntry(
                        fp,
                        namePrefix + subAccessor.getNameSegment(i),
                        state[i]
                ));
            }
        }
    }

    @Override
    public List<FieldPath> collectFieldPaths() {
        List<FieldPath> result = new ArrayList<>();
        collectInternal(dtClass, state, new FieldPath(), result);
        return result;
    }

    private void collectInternal(AccessorFactory factory, Object[] state, FieldPath fp, List<FieldPath> result) {
        for (int i = 0; i < state.length; i++) {
            if (state[i] == null) continue;

            Accessor subAccessor = factory.getSubAccessor(i);
            fp.path[fp.last] = i;
            if (state[i] instanceof Object[]) {
                fp.last++;
                collectInternal(
                        subAccessor,
                        (Object[]) state[i],
                        fp,
                        result
                );
                fp.last--;
            } else {
                result.add(new FieldPath(fp));
            }
        }
    }

    public class CursorImpl implements Cursor {

        private final Accessor[] accessor;
        private final FieldPath fieldPath;

        private CursorImpl(FieldPath fp) {
            this.fieldPath = new FieldPath(fp);
            this.accessor = new Accessor[fp.last + 1];
            AccessorFactory factory = dtClass;
            for (int i = 0; i <= fp.last; i++) {
                Accessor subAccessor = factory.getSubAccessor(fp.path[i]);
                accessor[i] = subAccessor;
                factory = subAccessor;
            }
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
        public String getPropertyName() {
            StringBuilder b = new StringBuilder();
            for (int n = 0; n <= fieldPath.last; n++) {
                Accessor a = accessor[n];
                if (b.length() != 0) {
                    b.append('.');
                }
                b.append(a.getNameSegment(fieldPath.path[n]));
            }
            return b.toString();
        }

        @Override
        public FieldPath getFieldPath() {
            return fieldPath;
        }

        @Override
        public Unpacker getUnpacker() {
            return accessor[fieldPath.last].getUnpacker();
        }

        @Override
        public FieldType getFieldType() {
            return accessor[fieldPath.last].getType();
        }

        @Override
        public FieldProperties getFieldProperties() {
            return accessor[fieldPath.last].getFieldProperties();
        }

    }

}
