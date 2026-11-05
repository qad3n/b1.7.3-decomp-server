package net.minecraft.src;

public class ChunkCoordIntPair {
    public final int chunkXPos;
    public final int chunkZPos;

    public ChunkCoordIntPair(int x, int z) {
        this.chunkXPos = x;
        this.chunkZPos = z;
    }

    public static int chunkXZ2Int(int chunkX, int chunkZ) {
        return (chunkX < 0 ? Integer.MIN_VALUE : 0) | (chunkX & Short.MAX_VALUE) << 16 | (chunkZ < 0 ? 32768 : 0) | chunkZ & Short.MAX_VALUE;
    }

    public int hashCode() {
        return ChunkCoordIntPair.chunkXZ2Int(this.chunkXPos, this.chunkZPos);
    }

    public boolean equals(Object object) {
        ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)object;
        return chunkCoordIntPair.chunkXPos == this.chunkXPos && chunkCoordIntPair.chunkZPos == this.chunkZPos;
    }
}

