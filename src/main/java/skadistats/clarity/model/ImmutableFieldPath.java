package skadistats.clarity.model;

public class ImmutableFieldPath implements FieldPath, Comparable<ImmutableFieldPath> {

    private final int[] path;

    public ImmutableFieldPath(int... elements) {
        path = elements;
    }

    public ImmutableFieldPath(MutableFieldPath from) {
        path = new int[from.last + 1];
        System.arraycopy(from.path, 0, path, 0, path.length);
    }

    public int getElement(int i) {
        return path[i];
    }

    @Override
    public int getLast() {
        return path.length - 1;
    }

    @Override
    public int getLength() {
        return path.length;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            if (i != 0) {
                sb.append('/');
            }
            sb.append(path[i]);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableFieldPath fieldPath = (ImmutableFieldPath) o;
        if (path.length != fieldPath.path.length) return false;
        for (int i = 0; i < path.length; i++) {
            if (path[i] != fieldPath.path[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < path.length; i++) {
            result = 31 * result + path[i];
        }
        return result;
    }

    @Override
    public int compareTo(ImmutableFieldPath o) {
        if (this == o) return 0;
        int n = Math.min(path.length, o.path.length);
        for (int i = 0; i <= n; i++) {
            int r = Integer.compare(path[i], o.path[i]);
            if (r != 0) {
                return r;
            }
        }
        return Integer.compare(path.length, o.path.length);
    }

}
