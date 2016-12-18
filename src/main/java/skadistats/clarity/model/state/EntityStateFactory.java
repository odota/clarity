package skadistats.clarity.model.state;

import skadistats.clarity.model.DTClass;

public class EntityStateFactory {

    public static EntityState createForClass(DTClass dtClass) {
        return new NestedArrayState(dtClass);
    }

}
