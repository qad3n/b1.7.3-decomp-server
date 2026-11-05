package net.minecraft.src;

import java.util.Random;

public class BlockDoor
extends Block {
    protected BlockDoor(int n, Material material) {
        super(n, material);
        this.blockIndexInTexture = 97;
        if (material == Material.iron) {
            ++this.blockIndexInTexture;
        }
        float f = 0.5f;
        float f2 = 1.0f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f2, 0.5f + f);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 0 || face == 1) {
            return this.blockIndexInTexture;
        }
        int n = this.getFacing(metadata);
        if ((n == 0 || n == 2) ^ face <= 3) {
            return this.blockIndexInTexture;
        }
        int n2 = n / 2 + (face & 1 ^ n);
        int n3 = this.blockIndexInTexture - (metadata & 8) * 2;
        if (((n2 += (metadata & 4) / 4) & 1) != 0) {
            n3 = -n3;
        }
        return n3;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        this.updateShape(this.getFacing(world.getBlockMetadata(x, y, z)));
    }

    public void updateShape(int facing) {
        float f = 0.1875f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f);
        if (facing == 0) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f);
        }
        if (facing == 1) {
            this.setBlockBounds(1.0f - f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        if (facing == 2) {
            this.setBlockBounds(0.0f, 0.0f, 1.0f - f, 1.0f, 1.0f, 1.0f);
        }
        if (facing == 3) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, f, 1.0f, 1.0f);
        }
    }

    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.blockActivated(world, x, y, z, player);
    }

    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        if (this.blockMaterial == Material.iron) {
            return true;
        }
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) != 0) {
            if (world.getBlockId(x, y - 1, z) == this.blockID) {
                this.blockActivated(world, x, y - 1, z, player);
            }
            return true;
        }
        if (world.getBlockId(x, y + 1, z) == this.blockID) {
            world.setBlockMetadataWithNotify(x, y + 1, z, (n ^ 4) + 8);
        }
        world.setBlockMetadataWithNotify(x, y, z, n ^ 4);
        world.markBlocksDirty(x, y - 1, z, x, y, z);
        world.doEvent(player, 1003, x, y, z, 0);
        return true;
    }

    public void updateOpenState(World world, int x, int y, int z, boolean open) {
        boolean bl;
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) != 0) {
            if (world.getBlockId(x, y - 1, z) == this.blockID) {
                this.updateOpenState(world, x, y - 1, z, open);
            }
            return;
        }
        boolean bl2 = bl = (world.getBlockMetadata(x, y, z) & 4) > 0;
        if (bl == open) {
            return;
        }
        if (world.getBlockId(x, y + 1, z) == this.blockID) {
            world.setBlockMetadataWithNotify(x, y + 1, z, (n ^ 4) + 8);
        }
        world.setBlockMetadataWithNotify(x, y, z, n ^ 4);
        world.markBlocksDirty(x, y - 1, z, x, y, z);
        world.doEvent(null, 1003, x, y, z, 0);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        int n = world.getBlockMetadata(x, y, z);
        if ((n & 8) != 0) {
            if (world.getBlockId(x, y - 1, z) != this.blockID) {
                world.setBlockWithNotify(x, y, z, 0);
            }
            if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower()) {
                this.onNeighborBlockChange(world, x, y - 1, z, neighborBlock);
            }
        } else {
            boolean bl = false;
            if (world.getBlockId(x, y + 1, z) != this.blockID) {
                world.setBlockWithNotify(x, y, z, 0);
                bl = true;
            }
            if (!world.isBlockNormalCube(x, y - 1, z)) {
                world.setBlockWithNotify(x, y, z, 0);
                bl = true;
                if (world.getBlockId(x, y + 1, z) == this.blockID) {
                    world.setBlockWithNotify(x, y + 1, z, 0);
                }
            }
            if (bl) {
                if (!world.singleplayerWorld) {
                    this.dropBlockAsItem(world, x, y, z, n);
                }
            } else if (neighborBlock > 0 && Block.blocksList[neighborBlock].canProvidePower()) {
                boolean bl2 = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
                this.updateOpenState(world, x, y, z, bl2);
            }
        }
    }

    public int idDropped(int metadata, Random random) {
        if ((metadata & 8) != 0) {
            return 0;
        }
        if (this.blockMaterial == Material.iron) {
            return Item.doorSteel.shiftedIndex;
        }
        return Item.doorWood.shiftedIndex;
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D from, Vec3D to) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, from, to);
    }

    public int getFacing(int metadata) {
        if ((metadata & 4) == 0) {
            return metadata - 1 & 3;
        }
        return metadata & 3;
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (y >= 127) {
            return false;
        }
        return world.isBlockNormalCube(x, y - 1, z) && super.canPlaceBlockAt(world, x, y, z) && super.canPlaceBlockAt(world, x, y + 1, z);
    }

    public static boolean getOpen(int metadata) {
        return (metadata & 4) != 0;
    }

    public int getMobilityFlag() {
        return 1;
    }
}

