package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockRedstoneTorch
extends BlockTorch {
    private boolean torchActive = false;
    private static List<RedstoneUpdateInfo> torchUpdates = new ArrayList<RedstoneUpdateInfo>();

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 1) {
            return Block.redstoneWire.getBlockTextureFromSideAndMetadata(face, metadata);
        }
        return super.getBlockTextureFromSideAndMetadata(face, metadata);
    }

    private boolean checkForBurnout(World world, int x, int y, int z, boolean logToggle) {
        if (logToggle) {
            torchUpdates.add(new BlockRedstoneTorch.RedstoneUpdateInfo(x, y, z, world.getWorldTime()));
        }
        int i = 0;
        for (int j = 0; j < torchUpdates.size(); j++) {
            BlockRedstoneTorch.RedstoneUpdateInfo blockredstonetorch$redstoneupdateinfo = torchUpdates.get(j);
            if (blockredstonetorch$redstoneupdateinfo.x == x && blockredstonetorch$redstoneupdateinfo.y == y && blockredstonetorch$redstoneupdateinfo.z == z) {
                if (++i >= 8) {
                    return true;
                }
            }
        }
        return false;
    }

    protected BlockRedstoneTorch(int id, int sprite, boolean lit) {
        super(id, sprite);
        this.torchActive = lit;
        this.setTickOnLoad(true);
    }

    public int tickRate() {
        return 2;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            super.onBlockAdded(world, x, y, z);
        }
        if (this.torchActive) {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        }
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        if (this.torchActive) {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        }
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        if (!this.torchActive) {
            return false;
        }
        int n = world.getBlockMetadata(x, y, z);
        if (n == 5 && dir == 1) {
            return false;
        }
        if (n == 3 && dir == 3) {
            return false;
        }
        if (n == 4 && dir == 2) {
            return false;
        }
        if (n == 1 && dir == 5) {
            return false;
        }
        return n != 2 || dir != 4;
    }

    private boolean hasNeighborSignal(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        if (n == 5 && world.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0)) {
            return true;
        }
        if (n == 3 && world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2)) {
            return true;
        }
        if (n == 4 && world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3)) {
            return true;
        }
        if (n == 1 && world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4)) {
            return true;
        }
        return n == 2 && world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        boolean bl = this.hasNeighborSignal(world, x, y, z);
        while (torchUpdates.size() > 0 && world.getWorldTime() - BlockRedstoneTorch.torchUpdates.get((int)0).updateTime > 100L) {
            torchUpdates.remove(0);
        }
        if (this.torchActive) {
            if (bl) {
                world.setBlockAndMetadataWithNotify(x, y, z, Block.torchRedstoneIdle.blockID, world.getBlockMetadata(x, y, z));
                if (this.checkForBurnout(world, x, y, z, true)) {
                    world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, "random.fizz", 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
                    for (int i = 0; i < 5; ++i) {
                        double d = (double)x + random.nextDouble() * 0.6 + 0.2;
                        double d2 = (double)y + random.nextDouble() * 0.6 + 0.2;
                        double d3 = (double)z + random.nextDouble() * 0.6 + 0.2;
                        world.spawnParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
                    }
                }
            }
        } else if (!bl && !this.checkForBurnout(world, x, y, z, false)) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.torchRedstoneActive.blockID, world.getBlockMetadata(x, y, z));
        }
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        if (dir == 0) {
            return this.isPoweringTo(world, x, y, z, dir);
        }
        return false;
    }

    public int idDropped(int metadata, Random random) {
        return Block.torchRedstoneActive.blockID;
    }

    public boolean canProvidePower() {
        return true;
    }

    static class RedstoneUpdateInfo {
        int x;
        int y;
        int z;
        long updateTime;

        public RedstoneUpdateInfo(int x, int y, int z, long time) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.updateTime = time;
        }
    }
}

