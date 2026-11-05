package net.minecraft.src;

import java.util.Random;

public class BlockLadder
extends Block {
    protected BlockLadder(int id, int sprite) {
        super(id, sprite, Material.circuits);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        float f = 0.125f;
        if (n == 2) {
            this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
        }
        if (n == 3) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
        }
        if (n == 4) {
            this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        if (n == 5) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (world.isBlockNormalCube(x - 1, y, z)) {
            return true;
        }
        if (world.isBlockNormalCube(x + 1, y, z)) {
            return true;
        }
        if (world.isBlockNormalCube(x, y, z - 1)) {
            return true;
        }
        return world.isBlockNormalCube(x, y, z + 1);
    }

    public void onBlockPlaced(World world, int x, int y, int z, int face) {
        int n = world.getBlockMetadata(x, y, z);
        if ((n == 0 || face == 2) && world.isBlockNormalCube(x, y, z + 1)) {
            n = 2;
        }
        if ((n == 0 || face == 3) && world.isBlockNormalCube(x, y, z - 1)) {
            n = 3;
        }
        if ((n == 0 || face == 4) && world.isBlockNormalCube(x + 1, y, z)) {
            n = 4;
        }
        if ((n == 0 || face == 5) && world.isBlockNormalCube(x - 1, y, z)) {
            n = 5;
        }
        world.setBlockMetadataWithNotify(x, y, z, n);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        int n = world.getBlockMetadata(x, y, z);
        boolean bl = false;
        if (n == 2 && world.isBlockNormalCube(x, y, z + 1)) {
            bl = true;
        }
        if (n == 3 && world.isBlockNormalCube(x, y, z - 1)) {
            bl = true;
        }
        if (n == 4 && world.isBlockNormalCube(x + 1, y, z)) {
            bl = true;
        }
        if (n == 5 && world.isBlockNormalCube(x - 1, y, z)) {
            bl = true;
        }
        if (!bl) {
            this.dropBlockAsItem(world, x, y, z, n);
            world.setBlockWithNotify(x, y, z, 0);
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
    }

    public int quantityDropped(Random random) {
        return 1;
    }
}

