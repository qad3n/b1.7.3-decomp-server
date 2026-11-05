package net.minecraft.src;

import java.util.Random;

public class WorldGenClay extends WorldGenerator {
    private int clayBlockId = Block.blockClay.blockID;
    private int numberOfBlocks;

    public WorldGenClay(int size) {
        this.numberOfBlocks = size;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockMaterial(x, y, z) != Material.water) {
            return false;
        }

        float f = random.nextFloat() * (float) Math.PI;
        double d0 = x + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
        double d1 = x + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
        double d2 = z + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
        double d3 = z + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
        double d4 = y + random.nextInt(3) + 2;
        double d5 = y + random.nextInt(3) + 2;

        for (int i = 0; i <= this.numberOfBlocks; i++) {
            double d6 = d0 + (d1 - d0) * i / this.numberOfBlocks;
            double d7 = d4 + (d5 - d4) * i / this.numberOfBlocks;
            double d8 = d2 + (d3 - d2) * i / this.numberOfBlocks;
            double d9 = random.nextDouble() * this.numberOfBlocks / 16.0;
            double d10 = (MathHelper.sin(i * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
            double d11 = (MathHelper.sin(i * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
            int j = MathHelper.floor_double(d6 - d10 / 2.0);
            int k = MathHelper.floor_double(d6 + d10 / 2.0);
            int l = MathHelper.floor_double(d7 - d11 / 2.0);
            int i1 = MathHelper.floor_double(d7 + d11 / 2.0);
            int j1 = MathHelper.floor_double(d8 - d10 / 2.0);
            int k1 = MathHelper.floor_double(d8 + d10 / 2.0);

            for (int l1 = j; l1 <= k; l1++) {
                for (int i2 = l; i2 <= i1; i2++) {
                    for (int j2 = j1; j2 <= k1; j2++) {
                        double d12 = (l1 + 0.5 - d6) / (d10 / 2.0);
                        double d13 = (i2 + 0.5 - d7) / (d11 / 2.0);
                        double d14 = (j2 + 0.5 - d8) / (d10 / 2.0);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0) {
                            int k2 = world.getBlockId(l1, i2, j2);
                            if (k2 == Block.sand.blockID) {
                                world.setBlock(l1, i2, j2, this.clayBlockId);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
