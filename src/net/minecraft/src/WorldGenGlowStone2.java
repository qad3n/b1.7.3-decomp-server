package net.minecraft.src;

import java.util.Random;

public class WorldGenGlowStone2 extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (!world.isAirBlock(x, y, z)) {
            return false;
        }

        if (world.getBlockId(x, y + 1, z) != Block.bloodStone.blockID) {
            return false;
        }

        world.setBlockWithNotify(x, y, z, Block.glowStone.blockID);

        for (int i = 0; i < 1500; i++) {
            int j = x + random.nextInt(8) - random.nextInt(8);
            int k = y - random.nextInt(12);
            int l = z + random.nextInt(8) - random.nextInt(8);
            if (world.getBlockId(j, k, l) == 0) {
                int i1 = 0;

                for (int j1 = 0; j1 < 6; j1++) {
                    int k1 = 0;
                    if (j1 == 0) {
                        k1 = world.getBlockId(j - 1, k, l);
                    }

                    if (j1 == 1) {
                        k1 = world.getBlockId(j + 1, k, l);
                    }

                    if (j1 == 2) {
                        k1 = world.getBlockId(j, k - 1, l);
                    }

                    if (j1 == 3) {
                        k1 = world.getBlockId(j, k + 1, l);
                    }

                    if (j1 == 4) {
                        k1 = world.getBlockId(j, k, l - 1);
                    }

                    if (j1 == 5) {
                        k1 = world.getBlockId(j, k, l + 1);
                    }

                    if (k1 == Block.glowStone.blockID) {
                        i1++;
                    }
                }

                if (i1 == 1) {
                    world.setBlockWithNotify(j, k, l, Block.glowStone.blockID);
                }
            }
        }

        return true;
    }
}
