package net.minecraft.src;

import java.util.Random;

public class BlockButton
extends Block {
    protected BlockButton(int id, int sprite) {
        super(id, sprite, Material.circuits);
        this.setTickOnLoad(true);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public int tickRate() {
        return 20;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int face) {
        if (face == 2 && world.isBlockNormalCube(x, y, z + 1)) {
            return true;
        }
        if (face == 3 && world.isBlockNormalCube(x, y, z - 1)) {
            return true;
        }
        if (face == 4 && world.isBlockNormalCube(x + 1, y, z)) {
            return true;
        }
        return face == 5 && world.isBlockNormalCube(x - 1, y, z);
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

    @Override
public void onBlockPlaced(World world, int x, int y, int z, int face) {
    int i = world.getBlockMetadata(x, y, z);
    int j = i & 8;
    i &= 7;
    if (face == 2 && world.isBlockNormalCube(x, y, z + 1)) {
        i = 4;
    } else if (face == 3 && world.isBlockNormalCube(x, y, z - 1)) {
        i = 3;
    } else if (face == 4 && world.isBlockNormalCube(x + 1, y, z)) {
        i = 2;
    } else if (face == 5 && world.isBlockNormalCube(x - 1, y, z)) {
        i = 1;
    } else {
        i = this.getOrientation(world, x, y, z);
    }
    world.setBlockMetadataWithNotify(x, y, z, i + j);
}

    private int getOrientation(World world, int x, int y, int z) {
        if (world.isBlockNormalCube(x - 1, y, z)) {
            return 1;
        }
        if (world.isBlockNormalCube(x + 1, y, z)) {
            return 2;
        }
        if (world.isBlockNormalCube(x, y, z - 1)) {
            return 3;
        }
        if (world.isBlockNormalCube(x, y, z + 1)) {
            return 4;
        }
        return 1;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (this.canSurviveOrBreak(world, x, y, z)) {
            int n = world.getBlockMetadata(x, y, z) & 7;
            boolean bl = false;
            if (!world.isBlockNormalCube(x - 1, y, z) && n == 1) {
                bl = true;
            }
            if (!world.isBlockNormalCube(x + 1, y, z) && n == 2) {
                bl = true;
            }
            if (!world.isBlockNormalCube(x, y, z - 1) && n == 3) {
                bl = true;
            }
            if (!world.isBlockNormalCube(x, y, z + 1) && n == 4) {
                bl = true;
            }
            if (bl) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    private boolean canSurviveOrBreak(World world, int x, int y, int z) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return false;
        }
        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = n & 7;
        boolean bl = (n & 8) > 0;
        float f = 0.375f;
        float f2 = 0.625f;
        float f3 = 0.1875f;
        float f4 = 0.125f;
        if (bl) {
            f4 = 0.0625f;
        }
        if (n2 == 1) {
            this.setBlockBounds(0.0f, f, 0.5f - f3, f4, f2, 0.5f + f3);
        } else if (n2 == 2) {
            this.setBlockBounds(1.0f - f4, f, 0.5f - f3, 1.0f, f2, 0.5f + f3);
        } else if (n2 == 3) {
            this.setBlockBounds(0.5f - f3, f, 0.0f, 0.5f + f3, f2, f4);
        } else if (n2 == 4) {
            this.setBlockBounds(0.5f - f3, f, 1.0f - f4, 0.5f + f3, f2, 1.0f);
        }
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.blockActivated(world, x, y, z, player);
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = n & 7;
        int n3 = 8 - (n & 8);
        if (n3 == 0) {
            return true;
        }
        world.setBlockMetadataWithNotify(x, y, z, n2 + n3);
        world.markBlocksDirty(x, y, z, x, y, z);
        world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, 0.6f);
        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
        if (n2 == 1) {
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
        } else if (n2 == 2) {
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
        } else if (n2 == 3) {
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
        } else if (n2 == 4) {
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        } else {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        }
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        return true;
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) > 0) {
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            int n2 = n & 7;
            if (n2 == 1) {
                world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
            } else if (n2 == 2) {
                world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
            } else if (n2 == 3) {
                world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
            } else if (n2 == 4) {
                world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
            } else {
                world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            }
        }
        super.onBlockRemoval(world, x, y, z);
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        return (world.getBlockMetadata(x, y, z) & 8) > 0;
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) == 0) {
            return false;
        }
        int n2 = n & 7;
        if (n2 == 5 && dir == 1) {
            return true;
        }
        if (n2 == 4 && dir == 2) {
            return true;
        }
        if (n2 == 3 && dir == 3) {
            return true;
        }
        if (n2 == 2 && dir == 4) {
            return true;
        }
        return n2 == 1 && dir == 5;
    }

    public boolean canProvidePower() {
        return true;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) == 0) {
            return;
        }
        world.setBlockMetadataWithNotify(x, y, z, n & 7);
        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
        int n2 = n & 7;
        if (n2 == 1) {
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
        } else if (n2 == 2) {
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
        } else if (n2 == 3) {
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
        } else if (n2 == 4) {
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        } else {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        }
        world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, 0.5f);
        world.markBlocksDirty(x, y, z, x, y, z);
    }
}

