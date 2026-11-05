package net.minecraft.src;


public class BlockLever
extends Block {
    protected BlockLever(int id, int sprite) {
        super(id, sprite, Material.circuits);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int face) {
        if (face == 1 && world.isBlockNormalCube(x, y - 1, z)) {
            return true;
        }
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
        if (world.isBlockNormalCube(x, y, z + 1)) {
            return true;
        }
        return world.isBlockNormalCube(x, y - 1, z);
    }

    public void onBlockPlaced(World world, int x, int y, int z, int face) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = n & 8;
        n &= 7;
        n = -1;
        if (face == 1 && world.isBlockNormalCube(x, y - 1, z)) {
            n = 5 + world.rand.nextInt(2);
        }
        if (face == 2 && world.isBlockNormalCube(x, y, z + 1)) {
            n = 4;
        }
        if (face == 3 && world.isBlockNormalCube(x, y, z - 1)) {
            n = 3;
        }
        if (face == 4 && world.isBlockNormalCube(x + 1, y, z)) {
            n = 2;
        }
        if (face == 5 && world.isBlockNormalCube(x - 1, y, z)) {
            n = 1;
        }
        if (n == -1) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return;
        }
        world.setBlockMetadataWithNotify(x, y, z, n + n2);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (this.checkIfAttachedToBlock(world, x, y, z)) {
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
            if (!world.isBlockNormalCube(x, y - 1, z) && n == 5) {
                bl = true;
            }
            if (!world.isBlockNormalCube(x, y - 1, z) && n == 6) {
                bl = true;
            }
            if (bl) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    private boolean checkIfAttachedToBlock(World world, int x, int y, int z) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return false;
        }
        return true;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z) & 7;
        float f = 0.1875f;
        if (n == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
        } else if (n == 2) {
            this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
        } else if (n == 3) {
            this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
        } else if (n == 4) {
            this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
        } else {
            f = 0.25f;
            this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.6f, 0.5f + f);
        }
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.blockActivated(world, x, y, z, player);
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (world.singleplayerWorld) {
            return true;
        }
        int n = world.getBlockMetadata(x, y, z);
        int n2 = n & 7;
        int n3 = 8 - (n & 8);
        world.setBlockMetadataWithNotify(x, y, z, n2 + n3);
        world.markBlocksDirty(x, y, z, x, y, z);
        world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.click", 0.3f, n3 > 0 ? 0.6f : 0.5f);
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
        if (n2 == 6 && dir == 1) {
            return true;
        }
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
}

