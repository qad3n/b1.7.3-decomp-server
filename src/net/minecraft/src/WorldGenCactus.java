package net.minecraft.src;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 10; i++) {
            int j = x + random.nextInt(8) - random.nextInt(8);
            int k = y + random.nextInt(4) - random.nextInt(4);
            int l = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(j, k, l)) {
                int i1 = 1 + random.nextInt(random.nextInt(3) + 1);

                for (int j1 = 0; j1 < i1; j1++) {
                    if (Block.cactus.canBlockStay(world, j, k + j1, l)) {
                        world.setBlock(j, k + j1, l, Block.cactus.blockID);
                    }
                }
            }
        }

        return true;
    }
}
