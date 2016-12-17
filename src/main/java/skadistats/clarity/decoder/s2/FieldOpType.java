package skadistats.clarity.decoder.s2;

import skadistats.clarity.decoder.bitstream.BitStream;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.state.Cursor;

public enum FieldOpType {

    PlusOne(36271) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
        }
    },
    PlusTwo(10334) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += 2;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(2);
        }
    },
    PlusThree(1375) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += 3;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(3);
        }
    },
    PlusFour(646) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += 4;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(4);
        }
    },
    PlusN(4128) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVarFieldPath() + 5;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVarFieldPath() + 5);
        }
    },
    PushOneLeftDeltaZeroRightZero(35) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] = 0;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.push(0);
        }
    },
    PushOneLeftDeltaZeroRightNonZero(3) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] = bs.readUBitVarFieldPath();
        }
        public void applyTo(Cursor c, BitStream bs) {
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushOneLeftDeltaOneRightZero(521) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] = 0;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(0);
        }
    },
    PushOneLeftDeltaOneRightNonZero(2942) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] = bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushOneLeftDeltaNRightZero(560) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] = 0;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVarFieldPath());
            c.push(0);
        }
    },
    PushOneLeftDeltaNRightNonZero(471) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVarFieldPath() + 2;
            fp.path[++fp.last] = bs.readUBitVarFieldPath() + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVarFieldPath() + 2);
            c.push(bs.readUBitVarFieldPath() + 1);
        }
    },
    PushOneLeftDeltaNRightNonZeroPack6Bits(10530) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitInt(3) + 2;
            fp.path[++fp.last] = bs.readUBitInt(3) + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitInt(3) + 2);
            c.push(bs.readUBitInt(3) + 1);
        }
    },
    PushOneLeftDeltaNRightNonZeroPack8Bits(251) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitInt(4) + 2;
            fp.path[++fp.last] = bs.readUBitInt(4) + 1;
        }

        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitInt(4) + 2);
            c.push(bs.readUBitInt(4) + 1);
        }
    },
    PushTwoLeftDeltaZero(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushTwoPack5LeftDeltaZero(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] = bs.readUBitInt(5);
            fp.path[++fp.last] = bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushThreeLeftDeltaZero(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushThreePack5LeftDeltaZero(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[++fp.last] = bs.readUBitInt(5);
            fp.path[++fp.last] = bs.readUBitInt(5);
            fp.path[++fp.last] = bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushTwoLeftDeltaOne(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushTwoPack5LeftDeltaOne(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushThreeLeftDeltaOne(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushThreePack5LeftDeltaOne(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last]++;
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(1);
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushTwoLeftDeltaN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVar() + 2;
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVar() + 2);
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushTwoPack5LeftDeltaN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVar() + 2;
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVar() + 2);
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushThreeLeftDeltaN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVar() + 2;
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
            fp.path[++fp.last] += bs.readUBitVarFieldPath();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVar() + 2);
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
            c.push(bs.readUBitVarFieldPath());
        }
    },
    PushThreePack5LeftDeltaN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last] += bs.readUBitVar() + 2;
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
            fp.path[++fp.last] += bs.readUBitInt(5);
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.add(bs.readUBitVar() + 2);
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
            c.push(bs.readUBitInt(5));
        }
    },
    PushN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            int n = bs.readUBitVar();
            fp.path[fp.last] += bs.readUBitVar();
            for (int i = 0; i < n; i++) {
                fp.path[++fp.last] += bs.readUBitVarFieldPath();
            }
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            int n = bs.readUBitVar();
            c.add(bs.readUBitVar());
            for (int i = 0; i < n; i++) {
                c.push(bs.readUBitVarFieldPath());
            }
        }
    },
    PushNAndNonTopographical(310) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            for (int i = 0; i <= fp.last; i++) {
                if (bs.readBitFlag()) {
                    fp.path[i] += bs.readVarSInt() + 1;
                }
            }
            int c = bs.readUBitVar();
            for (int i = 0; i < c; i++) {
                fp.path[++fp.last] = bs.readUBitVarFieldPath();
            }
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            FieldPath fp = c.getFieldPath();
            execute(fp, bs);
            c.pop(c.getDepth());
            for (int i = 0; i <= fp.last; i++) {
                c.push(fp.path[i]);
            }
        }
    },
    PopOnePlusOne(2) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[--fp.last]++;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(1);
            c.add(1);
        }
    },
    PopOnePlusN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[--fp.last] += bs.readUBitVarFieldPath() + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(1);
            c.add(bs.readUBitVarFieldPath() + 1);
        }
    },
    PopAllButOnePlusOne(1837) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last = 0;
            fp.path[0]++;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(c.getDepth() - 1);
            c.add(1);
        }
    },
    PopAllButOnePlusN(149) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last = 0;
            fp.path[0] += bs.readUBitVarFieldPath() + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(c.getDepth() - 1);
            c.add(bs.readUBitVarFieldPath() + 1);
        }
    },
    PopAllButOnePlusNPack3Bits(300) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last = 0;
            fp.path[0] += bs.readUBitInt(3) + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(c.getDepth() - 1);
            c.add(bs.readUBitInt(3) + 1);
        }
    },
    PopAllButOnePlusNPack6Bits(634) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last = 0;
            fp.path[0] += bs.readUBitInt(6) + 1;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(c.getDepth() - 1);
            c.add(bs.readUBitInt(6) + 1);
        }
    },
    PopNPlusOne(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last -= bs.readUBitVarFieldPath();
            fp.path[fp.last]++;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(bs.readUBitVarFieldPath());
            c.add(1);
        }
    },
    PopNPlusN(0) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last -= bs.readUBitVarFieldPath();
            fp.path[fp.last] += bs.readVarSInt();
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            c.pop(bs.readUBitVarFieldPath());
            c.add(bs.readVarSInt());
        }
    },
    PopNAndNonTopographical(1) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.last -= bs.readUBitVarFieldPath();
            for (int i = 0; i <= fp.last; i++) {
                if (bs.readBitFlag()) {
                    fp.path[i] += bs.readVarSInt();
                }
            }
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            FieldPath fp = c.getFieldPath();
            execute(fp, bs);
            c.pop(c.getDepth());
            for (int i = 0; i <= fp.last; i++) {
                c.push(fp.path[i]);
            }
        }
    },
    NonTopoComplex(76) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            for (int i = 0; i <= fp.last; i++) {
                if (bs.readBitFlag()) {
                    fp.path[i] += bs.readVarSInt();
                }
            }
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            FieldPath fp = c.getFieldPath();
            execute(fp, bs);
            c.pop(c.getDepth());
            for (int i = 0; i <= fp.last; i++) {
                c.push(fp.path[i]);
            }
        }
    },
    NonTopoPenultimatePluseOne(271) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            fp.path[fp.last - 1]++;
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            FieldPath fp = c.getFieldPath();
            execute(fp, bs);
            c.pop(2);
            c.push(fp.path[fp.last - 1]);
            c.push(fp.path[fp.last]);
        }
    },
    NonTopoComplexPack4Bits(99) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
            for (int i = 0; i <= fp.last; i++) {
                if (bs.readBitFlag()) {
                    fp.path[i] += bs.readUBitInt(4) - 7;
                }
            }
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
            FieldPath fp = c.getFieldPath();
            execute(fp, bs);
            c.pop(c.getDepth());
            for (int i = 0; i <= fp.last; i++) {
                c.push(fp.path[i]);
            }
        }
    },
    FieldPathEncodeFinish(25474) {
        @Override
        public void execute(FieldPath fp, BitStream bs) {
        }
        @Override
        public void applyTo(Cursor c, BitStream bs) {
        }
    };

    private final int weight;

    FieldOpType(int weight) {
        this.weight = weight;
    }

    public abstract void execute(FieldPath fp, BitStream bs);

    public abstract void applyTo(Cursor c, BitStream bs);

    public int getWeight() {
        return weight;
    }

}
