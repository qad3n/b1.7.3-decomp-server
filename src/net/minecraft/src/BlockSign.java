package net.minecraft.src;

import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BlockSign
extends BlockContainer {
    private Class<? extends TileEntity> signEntityClass;
    private boolean isFreestanding;

    protected BlockSign(int id, Class<? extends TileEntity> blockEntityType, boolean standing) {
        super(id, Material.wood);
        this.isFreestanding = standing;
        this.blockIndexInTexture = 4;
        this.signEntityClass = blockEntityType;
        float f = 0.25f;
        float f2 = 1.0f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f2, 0.5f + f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        if (this.isFreestanding) {
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        float f = 0.28125f;
        float f2 = 0.78125f;
        float f3 = 0.0f;
        float f4 = 1.0f;
        float f5 = 0.125f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        if (n == 2) {
            this.setBlockBounds(f3, f, 1.0f - f5, f4, f2, 1.0f);
        }
        if (n == 3) {
            this.setBlockBounds(f3, f, 0.0f, f4, f2, f5);
        }
        if (n == 4) {
            this.setBlockBounds(1.0f - f5, f, f3, 1.0f, f2, f4);
        }
        if (n == 5) {
            this.setBlockBounds(0.0f, f, f3, f5, f2, f4);
        }
    }

    @Override
    public boolean isACube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    protected TileEntity getBlockEntity() {
        try {
            return this.signEntityClass.newInstance();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public int idDropped(int metadata, Random random) {
        return Item.sign.shiftedIndex;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        boolean bl = false;
        if (this.isFreestanding) {
            if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
                bl = true;
            }
        } else {
            int n = world.getBlockMetadata(x, y, z);
            bl = true;
            if (n == 2 && world.getBlockMaterial(x, y, z + 1).isSolid()) {
                bl = false;
            }
            if (n == 3 && world.getBlockMaterial(x, y, z - 1).isSolid()) {
                bl = false;
            }
            if (n == 4 && world.getBlockMaterial(x + 1, y, z).isSolid()) {
                bl = false;
            }
            if (n == 5 && world.getBlockMaterial(x - 1, y, z).isSolid()) {
                bl = false;
            }
        }
        if (bl) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
    }
}

