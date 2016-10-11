package skadistats.clarity.model;

import skadistats.clarity.processor.entities.ClientFrame;
import skadistats.clarity.processor.entities.EntityState;
import skadistats.clarity.processor.sendtables.DTClasses;

public abstract class Entity {

    private final int index;
    private ClientFrame clientFrame;

    public Entity(int index) {
        this.index = index;
    }

    public void updateClientFrame(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }

    protected abstract EngineType getEngineType();
    protected abstract DTClasses getDtClasses();

    private EntityState entityState() {
        return clientFrame != null ? clientFrame.getState(index) : null;
    }

    public int getIndex() {
        return index;
    }

    public int getSerial() {
        return entityState() != null ? entityState().serial : -1;
    }

    public int getHandle() {
        return getEngineType().handleForIndexAndSerial(index, getSerial());
    }

    public DTClass getDtClass() {
        return entityState() != null ? getDtClasses().forClassId(entityState().clsId) : null;
    }

    public boolean isActive() {
        return entityState() != null ? entityState().active : false;
    }

    public boolean isValid() {
        return clientFrame != null;
    }

    public Object[] getState() {
        return entityState() != null ? entityState().propertyState : null;
    }

    /**
     * Check if this entity contains the given property.
     *
     * @param property Name of the property
     * @return True, if and only if the given property is present in this entity
     */
    public boolean hasProperty(String property) {
        return getDtClass().getFieldPathForName(property) != null;
    }

    /**
     * Check if this entity contains all of the given properties.
     *
     * @param properties Names of the properties
     * @return True, if and only if the given properties are present in this entity
     */
    public boolean hasProperties(String... properties) {
        for (String property : properties) {
            if (!hasProperty(property)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String property) {
        FieldPath fp = getDtClass().getFieldPathForName(property);
        if (fp == null) {
            throw new IllegalArgumentException(String.format("property %s not found on entity of class %s", property, getDtClass().getDtName()));
        }
        return getPropertyForFieldPath(fp);
    }

    public <T> T getPropertyForFieldPath(FieldPath fp) {
        return (T) getDtClass().getValueForFieldPath(fp, entityState().propertyState);
    }

    @Override
    public String toString() {
        String title = "idx: " + index + ", serial: " + getSerial() + ", class: " + getDtClass().getDtName();
        return getDtClass().dumpState(title, entityState().propertyState);
    }

}
