package skadistats.clarity.decoder.unpacker;

import skadistats.clarity.decoder.bitstream.BitStream;

public class LongVarUnsignedUnpacker implements Unpacker<Long> {

    @Override
    public Long unpack(BitStream bs) {
        return bs.readVarULong();
    }

    @Override
    public int getNeededMemorySize() {
        return 8;
    }

}
