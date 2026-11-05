package net.minecraft.src;

import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BlockPistonBase
extends Block {
    private boolean isSticky;
    private boolean ignoreUpdates;

    public BlockPistonBase(int id, int sprite, boolean sticky) {
        super(id, sprite, Material.piston);
        this.isSticky = sticky;
        this.setStepSound(soundStoneFootstep);
        this.setHardness(0.5f);
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        int n = BlockPistonBase.getOrientation(metadata);
        if (n > 5) {
            return this.blockIndexInTexture;
        }
        if (face == n) {
            if (BlockPistonBase.isExtended(metadata) || this.minX > 0.0 || this.minY > 0.0 || this.minZ > 0.0 || this.maxX < 1.0 || this.maxY < 1.0 || this.maxZ < 1.0) {
                return 110;
            }
            return this.blockIndexInTexture;
        }
        if (face == PistonBlockTextures.OPPOSITE[n]) {
            return 109;
        }
        return 108;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
        int n = BlockPistonBase.determineOrientation(world, x, y, z, (EntityPlayer)entity);
        world.setBlockMetadataWithNotify(x, y, z, n);
        if (!world.singleplayerWorld) {
            this.updatePistonState(world, x, y, z);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (!world.singleplayerWorld && !this.ignoreUpdates) {
            this.updatePistonState(world, x, y, z);
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.singleplayerWorld && world.getBlockTileEntity(x, y, z) == null) {
            this.updatePistonState(world, x, y, z);
        }
    }

    private void updatePistonState(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        int n2 = BlockPistonBase.getOrientation(n);
        boolean bl = this.isPowered(world, x, y, z, n2);
        if (n == 7) {
            return;
        }
        if (bl && !BlockPistonBase.isExtended(n)) {
            if (BlockPistonBase.canExtend(world, x, y, z, n2)) {
                world.setBlockMetadata(x, y, z, n2 | 8);
                world.playNoteAt(x, y, z, 0, n2);
            }
        } else if (!bl && BlockPistonBase.isExtended(n)) {
            world.setBlockMetadata(x, y, z, n2);
            world.playNoteAt(x, y, z, 1, n2);
        }
    }

    private boolean isPowered(World world, int x, int y, int z, int facing) {
        if (facing != 0 && world.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0)) {
            return true;
        }
        if (facing != 1 && world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z, 1)) {
            return true;
        }
        if (facing != 2 && world.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2)) {
            return true;
        }
        if (facing != 3 && world.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3)) {
            return true;
        }
        if (facing != 5 && world.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5)) {
            return true;
        }
        if (facing != 4 && world.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4)) {
            return true;
        }
        if (world.isBlockIndirectlyProvidingPowerTo(x, y, z, 0)) {
            return true;
        }
        if (world.isBlockIndirectlyProvidingPowerTo(x, y + 2, z, 1)) {
            return true;
        }
        if (world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z - 1, 2)) {
            return true;
        }
        if (world.isBlockIndirectlyProvidingPowerTo(x, y + 1, z + 1, 3)) {
            return true;
        }
        if (world.isBlockIndirectlyProvidingPowerTo(x - 1, y + 1, z, 4)) {
            return true;
        }
        return world.isBlockIndirectlyProvidingPowerTo(x + 1, y + 1, z, 5);
    }

    @Override
    public void playBlock(World world, int x, int y, int z, int type, int data) {
        this.ignoreUpdates = true;
        int n = data;
        if (type == 0) {
            if (this.tryExtend(world, x, y, z, n)) {
                world.setBlockMetadataWithNotify(x, y, z, n | 8);
                world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "tile.piston.out", 0.5f, world.rand.nextFloat() * 0.25f + 0.6f);
            }
        } else if (type == 1) {
            TileEntity tileEntity = world.getBlockTileEntity(x + PistonBlockTextures.X_OFFSET[n], y + PistonBlockTextures.Y_OFFSET[n], z + PistonBlockTextures.Z_OFFSET[n]);
            if (tileEntity != null && tileEntity instanceof TileEntityPiston) {
                ((TileEntityPiston)tileEntity).clearPistonTileEntity();
            }
            world.setBlockAndMetadata(x, y, z, Block.pistonMoving.blockID, n);
            world.setBlockTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this.blockID, n, n, false, true));
            if (this.isSticky) {
                TileEntityPiston tileEntityPiston;
                TileEntity tileEntity2;
                int n2 = x + PistonBlockTextures.X_OFFSET[n] * 2;
                int n3 = y + PistonBlockTextures.Y_OFFSET[n] * 2;
                int n4 = z + PistonBlockTextures.Z_OFFSET[n] * 2;
                int n5 = world.getBlockId(n2, n3, n4);
                int n6 = world.getBlockMetadata(n2, n3, n4);
                boolean bl = false;
                if (n5 == Block.pistonMoving.blockID && (tileEntity2 = world.getBlockTileEntity(n2, n3, n4)) != null && tileEntity2 instanceof TileEntityPiston && (tileEntityPiston = (TileEntityPiston)tileEntity2).getFacing() == n && tileEntityPiston.isExtending()) {
                    tileEntityPiston.clearPistonTileEntity();
                    n5 = tileEntityPiston.getStoredBlockID();
                    n6 = tileEntityPiston.getBlockMetadata();
                    bl = true;
                }
                if (!bl && n5 > 0 && BlockPistonBase.canPushBlock(n5, world, n2, n3, n4, false) && (Block.blocksList[n5].getMobilityFlag() == 0 || n5 == Block.pistonBase.blockID || n5 == Block.pistonStickyBase.blockID)) {
                    this.ignoreUpdates = false;
                    world.setBlockWithNotify(n2, n3, n4, 0);
                    this.ignoreUpdates = true;
                    world.setBlockAndMetadata(x += PistonBlockTextures.X_OFFSET[n], y += PistonBlockTextures.Y_OFFSET[n], z += PistonBlockTextures.Z_OFFSET[n], Block.pistonMoving.blockID, n6);
                    world.setBlockTileEntity(x, y, z, BlockPistonMoving.getTileEntity(n5, n6, n, false, false));
                } else if (!bl) {
                    this.ignoreUpdates = false;
                    world.setBlockWithNotify(x + PistonBlockTextures.X_OFFSET[n], y + PistonBlockTextures.Y_OFFSET[n], z + PistonBlockTextures.Z_OFFSET[n], 0);
                    this.ignoreUpdates = true;
                }
            } else {
                this.ignoreUpdates = false;
                world.setBlockWithNotify(x + PistonBlockTextures.X_OFFSET[n], y + PistonBlockTextures.Y_OFFSET[n], z + PistonBlockTextures.Z_OFFSET[n], 0);
                this.ignoreUpdates = true;
            }
            world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "tile.piston.in", 0.5f, world.rand.nextFloat() * 0.15f + 0.6f);
        }
        this.ignoreUpdates = false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        if (BlockPistonBase.isExtended(n)) {
            switch (BlockPistonBase.getOrientation(n)) {
                case 0: {
                    this.setBlockBounds(0.0f, 0.25f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 1: {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.75f, 1.0f);
                    break;
                }
                case 2: {
                    this.setBlockBounds(0.0f, 0.0f, 0.25f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 3: {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.75f);
                    break;
                }
                case 4: {
                    this.setBlockBounds(0.25f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                    break;
                }
                case 5: {
                    this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.75f, 1.0f, 1.0f);
                }
            }
        } else {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB shape, ArrayList<AxisAlignedBB> collisions) {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        super.getCollidingBoundingBoxes(world, x, y, z, shape, collisions);
    }

    @Override
    public boolean isACube() {
        return false;
    }

    public static int getOrientation(int metadata) {
        return metadata & 7;
    }

    public static boolean isExtended(int metadata) {
        return (metadata & 8) != 0;
    }

    private static int determineOrientation(final World world, final int x, final int y, final int z, final EntityPlayer player) {
        if (MathHelper.abs((float) player.posX - x) < 2.0f && MathHelper.abs((float) player.posZ - z) < 2.0f) {
            final double n = player.posY + 1.82 - player.yOffset;
            if (n - y > 2.0) {
                return 1;
            }
            if (y - n > 0.0) {
                return 0;
            }
        }
        final int n2 = MathHelper.floor_double(player.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3;
        if (n2 == 0) {
            return 2;
        }
        if (n2 == 1) {
            return 5;
        }
        if (n2 == 2) {
            return 3;
        }
        if (n2 == 3) {
            return 4;
        }
        return 0;
    }

    private static boolean canPushBlock(int block, World world, int x, int y, int z, boolean allowBreaking) {
        TileEntity tileEntity;
        if (block == Block.obsidian.blockID) {
            return false;
        }
        if (block == Block.pistonBase.blockID || block == Block.pistonStickyBase.blockID) {
            if (BlockPistonBase.isExtended(world.getBlockMetadata(x, y, z))) {
                return false;
            }
        } else {
            if (Block.blocksList[block].getHardness() == -1.0f) {
                return false;
            }
            if (Block.blocksList[block].getMobilityFlag() == 2) {
                return false;
            }
            if (!allowBreaking && Block.blocksList[block].getMobilityFlag() == 1) {
                return false;
            }
        }
        return (tileEntity = world.getBlockTileEntity(x, y, z)) == null;
    }

    private static boolean canExtend(World world, int x, int y, int z, int dir) {
        int n = x + PistonBlockTextures.X_OFFSET[dir];
        int n2 = y + PistonBlockTextures.Y_OFFSET[dir];
        int n3 = z + PistonBlockTextures.Z_OFFSET[dir];
        for (int i = 0; i < 13; ++i) {
            if (n2 <= 0 || n2 >= 127) {
                return false;
            }
            int n4 = world.getBlockId(n, n2, n3);
            if (n4 == 0) break;
            if (!BlockPistonBase.canPushBlock(n4, world, n, n2, n3, true)) {
                return false;
            }
            if (Block.blocksList[n4].getMobilityFlag() == 1) break;
            if (i == 12) {
                return false;
            }
            n += PistonBlockTextures.X_OFFSET[dir];
            n2 += PistonBlockTextures.Y_OFFSET[dir];
            n3 += PistonBlockTextures.Z_OFFSET[dir];
        }
        return true;
    }

    private boolean tryExtend(World world, int x, int y, int z, int dir) {
        int n;
        int n2;
        int n3 = x + PistonBlockTextures.X_OFFSET[dir];
        int n4 = y + PistonBlockTextures.Y_OFFSET[dir];
        int n5 = z + PistonBlockTextures.Z_OFFSET[dir];
        for (n2 = 0; n2 < 13; ++n2) {
            if (n4 <= 0 || n4 >= 127) {
                return false;
            }
            n = world.getBlockId(n3, n4, n5);
            if (n == 0) break;
            if (!BlockPistonBase.canPushBlock(n, world, n3, n4, n5, true)) {
                return false;
            }
            if (Block.blocksList[n].getMobilityFlag() == 1) {
                Block.blocksList[n].dropBlockAsItem(world, n3, n4, n5, world.getBlockMetadata(n3, n4, n5));
                world.setBlockWithNotify(n3, n4, n5, 0);
                break;
            }
            if (n2 == 12) {
                return false;
            }
            n3 += PistonBlockTextures.X_OFFSET[dir];
            n4 += PistonBlockTextures.Y_OFFSET[dir];
            n5 += PistonBlockTextures.Z_OFFSET[dir];
        }
        while (n3 != x || n4 != y || n5 != z) {
            n2 = n3 - PistonBlockTextures.X_OFFSET[dir];
            n = n4 - PistonBlockTextures.Y_OFFSET[dir];
            int n6 = n5 - PistonBlockTextures.Z_OFFSET[dir];
            int n7 = world.getBlockId(n2, n, n6);
            int n8 = world.getBlockMetadata(n2, n, n6);
            if (n7 == this.blockID && n2 == x && n == y && n6 == z) {
                world.setBlockAndMetadata(n3, n4, n5, Block.pistonMoving.blockID, dir | (this.isSticky ? 8 : 0));
                world.setBlockTileEntity(n3, n4, n5, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, dir | (this.isSticky ? 8 : 0), dir, true, false));
            } else {
                world.setBlockAndMetadata(n3, n4, n5, Block.pistonMoving.blockID, n8);
                world.setBlockTileEntity(n3, n4, n5, BlockPistonMoving.getTileEntity(n7, n8, dir, true, false));
            }
            n3 = n2;
            n4 = n;
            n5 = n6;
        }
        return true;
    }
}

