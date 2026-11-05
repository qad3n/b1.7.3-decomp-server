package net.minecraft.src;

import java.util.Random;

public class BlockTorch
extends Block {
    protected BlockTorch(int id, int sprite) {
        super(id, sprite, Material.circuits);
        this.setTickOnLoad(true);
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

    private boolean canSitOnTop(World world, int x, int y, int z) {
        return world.isBlockNormalCube(x, y, z) || world.getBlockId(x, y, z) == Block.fence.blockID;
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
        return this.canSitOnTop(world, x, y - 1, z);
    }

    public void onBlockPlaced(World world, int x, int y, int z, int face) {
        int n = world.getBlockMetadata(x, y, z);
        if (face == 1 && this.canSitOnTop(world, x, y - 1, z)) {
            n = 5;
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
        world.setBlockMetadataWithNotify(x, y, z, n);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlockMetadata(x, y, z) == 0) {
            this.onBlockAdded(world, x, y, z);
        }
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        if (world.isBlockNormalCube(x - 1, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 1);
        } else if (world.isBlockNormalCube(x + 1, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        } else if (world.isBlockNormalCube(x, y, z - 1)) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        } else if (world.isBlockNormalCube(x, y, z + 1)) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        } else if (this.canSitOnTop(world, x, y - 1, z)) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }
        this.dropTorchIfCantStay(world, x, y, z);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (this.dropTorchIfCantStay(world, x, y, z)) {
            int n = world.getBlockMetadata(x, y, z);
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
            if (!this.canSitOnTop(world, x, y - 1, z) && n == 5) {
                bl = true;
            }
            if (bl) {
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    private boolean dropTorchIfCantStay(World world, int x, int y, int z) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return false;
        }
        return true;
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D from, Vec3D to) {
        int n = world.getBlockMetadata(x, y, z) & 7;
        float f = 0.15f;
        if (n == 1) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
        } else if (n == 2) {
            this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
        } else if (n == 3) {
            this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
        } else if (n == 4) {
            this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
        } else {
            f = 0.1f;
            this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.6f, 0.5f + f);
        }
        return super.collisionRayTrace(world, x, y, z, from, to);
    }
}

