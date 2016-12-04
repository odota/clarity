package skadistats.clarity.model.state;

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
        return new Cursor() {
            @Override
            public <T> T getValue() {
                return dtClass.getValueForFieldPath(fp, state);
            }
        };
    }

    @Override
    public Object[] getState() {
        return state;
    }

}
