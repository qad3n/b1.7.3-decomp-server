package net.minecraft.src;

import java.util.Random;

public class BlockCrops
extends BlockFlower {
    protected BlockCrops(int n, int n2) {
        super(n, n2);
        this.blockIndexInTexture = n2;
        this.setTickOnLoad(true);
        float f = 0.5f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.25f, 0.5f + f);
    }

    protected boolean canThisPlantGrowOnThisBlockID(int block) {
        return block == Block.tilledField.blockID;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        float f;
        int n;
        super.updateTick(world, x, y, z, random);
        if (world.getBlockLightValue(x, y + 1, z) >= 9 && (n = world.getBlockMetadata(x, y, z)) < 7 && random.nextInt((int)(100.0f / (f = this.getGrowthRate(world, x, y, z)))) == 0) {
            world.setBlockMetadataWithNotify(x, y, z, ++n);
        }
    }

    public void fertilize(World world, int x, int y, int z) {
        world.setBlockMetadataWithNotify(x, y, z, 7);
    }

    private float getGrowthRate(World world, int x, int y, int z) {
        float f = 1.0f;
        int n = world.getBlockId(x, y, z - 1);
        int n2 = world.getBlockId(x, y, z + 1);
        int n3 = world.getBlockId(x - 1, y, z);
        int n4 = world.getBlockId(x + 1, y, z);
        int n5 = world.getBlockId(x - 1, y, z - 1);
        int n6 = world.getBlockId(x + 1, y, z - 1);
        int n7 = world.getBlockId(x + 1, y, z + 1);
        int n8 = world.getBlockId(x - 1, y, z + 1);
        boolean bl = n3 == this.blockID || n4 == this.blockID;
        boolean bl2 = n == this.blockID || n2 == this.blockID;
        boolean bl3 = n5 == this.blockID || n6 == this.blockID || n7 == this.blockID || n8 == this.blockID;
        for (int i = x - 1; i <= x + 1; ++i) {
            for (int j = z - 1; j <= z + 1; ++j) {
                int n9 = world.getBlockId(i, y - 1, j);
                float f2 = 0.0f;
                if (n9 == Block.tilledField.blockID) {
                    f2 = 1.0f;
                    if (world.getBlockMetadata(i, y - 1, j) > 0) {
                        f2 = 3.0f;
                    }
                }
                if (i != x || j != z) {
                    f2 /= 4.0f;
                }
                f += f2;
            }
        }
        if (bl3 || bl && bl2) {
            f /= 2.0f;
        }
        return f;
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (metadata < 0) {
            metadata = 7;
        }
        return this.blockIndexInTexture + metadata;
    }

    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float luck) {
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, luck);
        if (world.singleplayerWorld) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            if (world.rand.nextInt(15) > metadata) continue;
            float f = 0.7f;
            float f2 = world.rand.nextFloat() * f + (1.0f - f) * 0.5f;
            float f3 = world.rand.nextFloat() * f + (1.0f - f) * 0.5f;
            float f4 = world.rand.nextFloat() * f + (1.0f - f) * 0.5f;
            EntityItem entityItem = new EntityItem(world, (float)x + f2, (float)y + f3, (float)z + f4, new ItemStack(Item.seeds));
            entityItem.delayBeforeCanPickup = 10;
            world.entityJoinedWorld(entityItem);
        }
    }

    public int idDropped(int metadata, Random random) {
        if (metadata == 7) {
            return Item.wheat.shiftedIndex;
        }
        return -1;
    }

    public int quantityDropped(Random random) {
        return 1;
    }
}

