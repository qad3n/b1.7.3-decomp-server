package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private RandomAccessFile dataFile;
    private final int[] offsets;
    private final int[] chunkTimestamps;
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0L;

    public RegionFile(File file) {
        this.offsets = new int[1024];
        this.chunkTimestamps = new int[1024];
        this.fileName = file;
        this.debugln("REGION LOAD " + this.fileName);
        this.sizeDelta = 0;

        try {
            if (file.exists()) {
                this.lastModified = file.lastModified();
            }

            this.dataFile = new RandomAccessFile(file, "rw");
            if (this.dataFile.length() < 4096L) {
                for (int i = 0; i < 1024; i++) {
                    this.dataFile.writeInt(0);
                }

                for (int i1 = 0; i1 < 1024; i1++) {
                    this.dataFile.writeInt(0);
                }

                this.sizeDelta += 8192;
            }

            if ((this.dataFile.length() & 4095L) != 0L) {
                for (int j1 = 0; j1 < (this.dataFile.length() & 4095L); j1++) {
                    this.dataFile.write(0);
                }
            }

            int k1 = (int)this.dataFile.length() / 4096;
            this.sectorFree = new ArrayList<>(k1);

            for (int j = 0; j < k1; j++) {
                this.sectorFree.add(true);
            }

            this.sectorFree.set(0, false);
            this.sectorFree.set(1, false);
            this.dataFile.seek(0L);

            for (int l1 = 0; l1 < 1024; l1++) {
                int k = this.dataFile.readInt();
                this.offsets[l1] = k;
                if (k != 0 && (k >> 8) + (k & 0xFF) <= this.sectorFree.size()) {
                    for (int l = 0; l < (k & 0xFF); l++) {
                        this.sectorFree.set((k >> 8) + l, false);
                    }
                }
            }

            for (int i2 = 0; i2 < 1024; i2++) {
                int j2 = this.dataFile.readInt();
                this.chunkTimestamps[i2] = j2;
            }
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public synchronized int getSizeDelta() {
        int i = this.sizeDelta;
        this.sizeDelta = 0;
        return i;
    }

    private void debug(String s) {
    }

    private void debugln(String s) {
        this.debug(s + "\n");
    }

    private void debug(String action, int chunkX, int chunkZ, String s) {
        this.debug("REGION " + action + " " + this.fileName.getName() + "[" + chunkX + "," + chunkZ + "] = " + s);
    }

    private void debug(String action, int chunkX, int chunkZ, int size, String s) {
        this.debug("REGION " + action + " " + this.fileName.getName() + "[" + chunkX + "," + chunkZ + "] " + size + "B = " + s);
    }

    private void debugln(String action, int chunkX, int chunkZ, String s) {
        this.debug(action, chunkX, chunkZ, s + "\n");
    }

    public synchronized DataInputStream getChunkDataInputStream(int chunkX, int chunkZ) {
        if (this.outOfBounds(chunkX, chunkZ)) {
            this.debugln("READ", chunkX, chunkZ, "out of bounds");
            return null;
        }
        try {
            int n = this.getOffset(chunkX, chunkZ);
            if (n == 0) {
                return null;
            }
            int n2 = n >> 8;
            int n3 = n & 0xFF;
            if (n2 + n3 > this.sectorFree.size()) {
                this.debugln("READ", chunkX, chunkZ, "invalid sector");
                return null;
            }
            this.dataFile.seek(n2 * 4096);
            int n4 = this.dataFile.readInt();
            if (n4 > 4096 * n3) {
                this.debugln("READ", chunkX, chunkZ, "invalid length: " + n4 + " > 4096 * " + n3);
                return null;
            }
            byte by = this.dataFile.readByte();
            if (by == 1) {
                byte[] byArray = new byte[n4 - 1];
                this.dataFile.read(byArray);
                DataInputStream dataInputStream = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(byArray)));
                return dataInputStream;
            }
            if (by == 2) {
                byte[] byArray = new byte[n4 - 1];
                this.dataFile.read(byArray);
                DataInputStream dataInputStream = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(byArray)));
                return dataInputStream;
            }
            this.debugln("READ", chunkX, chunkZ, "unknown version " + by);
            return null;
        } catch (IOException iOException) {
            this.debugln("READ", chunkX, chunkZ, "exception");
            return null;
        }
    }

    public DataOutputStream getChunkDataOutputStream(int chunkX, int chunkZ) {
        if (this.outOfBounds(chunkX, chunkZ)) {
            return null;
        }
        return new DataOutputStream(new DeflaterOutputStream(new RegionFileChunkBuffer(chunkX, chunkZ)));
    }

    protected synchronized void write(int chunkX, int chunkZ, byte[] data, int size) {
        try {
            int i = this.getOffset(chunkX, chunkZ);
            int j = i >> 8;
            int k = i & 0xFF;
            int l = (size + 5) / 4096 + 1;
            if (l >= 256) {
                return;
            }

            if (j != 0 && k == l) {
                this.debug("SAVE", chunkX, chunkZ, size, "rewrite");
                this.write(j, data, size);
            } else {
                for (int i1 = 0; i1 < k; i1++) {
                    this.sectorFree.set(j + i1, true);
                }

                int l1 = this.sectorFree.indexOf(true);
                int j1 = 0;
                if (l1 != -1) {
                    for (int k1 = l1; k1 < this.sectorFree.size(); k1++) {
                        if (j1 != 0) {
                            if (this.sectorFree.get(k1)) {
                                j1++;
                            } else {
                                j1 = 0;
                            }
                        } else if (this.sectorFree.get(k1)) {
                            l1 = k1;
                            j1 = 1;
                        }

                        if (j1 >= l) {
                            break;
                        }
                    }
                }

                if (j1 >= l) {
                    this.debug("SAVE", chunkX, chunkZ, size, "reuse");
                    j = l1;
                    this.setOffset(chunkX, chunkZ, j << 8 | l);

                    for (int j2 = 0; j2 < l; j2++) {
                        this.sectorFree.set(j + j2, false);
                    }

                    this.write(j, data, size);
                } else {
                    this.debug("SAVE", chunkX, chunkZ, size, "grow");
                    this.dataFile.seek(this.dataFile.length());
                    j = this.sectorFree.size();

                    for (int i2 = 0; i2 < l; i2++) {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(false);
                    }

                    this.sizeDelta += 4096 * l;
                    this.write(j, data, size);
                    this.setOffset(chunkX, chunkZ, j << 8 | l);
                }
            }

            this.setChunkTimestamp(chunkX, chunkZ, (int)(System.currentTimeMillis() / 1000L));
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private void write(int blockOffset, byte[] data, int size) throws IOException {
        this.debugln(" " + blockOffset);
        this.dataFile.seek(blockOffset * 4096);
        this.dataFile.writeInt(size + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(data, 0, size);
    }

    private boolean outOfBounds(int chunkX, int chunkZ) {
        return chunkX < 0 || chunkX >= 32 || chunkZ < 0 || chunkZ >= 32;
    }

    private int getOffset(int chunkX, int chunkZ) {
        return this.offsets[chunkX + chunkZ * 32];
    }

    public boolean isChunkSaved(int chunkX, int chunkZ) {
        return this.getOffset(chunkX, chunkZ) != 0;
    }

    private void setOffset(int chunkX, int chunkZ, int blockInfo) throws IOException {
        this.offsets[chunkX + chunkZ * 32] = blockInfo;
        this.dataFile.seek((chunkX + chunkZ * 32) * 4);
        this.dataFile.writeInt(blockInfo);
    }

    private void setChunkTimestamp(int chunkX, int chunkZ, int timeSeconds) throws IOException {
        this.chunkTimestamps[chunkX + chunkZ * 32] = timeSeconds;
        this.dataFile.seek(4096 + (chunkX + chunkZ * 32) * 4);
        this.dataFile.writeInt(timeSeconds);
    }

    public void close() throws IOException {
        this.dataFile.close();
    }

    class RegionFileChunkBuffer extends ByteArrayOutputStream {
        private int chunkX;
        private int chunkZ;

        public RegionFileChunkBuffer(int chunkX, int chunkZ) {
            super(8096);
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public void close() {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}
