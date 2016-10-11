package skadistats.clarity.processor.entities;

import skadistats.clarity.model.EngineType;

public class ClientFrame {

    final EntityState[] state;
    final int tick;

    public ClientFrame(EngineType engineType, int tick) {
        this.tick = tick;
        this.state = new EntityState[1 << engineType.getIndexBits()];
    }

    public int getTick() {
        return tick;
    }

    public EntityState copyState(ClientFrame otherFrame, int idx) {
        state[idx] = otherFrame != null ? otherFrame.state[idx] : null;
        return state[idx];
    }

    public EntityState cloneState(ClientFrame otherFrame, int idx) {
        EntityState otherState = otherFrame != null ? otherFrame.state[idx] : null;
        state[idx] = otherState != null ? otherState.copy() : null;
        return state[idx];
    }

    public void setState(EntityState entityState, int idx) {
        state[idx] = entityState;
    }

    public EntityState getState(int idx) {
        return state[idx];
    }

    public int getSize() {
        return state.length;
    }

}
