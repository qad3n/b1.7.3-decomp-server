package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockPressurePlate extends Block {
    private BlockPressurePlate.EnumMobType triggerMobType;

    protected BlockPressurePlate(int id, int sprite, BlockPressurePlate.EnumMobType rule, Material material) {
        super(id, sprite, material);
        this.triggerMobType = rule;
        this.setTickOnLoad(true);
        float f = 0.0625F;
        this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
    }

    @Override
    public int tickRate() {
        return 20;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isACube() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isBlockNormalCube(x, y - 1, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        boolean flag = false;
        if (!world.isBlockNormalCube(x, y - 1, z)) {
            flag = true;
        }

        if (flag) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
public void updateTick(final World world, final int x, final int y, final int z, final Random random) {
    if (world.singleplayerWorld) {
        return;
    }
    if (world.getBlockMetadata(x, y, z) == 0) {
        return;
    }
    this.setStateIfMobInteractsWithPlate(world, x, y, z);
}

    @Override
public void onEntityCollidedWithBlock(final World world, final int x, final int y, final int z, final Entity entity) {
    if (world.singleplayerWorld) {
        return;
    }
    if (world.getBlockMetadata(x, y, z) == 1) {
        return;
    }
    this.setStateIfMobInteractsWithPlate(world, x, y, z);
}

    private void setStateIfMobInteractsWithPlate(World world, int x, int y, int z) {
        boolean flag = world.getBlockMetadata(x, y, z) == 1;
        boolean flag1 = false;
        float f = 0.125F;
        List list = null;
        if (this.triggerMobType == BlockPressurePlate.EnumMobType.everything) {
            list = world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBoxFromPool(x + f, y, z + f, x + 1 - f, y + 0.25, z + 1 - f));
        }

        if (this.triggerMobType == BlockPressurePlate.EnumMobType.mobs) {
            list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBoxFromPool(x + f, y, z + f, x + 1 - f, y + 0.25, z + 1 - f));
        }

        if (this.triggerMobType == BlockPressurePlate.EnumMobType.players) {
            list = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBoxFromPool(x + f, y, z + f, x + 1 - f, y + 0.25, z + 1 - f));
        }

        if (list.size() > 0) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            world.setBlockMetadataWithNotify(x, y, z, 1);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlocksDirty(x, y, z, x, y, z);
            world.playSoundEffect(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.6F);
        }

        if (!flag1 && flag) {
            world.setBlockMetadataWithNotify(x, y, z, 0);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlocksDirty(x, y, z, x, y, z);
            world.playSoundEffect(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.5F);
        }

        if (flag1) {
            world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        }
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        int i = world.getBlockMetadata(x, y, z);
        if (i > 0) {
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        }

        super.onBlockRemoval(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        boolean flag = world.getBlockMetadata(x, y, z) == 1;
        float f = 0.0625F;
        if (flag) {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        } else {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }
    }

    @Override
    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        return world.getBlockMetadata(x, y, z) > 0;
    }

    @Override
    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        return world.getBlockMetadata(x, y, z) == 0 ? false : dir == 1;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    public enum EnumMobType {
        everything,
        mobs,
        players;
    }
}
