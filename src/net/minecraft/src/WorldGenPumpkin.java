package net.minecraft.src;

import java.util.Random;

public class WorldGenPumpkin extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 64; i++) {
            int j = x + random.nextInt(8) - random.nextInt(8);
            int k = y + random.nextInt(4) - random.nextInt(4);
            int l = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(j, k, l) && world.getBlockId(j, k - 1, l) == Block.grass.blockID && Block.pumpkin.canPlaceBlockAt(world, j, k, l)) {
                world.setBlockAndMetadata(j, k, l, Block.pumpkin.blockID, random.nextInt(4));
            }
        }

        return true;
    }
}
