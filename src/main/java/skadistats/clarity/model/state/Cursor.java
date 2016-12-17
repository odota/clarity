package skadistats.clarity.model.state;

public interface Cursor {
    <T> T getValue();

    void push(int i);
    void pop();
    void add(int i);

}
