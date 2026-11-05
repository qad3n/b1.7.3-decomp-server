package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockDetectorRail
extends BlockRail {
    public BlockDetectorRail(int id, int sprite) {
        super(id, sprite, true);
        this.setTickOnLoad(true);
    }

    public int tickRate() {
        return 20;
    }

    public boolean canProvidePower() {
        return true;
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) != 0) {
            return;
        }
        this.updateOutputState(world, x, y, z, n);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) == 0) {
            return;
        }
        this.updateOutputState(world, x, y, z, n);
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        return (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        if ((world.getBlockMetadata(x, y, z) & 8) == 0) {
            return false;
        }
        return dir == 1;
    }

    private void updateOutputState(World world, int x, int y, int z, int metadata) {
        boolean bl = (metadata & 8) != 0;
        boolean bl2 = false;
        float f = 0.125f;
        List<EntityMinecart> list = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBoxFromPool((float)x + f, y, (float)z + f, (float)(x + 1) - f, (double)y + 0.25, (float)(z + 1) - f));
        if (list.size() > 0) {
            bl2 = true;
        }
        if (bl2 && !bl) {
            world.setBlockMetadataWithNotify(x, y, z, metadata | 8);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlocksDirty(x, y, z, x, y, z);
        }
        if (!bl2 && bl) {
            world.setBlockMetadataWithNotify(x, y, z, metadata & 7);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlocksDirty(x, y, z, x, y, z);
        }
        if (bl2) {
            world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        }
    }
}

