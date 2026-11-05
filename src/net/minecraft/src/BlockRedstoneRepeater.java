package net.minecraft.src;

import java.util.Random;

public class BlockRedstoneRepeater
extends Block {
    public static final double[] RANDOM_DISPLAY = new double[]{-0.0625, 0.0625, 0.1875, 0.3125};
    private static final int[] DELAY_SETTINGS = new int[]{1, 2, 3, 4};
    private final boolean powered;

    protected BlockRedstoneRepeater(int id, boolean powered) {
        super(id, 6, Material.circuits);
        this.powered = powered;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
    }

    public boolean isACube() {
        return false;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (!world.isBlockNormalCube(x, y - 1, z)) {
            return false;
        }
        return super.canPlaceBlockAt(world, x, y, z);
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        if (!world.isBlockNormalCube(x, y - 1, z)) {
            return false;
        }
        return super.canBlockStay(world, x, y, z);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        int n = world.getBlockMetadata(x, y, z);
        boolean bl = this.shouldBePowered(world, x, y, z, n);
        if (this.powered && !bl) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.redstoneRepeaterIdle.blockID, n);
        } else if (!this.powered) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.redstoneRepeaterActive.blockID, n);
            if (!bl) {
                int n2 = (n & 0xC) >> 2;
                world.scheduleUpdateTick(x, y, z, Block.redstoneRepeaterActive.blockID, DELAY_SETTINGS[n2] * 2);
            }
        }
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 0) {
            if (this.powered) {
                return 99;
            }
            return 115;
        }
        if (face == 1) {
            if (this.powered) {
                return 147;
            }
            return 131;
        }
        return 5;
    }

    public int getBlockTextureFromSide(int face) {
        return this.getBlockTextureFromSideAndMetadata(face, 0);
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        return this.isPoweringTo(world, x, y, z, dir);
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        if (!this.powered) {
            return false;
        }
        int n = world.getBlockMetadata(x, y, z) & 3;
        if (n == 0 && dir == 3) {
            return true;
        }
        if (n == 1 && dir == 4) {
            return true;
        }
        if (n == 2 && dir == 2) {
            return true;
        }
        return n == 3 && dir == 5;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        boolean bl = this.shouldBePowered(world, x, y, z, n);
        int n2 = (n & 0xC) >> 2;
        if (this.powered && !bl) {
            world.scheduleUpdateTick(x, y, z, this.blockID, DELAY_SETTINGS[n2] * 2);
        } else if (!this.powered && bl) {
            world.scheduleUpdateTick(x, y, z, this.blockID, DELAY_SETTINGS[n2] * 2);
        }
    }

    private boolean shouldBePowered(World world, int x, int y, int z, int metadata) {
        int n = metadata & 3;
        switch (n) {
            case 0: {
                return world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3) || world.getBlockId(x, y, z + 1) == Block.redstoneWire.blockID && world.getBlockMetadata(x, y, z + 1) > 0;
            }
            case 2: {
                return world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2) || world.getBlockId(x, y, z - 1) == Block.redstoneWire.blockID && world.getBlockMetadata(x, y, z - 1) > 0;
            }
            case 3: {
                return world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5) || world.getBlockId(x + 1, y, z) == Block.redstoneWire.blockID && world.getBlockMetadata(x + 1, y, z) > 0;
            }
            case 1: {
                return world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4) || world.getBlockId(x - 1, y, z) == Block.redstoneWire.blockID && world.getBlockMetadata(x - 1, y, z) > 0;
            }
        }
        return false;
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = (n & 0xC) >> 2;
        n2 = n2 + 1 << 2 & 0xC;
        world.setBlockMetadataWithNotify(x, y, z, n2 | n & 3);
        return true;
    }

    public boolean canProvidePower() {
        return false;
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
        int n = ((MathHelper.floor_double((double)(entity.rotationYaw * 4.0f / 360.0f) + 0.5) & 3) + 2) % 4;
        world.setBlockMetadataWithNotify(x, y, z, n);
        boolean bl = this.shouldBePowered(world, x, y, z, n);
        if (bl) {
            world.scheduleUpdateTick(x, y, z, this.blockID, 1);
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int idDropped(int metadata, Random random) {
        return Item.redstoneRepeater.shiftedIndex;
    }
}

