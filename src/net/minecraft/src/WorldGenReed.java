package net.minecraft.src;

import java.util.Random;

public class WorldGenReed extends WorldGenerator {
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        for (int i = 0; i < 20; i++) {
            int j = x + random.nextInt(4) - random.nextInt(4);
            int k = y;
            int l = z + random.nextInt(4) - random.nextInt(4);
            if (world.isAirBlock(j, k, l)
                && (
                    world.getBlockMaterial(j - 1, k - 1, l) == Material.water
                        || world.getBlockMaterial(j + 1, k - 1, l) == Material.water
                        || world.getBlockMaterial(j, k - 1, l - 1) == Material.water
                        || world.getBlockMaterial(j, k - 1, l + 1) == Material.water
                )) {
                int i1 = 2 + random.nextInt(random.nextInt(3) + 1);

                for (int j1 = 0; j1 < i1; j1++) {
                    if (Block.reed.canBlockStay(world, j, k + j1, l)) {
                        world.setBlock(j, k + j1, l, Block.reed.blockID);
                    }
                }
            }
        }

        return true;
    }
}
