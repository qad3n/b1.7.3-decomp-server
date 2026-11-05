package net.minecraft.src;

import java.util.Random;

public class BlockPistonMoving
extends BlockContainer {
    public BlockPistonMoving(int id) {
        super(id, Material.piston);
        this.setHardness(-1.0f);
    }

    protected TileEntity getBlockEntity() {
        return null;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileEntity).clearPistonTileEntity();
        } else {
            super.onBlockRemoval(world, x, y, z);
        }
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int face) {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (!world.singleplayerWorld && world.getBlockTileEntity(x, y, z) == null) {
            world.setBlockWithNotify(x, y, z, 0);
            return true;
        }
        return false;
    }

    public int idDropped(int metadata, Random random) {
        return 0;
    }

    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float luck) {
        if (world.singleplayerWorld) {
            return;
        }
        TileEntityPiston tileEntityPiston = this.getTileEntityAtLocation(world, x, y, z);
        if (tileEntityPiston == null) {
            return;
        }
        Block.blocksList[tileEntityPiston.getStoredBlockID()].dropBlockAsItem(world, x, y, z, tileEntityPiston.getBlockMetadata());
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (world.singleplayerWorld || world.getBlockTileEntity(x, y, z) == null) {
            // empty if block
        }
    }

    public static TileEntity getTileEntity(int movedBlock, int movedMetadata, int facing, boolean extending, boolean source) {
        return new TileEntityPiston(movedBlock, movedMetadata, facing, extending, source);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityPiston tileEntityPiston = this.getTileEntityAtLocation(world, x, y, z);
        if (tileEntityPiston == null) {
            return null;
        }
        float f = tileEntityPiston.getProgress(0.0f);
        if (tileEntityPiston.isExtending()) {
            f = 1.0f - f;
        }
        return this.getCollisionShape(world, x, y, z, tileEntityPiston.getStoredBlockID(), f, tileEntityPiston.getFacing());
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntityPiston tileEntityPiston = this.getTileEntityAtLocation(world, x, y, z);
        if (tileEntityPiston != null) {
            Block block = Block.blocksList[tileEntityPiston.getStoredBlockID()];
            if (block == null || block == this) {
                return;
            }
            block.setBlockBoundsBasedOnState(world, x, y, z);
            float f = tileEntityPiston.getProgress(0.0f);
            if (tileEntityPiston.isExtending()) {
                f = 1.0f - f;
            }
            int n = tileEntityPiston.getFacing();
            this.minX = block.minX - (double)((float)PistonBlockTextures.X_OFFSET[n] * f);
            this.minY = block.minY - (double)((float)PistonBlockTextures.Y_OFFSET[n] * f);
            this.minZ = block.minZ - (double)((float)PistonBlockTextures.Z_OFFSET[n] * f);
            this.maxX = block.maxX - (double)((float)PistonBlockTextures.X_OFFSET[n] * f);
            this.maxY = block.maxY - (double)((float)PistonBlockTextures.Y_OFFSET[n] * f);
            this.maxZ = block.maxZ - (double)((float)PistonBlockTextures.Z_OFFSET[n] * f);
        }
    }

    public AxisAlignedBB getCollisionShape(World world, int x, int y, int z, int movedBlock, float progress, int facing) {
        if (movedBlock == 0 || movedBlock == this.blockID) {
            return null;
        }
        AxisAlignedBB axisAlignedBB = Block.blocksList[movedBlock].getCollisionBoundingBoxFromPool(world, x, y, z);
        if (axisAlignedBB == null) {
            return null;
        }
        axisAlignedBB.minX -= (double)((float)PistonBlockTextures.X_OFFSET[facing] * progress);
        axisAlignedBB.maxX -= (double)((float)PistonBlockTextures.X_OFFSET[facing] * progress);
        axisAlignedBB.minY -= (double)((float)PistonBlockTextures.Y_OFFSET[facing] * progress);
        axisAlignedBB.maxY -= (double)((float)PistonBlockTextures.Y_OFFSET[facing] * progress);
        axisAlignedBB.minZ -= (double)((float)PistonBlockTextures.Z_OFFSET[facing] * progress);
        axisAlignedBB.maxZ -= (double)((float)PistonBlockTextures.Z_OFFSET[facing] * progress);
        return axisAlignedBB;
    }

    private TileEntityPiston getTileEntityAtLocation(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityPiston) {
            return (TileEntityPiston)tileEntity;
        }
        return null;
    }
}

