package net.minecraft.src;

public class ChunkPosition {
    public final int x;
    public final int y;
    public final int z;

    public ChunkPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object object) {
        if (object instanceof ChunkPosition) {
            ChunkPosition chunkPosition = (ChunkPosition)object;
            return chunkPosition.x == this.x && chunkPosition.y == this.y && chunkPosition.z == this.z;
        }
        return false;
    }

    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }
}

