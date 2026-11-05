package net.minecraft.src;

import java.util.Random;

public class MapGenBase {
    protected int range = 8;
    protected Random rand = new Random();

    public void place(IChunkProvider source, World world, int chunkX, int chunkZ, byte[] blocks) {
        int n = this.range;
        this.rand.setSeed(world.getRandomSeed());
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        long l2 = this.rand.nextLong() / 2L * 2L + 1L;
        for (int i = chunkX - n; i <= chunkX + n; ++i) {
            for (int j = chunkZ - n; j <= chunkZ + n; ++j) {
                this.rand.setSeed((long)i * l + (long)j * l2 ^ world.getRandomSeed());
                this.place(world, i, j, chunkX, chunkZ, blocks);
            }
        }
    }

    protected void place(World world, int startChunkX, int startChunkZ, int chunkX, int chunkZ, byte[] blocks) {
    }
}

