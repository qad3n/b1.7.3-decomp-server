package net.minecraft.src;

import java.util.Random;

public class BlockMushroom
extends BlockFlower {
    protected BlockMushroom(int n, int n2) {
        super(n, n2);
        float f = 0.2f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f * 2.0f, 0.5f + f);
        this.setTickOnLoad(true);
    }

    @Override
public void updateTick(World world, int x, int y, int z, Random random) {
    if (random.nextInt(100) == 0) {
        int i = x + random.nextInt(3) - 1;
        int j = y + random.nextInt(2) - random.nextInt(2);
        int k = z + random.nextInt(3) - 1;
        if (world.isAirBlock(i, j, k) && this.canBlockStay(world, i, j, k)) {
            x += random.nextInt(3) - 1;
            z += random.nextInt(3) - 1;
            if (world.isAirBlock(i, j, k) && this.canBlockStay(world, i, j, k)) {
                world.setBlockWithNotify(i, j, k, this.blockID);
            }
        }
    }
}

    protected boolean canThisPlantGrowOnThisBlockID(int block) {
        return Block.opaqueCubeLookup[block];
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        if (y < 0 || y >= 128) {
            return false;
        }
        return world.getBlockLightValueNoChecks(x, y, z) < 13 && this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }
}

