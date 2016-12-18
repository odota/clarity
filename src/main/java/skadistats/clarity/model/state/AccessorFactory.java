package skadistats.clarity.model.state;

public interface AccessorFactory {

    Accessor getSubAccessor(int i);
    Integer getSubStateLength();
    Integer getElementNum(int i);

}
