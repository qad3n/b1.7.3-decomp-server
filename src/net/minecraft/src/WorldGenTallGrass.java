package net.minecraft.src;

import java.util.Random;

public class WorldGenTallGrass extends WorldGenerator {
    private int plant;
    private int variant;

    public WorldGenTallGrass(int plant, int variant) {
        this.plant = plant;
        this.variant = variant;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int i = 0;

        while (((i = world.getBlockId(x, y, z)) == 0 || i == Block.leaves.blockID) && y > 0) {
            y--;
        }

        for (int j = 0; j < 128; j++) {
            int k = x + random.nextInt(8) - random.nextInt(8);
            int l = y + random.nextInt(4) - random.nextInt(4);
            int i1 = z + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(k, l, i1) && ((BlockFlower)Block.blocksList[this.plant]).canBlockStay(world, k, l, i1)) {
                world.setBlockAndMetadata(k, l, i1, this.plant, this.variant);
            }
        }

        return true;
    }
}
