package net.minecraft.src;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChunkCoordinates
implements Comparable<ChunkCoordinates> {
    public int posX;
    public int posY;
    public int posZ;

    public ChunkCoordinates() {
    }

    public ChunkCoordinates(int x, int y, int z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
    }

    public ChunkCoordinates(ChunkCoordinates pos) {
        this.posX = pos.posX;
        this.posY = pos.posY;
        this.posZ = pos.posZ;
    }

    public boolean equals(Object object) {
        if (!(object instanceof ChunkCoordinates)) {
            return false;
        }
        ChunkCoordinates chunkCoordinates = (ChunkCoordinates)object;
        return this.posX == chunkCoordinates.posX && this.posY == chunkCoordinates.posY && this.posZ == chunkCoordinates.posZ;
    }

    public int hashCode() {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    @Override
    public int compareTo(ChunkCoordinates chunkCoordinates) {
        if (this.posY == chunkCoordinates.posY) {
            if (this.posZ == chunkCoordinates.posZ) {
                return this.posX - chunkCoordinates.posX;
            }
            return this.posZ - chunkCoordinates.posZ;
        }
        return this.posY - chunkCoordinates.posY;
    }

    public double getSqDistanceTo(int x, int y, int z) {
        int n = this.posX - x;
        int n2 = this.posY - y;
        int n3 = this.posZ - z;
        return Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
}

