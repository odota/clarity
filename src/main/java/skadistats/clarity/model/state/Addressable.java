package skadistats.clarity.model.state;

public interface Addressable {

    Addressable LAST = new Addressable() {
        @Override
        public Addressable getSubAddressable(int i) {
            throw new UnsupportedOperationException();
        }
    };

    Addressable getSubAddressable(int i);

}
