package net.minecraft.src;

public class NibbleArray {
    public final byte[] data;

    public NibbleArray(int size) {
        this.data = new byte[size >> 1];
    }

    public NibbleArray(byte[] data) {
        this.data = data;
    }

    public int getNibble(int x, int y, int z) {
        int n = x << 11 | z << 7 | y;
        int n2 = n >> 1;
        int n3 = n & 1;
        if (n3 == 0) {
            return this.data[n2] & 0xF;
        }
        return this.data[n2] >> 4 & 0xF;
    }

    public void setNibble(int x, int y, int z, int value) {
        int i = x << 11 | z << 7 | y;
        int j = i >> 1;
        int k = i & 1;
        if (k == 0) {
            this.data[j] = (byte)(this.data[j] & 240 | value & 15);
        } else {
            this.data[j] = (byte)(this.data[j] & 15 | (value & 15) << 4);
        }
    }

    public boolean isValid() {
        return this.data != null;
    }
}
