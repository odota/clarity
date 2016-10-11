package skadistats.clarity.processor.entities;

public class EntityState {

    public final int clsId;
    public final int serial;
    public boolean active;
    public final Object[] propertyState;

    public EntityState(int clsId, int serial, boolean active, Object[] propertyState) {
        this.clsId = clsId;
        this.serial = serial;
        this.active = active;
        this.propertyState = propertyState;
    }

}
