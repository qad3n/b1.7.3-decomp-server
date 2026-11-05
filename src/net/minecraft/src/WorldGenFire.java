package net.minecraft.src;

import java.util.Random;

public class WorldGenFire extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 64; i++) {
            int j = x + random.nextInt(8) - random.nextInt(8);
            int k = y + random.nextInt(4) - random.nextInt(4);
            int l = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(j, k, l) && world.getBlockId(j, k - 1, l) == Block.bloodStone.blockID) {
                world.setBlockWithNotify(j, k, l, Block.fire.blockID);
            }
        }

        return true;
    }
}
