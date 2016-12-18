package skadistats.clarity.model;

public class MutableFieldPath {

    public final int[] path;
    public int last;

    public MutableFieldPath() {
        path = new int[6];
        path[0] = -1;
        last = 0;
    }

    public FieldPath toImmutable() {
        return new ImmutableFieldPath(this);
    }

}
