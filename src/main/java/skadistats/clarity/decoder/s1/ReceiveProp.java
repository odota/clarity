package skadistats.clarity.decoder.s1;


import skadistats.clarity.decoder.bitstream.BitStream;
import skadistats.clarity.decoder.FieldType;
import skadistats.clarity.decoder.unpacker.Unpacker;
import skadistats.clarity.model.state.Accessor;

public class ReceiveProp {

    private final SendProp sendProp;
    private final String name;

    private final Accessor accessor;

    public ReceiveProp(final SendProp sendProp, String name) {
        this.sendProp = sendProp;
        this.name = name;

        accessor = new Accessor() {
            @Override
            public Unpacker getUnpacker() {
                return sendProp.getUnpacker();
            }

            @Override
            public FieldType getType() {
                return FieldType.forString(sendProp.getType().name());
            }
        };
    }

    public Accessor getAccessor() {
        return accessor;
    }

    public SendProp getSendProp() {
        return sendProp;
    }

    public String getVarName() {
        return name;
    }

    public Object decode(BitStream stream) {
        return sendProp.getUnpacker().unpack(stream);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReceiveProp [source=");
        builder.append(sendProp.getSrc());
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(sendProp.getType());
        builder.append(", prio=");
        builder.append(sendProp.getPriority());
        builder.append("]");
        return builder.toString();
    }

}
