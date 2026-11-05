package net.minecraft.src;

import java.util.Random;

public class BlockSapling
extends BlockFlower {
    protected BlockSapling(int n, int n2) {
        super(n, n2);
        float f = 0.4f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f * 2.0f, 0.5f + f);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.singleplayerWorld) {
            return;
        }
        super.updateTick(world, x, y, z, random);
        if (world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(30) == 0) {
            int n = world.getBlockMetadata(x, y, z);
            if ((n & 8) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, n | 8);
            } else {
                this.growTree(world, x, y, z, random);
            }
        }
    }

    @Override
public int getBlockTextureFromSideAndMetadata(final int face, int metadata) {
    metadata &= 0x3;
    if (metadata == 1) {
        return 63;
    }
    if (metadata == 2) {
        return 79;
    }
    return super.getBlockTextureFromSideAndMetadata(face, metadata);
}

    public void growTree(World world, int x, int y, int z, Random rand) {
        int n = world.getBlockMetadata(x, y, z) & 3;
        world.setBlock(x, y, z, 0);
        WorldGenerator worldGenerator = null;
        if (n == 1) {
            worldGenerator = new WorldGenTaiga2();
        } else if (n == 2) {
            worldGenerator = new WorldGenForest();
        } else {
            worldGenerator = new WorldGenTrees();
            if (rand.nextInt(10) == 0) {
                worldGenerator = new WorldGenBigTree();
            }
        }
        if (!((WorldGenerator)worldGenerator).generate(world, rand, x, y, z)) {
            world.setBlockAndMetadata(x, y, z, this.blockID, n);
        }
    }

    protected int damageDropped(int metadata) {
        return metadata & 3;
    }
}

