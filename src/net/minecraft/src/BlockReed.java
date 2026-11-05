package net.minecraft.src;

import java.util.Random;

public class BlockReed
extends Block {
    protected BlockReed(int id, int sprite) {
        super(id, Material.plants);
        this.blockIndexInTexture = sprite;
        float f = 0.375f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 1.0f, 0.5f + f);
        this.setTickOnLoad(true);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.isAirBlock(x, y + 1, z)) {
            int n = 1;
            while (world.getBlockId(x, y - n, z) == this.blockID) {
                ++n;
            }
            if (n < 3) {
                int n2 = world.getBlockMetadata(x, y, z);
                if (n2 == 15) {
                    world.setBlockWithNotify(x, y + 1, z, this.blockID);
                    world.setBlockMetadataWithNotify(x, y, z, 0);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, n2 + 1);
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y - 1, z);
        if (n == this.blockID) {
            return true;
        }
        if (n != Block.grass.blockID && n != Block.dirt.blockID) {
            return false;
        }
        if (world.getBlockMaterial(x - 1, y - 1, z) == Material.water) {
            return true;
        }
        if (world.getBlockMaterial(x + 1, y - 1, z) == Material.water) {
            return true;
        }
        if (world.getBlockMaterial(x, y - 1, z - 1) == Material.water) {
            return true;
        }
        return world.getBlockMaterial(x, y - 1, z + 1) == Material.water;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        this.checkBlockCoordValid(world, x, y, z);
    }

    protected final void checkBlockCoordValid(World world, int x, int y, int z) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        return this.canPlaceBlockAt(world, x, y, z);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public int idDropped(int metadata, Random random) {
        return Item.reed.shiftedIndex;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }
}

