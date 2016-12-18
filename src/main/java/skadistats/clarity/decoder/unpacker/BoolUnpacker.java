package skadistats.clarity.decoder.unpacker;

import skadistats.clarity.decoder.bitstream.BitStream;

public class BoolUnpacker implements Unpacker<Boolean> {

    @Override
    public Boolean unpack(BitStream bs) {
        return bs.readBitFlag();
    }

    @Override
    public int getNeededMemorySize() {
        return 1;
    }

}
