package net.minecraft.src;

import java.util.Random;

public class BlockCactus
extends Block {
    protected BlockCactus(int id, int sprite) {
        super(id, sprite, Material.cactus);
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

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float f = 0.0625f;
        return AxisAlignedBB.getBoundingBoxFromPool((float)x + f, y, (float)z + f, (float)(x + 1) - f, (float)(y + 1) - f, (float)(z + 1) - f);
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture - 1;
        }
        if (face == 0) {
            return this.blockIndexInTexture + 1;
        }
        return this.blockIndexInTexture;
    }

    public boolean isACube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (!super.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }
        return this.canBlockStay(world, x, y, z);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        if (world.getBlockMaterial(x - 1, y, z).isSolid()) {
            return false;
        }
        if (world.getBlockMaterial(x + 1, y, z).isSolid()) {
            return false;
        }
        if (world.getBlockMaterial(x, y, z - 1).isSolid()) {
            return false;
        }
        if (world.getBlockMaterial(x, y, z + 1).isSolid()) {
            return false;
        }
        int n = world.getBlockId(x, y - 1, z);
        return n == Block.cactus.blockID || n == Block.sand.blockID;
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.attackEntityFrom(null, 1);
    }
}

