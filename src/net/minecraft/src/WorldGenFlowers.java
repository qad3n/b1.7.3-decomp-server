package net.minecraft.src;

import java.util.Random;

public class WorldGenFlowers extends WorldGenerator {
    private int plantBlockId;

    public WorldGenFlowers(int flower) {
        this.plantBlockId = flower;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 64; i++) {
            int j = x + random.nextInt(8) - random.nextInt(8);
            int k = y + random.nextInt(4) - random.nextInt(4);
            int l = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(j, k, l) && ((BlockFlower)Block.blocksList[this.plantBlockId]).canBlockStay(world, j, k, l)) {
                world.setBlock(j, k, l, this.plantBlockId);
            }
        }

        return true;
    }
}
