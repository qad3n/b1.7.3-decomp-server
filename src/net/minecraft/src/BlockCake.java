package net.minecraft.src;

import java.util.Random;

public class BlockCake
extends Block {
    protected BlockCake(int id, int sprite) {
        super(id, sprite, Material.cakeMaterial);
        this.setTickOnLoad(true);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        float f = 0.0625f;
        float f2 = (float)(1 + n * 2) / 16.0f;
        float f3 = 0.5f;
        this.setBlockBounds(f2, 0.0f, f, 1.0f - f, f3, 1.0f - f);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        float f = 0.0625f;
        float f2 = (float)(1 + n * 2) / 16.0f;
        float f3 = 0.5f;
        return AxisAlignedBB.getBoundingBoxFromPool((float)x + f2, y, (float)z + f, (float)(x + 1) - f, (float)y + f3 - f, (float)(z + 1) - f);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 1) {
            return this.blockIndexInTexture;
        }
        if (face == 0) {
            return this.blockIndexInTexture + 3;
        }
        if (metadata > 0 && face == 4) {
            return this.blockIndexInTexture + 2;
        }
        return this.blockIndexInTexture + 1;
    }

    public int getBlockTextureFromSide(int face) {
        if (face == 1) {
            return this.blockIndexInTexture;
        }
        if (face == 0) {
            return this.blockIndexInTexture + 3;
        }
        return this.blockIndexInTexture + 1;
    }

    public boolean isACube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        this.eatCakeSlice(world, x, y, z, player);
        return true;
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.eatCakeSlice(world, x, y, z, player);
    }

    private void eatCakeSlice(World world, int x, int y, int z, EntityPlayer player) {
        if (player.health < 20) {
            player.heal(3);
            int n = world.getBlockMetadata(x, y, z) + 1;
            if (n >= 6) {
                world.setBlockWithNotify(x, y, z, 0);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, n);
                world.markBlockAsNeedsUpdate(x, y, z);
            }
        }
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
        return world.getBlockMaterial(x, y - 1, z).isSolid();
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public int idDropped(int metadata, Random random) {
        return 0;
    }
}

