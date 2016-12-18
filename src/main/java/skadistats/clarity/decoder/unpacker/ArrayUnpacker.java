package skadistats.clarity.decoder.unpacker;

import skadistats.clarity.ClarityException;
import skadistats.clarity.decoder.bitstream.BitStream;

public class ArrayUnpacker<T> implements Unpacker<T[]> {

    private static final int MAX_LEN = 64;

    private final Unpacker<T> unpacker;
    private final int nSizeBits;

    public ArrayUnpacker(Unpacker<T> unpacker, int nSizeBits) {
        this.unpacker = unpacker;
        this.nSizeBits = nSizeBits;
    }

    @Override
    public T[] unpack(BitStream bs) {
        int count = bs.readUBitInt(nSizeBits);
        if (count > MAX_LEN) {
            throw new ClarityException("cannot hold %d elements, max is %d", count, MAX_LEN);
        }
        T[] result = (T[]) new Object[count];
        int i = 0;
        while (i < count) {
            result[i++] = unpacker.unpack(bs);
        }
        return result;
    }

    @Override
    public int getNeededMemorySize() {
        return MAX_LEN * unpacker.getNeededMemorySize();
    }

}
