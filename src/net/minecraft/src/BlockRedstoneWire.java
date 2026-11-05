package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire
extends Block {
    private boolean wiresProvidePower = true;
    private Set<ChunkPosition> neighborsToUpdate = new HashSet<ChunkPosition>();

    public BlockRedstoneWire(int id, int sprite) {
        super(id, sprite, Material.circuits);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.0625f, 1.0f);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        return this.blockIndexInTexture;
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

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.isBlockNormalCube(x, y - 1, z);
    }

    private void updateAndPropagateCurrentStrength(World world, int x, int y, int z) {
        this.doUpdatePower(world, x, y, z, x, y, z);
        ArrayList<ChunkPosition> arrayList = new ArrayList<ChunkPosition>(this.neighborsToUpdate);
        this.neighborsToUpdate.clear();
        for (int i = 0; i < arrayList.size(); ++i) {
            ChunkPosition chunkPosition = (ChunkPosition)arrayList.get(i);
            world.notifyBlocksOfNeighborChange(chunkPosition.x, chunkPosition.y, chunkPosition.z, this.blockID);
        }
    }

    private void doUpdatePower(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ) {
        int i = world.getBlockMetadata(x, y, z);
        int j = 0;
        this.wiresProvidePower = false;
        boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
        this.wiresProvidePower = true;
        if (flag) {
            j = 15;
        } else {
            for (int k = 0; k < 4; k++) {
                int l = x;
                int i1 = z;
                if (k == 0) {
                    l--;
                }
                if (k == 1) {
                    l++;
                }
                if (k == 2) {
                    i1--;
                }
                if (k == 3) {
                    i1++;
                }
                if (l != sourceX || y != sourceY || i1 != sourceZ) {
                    j = this.getMaxCurrentStrength(world, l, y, i1, j);
                }
                if (world.isBlockNormalCube(l, y, i1) && !world.isBlockNormalCube(x, y + 1, z)) {
                    if (l != sourceX || y + 1 != sourceY || i1 != sourceZ) {
                        j = this.getMaxCurrentStrength(world, l, y + 1, i1, j);
                    }
                } else if (!world.isBlockNormalCube(l, y, i1) && (l != sourceX || y - 1 != sourceY || i1 != sourceZ)) {
                    j = this.getMaxCurrentStrength(world, l, y - 1, i1, j);
                }
            }
            if (j > 0) {
                j--;
            } else {
                j = 0;
            }
        }
        if (i != j) {
            world.editingBlocks = true;
            world.setBlockMetadataWithNotify(x, y, z, j);
            world.markBlocksDirty(x, y, z, x, y, z);
            world.editingBlocks = false;
            for (int l1 = 0; l1 < 4; l1++) {
                int i2 = x;
                int j2 = z;
                int j1 = y - 1;
                if (l1 == 0) {
                    i2--;
                }
                if (l1 == 1) {
                    i2++;
                }
                if (l1 == 2) {
                    j2--;
                }
                if (l1 == 3) {
                    j2++;
                }
                if (world.isBlockNormalCube(i2, y, j2)) {
                    j1 += 2;
                }
                int k1 = 0;
                k1 = this.getMaxCurrentStrength(world, i2, y, j2, -1);
                j = world.getBlockMetadata(x, y, z);
                if (j > 0) {
                    j--;
                }
                if (k1 >= 0 && k1 != j) {
                    this.doUpdatePower(world, i2, y, j2, x, y, z);
                }
                k1 = this.getMaxCurrentStrength(world, i2, j1, j2, -1);
                j = world.getBlockMetadata(x, y, z);
                if (j > 0) {
                    j--;
                }
                if (k1 >= 0 && k1 != j) {
                    this.doUpdatePower(world, i2, j1, j2, x, y, z);
                }
            }
            if (i == 0 || j == 0) {
                this.neighborsToUpdate.add(new ChunkPosition(x, y, z));
                this.neighborsToUpdate.add(new ChunkPosition(x - 1, y, z));
                this.neighborsToUpdate.add(new ChunkPosition(x + 1, y, z));
                this.neighborsToUpdate.add(new ChunkPosition(x, y - 1, z));
                this.neighborsToUpdate.add(new ChunkPosition(x, y + 1, z));
                this.neighborsToUpdate.add(new ChunkPosition(x, y, z - 1));
                this.neighborsToUpdate.add(new ChunkPosition(x, y, z + 1));
            }
        }
    }

    private void notifyWireNeighborsOfNeighborChange(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) != this.blockID) {
            return;
        }
        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.singleplayerWorld) {
            return;
        }
        this.updateAndPropagateCurrentStrength(world, x, y, z);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        this.notifyWireNeighborsOfNeighborChange(world, x - 1, y, z);
        this.notifyWireNeighborsOfNeighborChange(world, x + 1, y, z);
        this.notifyWireNeighborsOfNeighborChange(world, x, y, z - 1);
        this.notifyWireNeighborsOfNeighborChange(world, x, y, z + 1);
        if (world.isBlockNormalCube(x - 1, y, z)) {
            this.notifyWireNeighborsOfNeighborChange(world, x - 1, y + 1, z);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x - 1, y - 1, z);
        }
        if (world.isBlockNormalCube(x + 1, y, z)) {
            this.notifyWireNeighborsOfNeighborChange(world, x + 1, y + 1, z);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x + 1, y - 1, z);
        }
        if (world.isBlockNormalCube(x, y, z - 1)) {
            this.notifyWireNeighborsOfNeighborChange(world, x, y + 1, z - 1);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x, y - 1, z - 1);
        }
        if (world.isBlockNormalCube(x, y, z + 1)) {
            this.notifyWireNeighborsOfNeighborChange(world, x, y + 1, z + 1);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x, y - 1, z + 1);
        }
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        super.onBlockRemoval(world, x, y, z);
        if (world.singleplayerWorld) {
            return;
        }
        world.notifyBlocksOfNeighborChange(x, y + 1, z, this.blockID);
        world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
        this.updateAndPropagateCurrentStrength(world, x, y, z);
        this.notifyWireNeighborsOfNeighborChange(world, x - 1, y, z);
        this.notifyWireNeighborsOfNeighborChange(world, x + 1, y, z);
        this.notifyWireNeighborsOfNeighborChange(world, x, y, z - 1);
        this.notifyWireNeighborsOfNeighborChange(world, x, y, z + 1);
        if (world.isBlockNormalCube(x - 1, y, z)) {
            this.notifyWireNeighborsOfNeighborChange(world, x - 1, y + 1, z);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x - 1, y - 1, z);
        }
        if (world.isBlockNormalCube(x + 1, y, z)) {
            this.notifyWireNeighborsOfNeighborChange(world, x + 1, y + 1, z);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x + 1, y - 1, z);
        }
        if (world.isBlockNormalCube(x, y, z - 1)) {
            this.notifyWireNeighborsOfNeighborChange(world, x, y + 1, z - 1);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x, y - 1, z - 1);
        }
        if (world.isBlockNormalCube(x, y, z + 1)) {
            this.notifyWireNeighborsOfNeighborChange(world, x, y + 1, z + 1);
        } else {
            this.notifyWireNeighborsOfNeighborChange(world, x, y - 1, z + 1);
        }
    }

    private int getMaxCurrentStrength(World world, int x, int y, int z, int wirePower) {
        if (world.getBlockId(x, y, z) != this.blockID) {
            return wirePower;
        }
        int n = world.getBlockMetadata(x, y, z);
        if (n > wirePower) {
            return n;
        }
        return wirePower;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        if (world.singleplayerWorld) {
            return;
        }
        int n = world.getBlockMetadata(x, y, z);
        boolean bl = this.canPlaceBlockAt(world, x, y, z);
        if (!bl) {
            this.dropBlockAsItem(world, x, y, z, n);
            world.setBlockWithNotify(x, y, z, 0);
        } else {
            this.updateAndPropagateCurrentStrength(world, x, y, z);
        }
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
    }

    public int idDropped(int metadata, Random random) {
        return Item.redstone.shiftedIndex;
    }

    public boolean isIndirectlyPoweringTo(World world, int x, int y, int z, int dir) {
        if (!this.wiresProvidePower) {
            return false;
        }
        return this.isPoweringTo(world, x, y, z, dir);
    }

    public boolean isPoweringTo(IBlockAccess world, int x, int y, int z, int dir) {
        boolean bl;
        if (!this.wiresProvidePower) {
            return false;
        }
        if (world.getBlockMetadata(x, y, z) == 0) {
            return false;
        }
        if (dir == 1) {
            return true;
        }
        boolean bl2 = BlockRedstoneWire.isPowerProviderOrWire(world, x - 1, y, z, 1) || !world.isBlockNormalCube(x - 1, y, z) && BlockRedstoneWire.isPowerProviderOrWire(world, x - 1, y - 1, z, -1);
        boolean bl3 = BlockRedstoneWire.isPowerProviderOrWire(world, x + 1, y, z, 3) || !world.isBlockNormalCube(x + 1, y, z) && BlockRedstoneWire.isPowerProviderOrWire(world, x + 1, y - 1, z, -1);
        boolean bl4 = BlockRedstoneWire.isPowerProviderOrWire(world, x, y, z - 1, 2) || !world.isBlockNormalCube(x, y, z - 1) && BlockRedstoneWire.isPowerProviderOrWire(world, x, y - 1, z - 1, -1);
        boolean bl5 = bl = BlockRedstoneWire.isPowerProviderOrWire(world, x, y, z + 1, 0) || !world.isBlockNormalCube(x, y, z + 1) && BlockRedstoneWire.isPowerProviderOrWire(world, x, y - 1, z + 1, -1);
        if (!world.isBlockNormalCube(x, y + 1, z)) {
            if (world.isBlockNormalCube(x - 1, y, z) && BlockRedstoneWire.isPowerProviderOrWire(world, x - 1, y + 1, z, -1)) {
                bl2 = true;
            }
            if (world.isBlockNormalCube(x + 1, y, z) && BlockRedstoneWire.isPowerProviderOrWire(world, x + 1, y + 1, z, -1)) {
                bl3 = true;
            }
            if (world.isBlockNormalCube(x, y, z - 1) && BlockRedstoneWire.isPowerProviderOrWire(world, x, y + 1, z - 1, -1)) {
                bl4 = true;
            }
            if (world.isBlockNormalCube(x, y, z + 1) && BlockRedstoneWire.isPowerProviderOrWire(world, x, y + 1, z + 1, -1)) {
                bl = true;
            }
        }
        if (!(bl4 || bl3 || bl2 || bl || dir < 2 || dir > 5)) {
            return true;
        }
        if (dir == 2 && bl4 && !bl2 && !bl3) {
            return true;
        }
        if (dir == 3 && bl && !bl2 && !bl3) {
            return true;
        }
        if (dir == 4 && bl2 && !bl4 && !bl) {
            return true;
        }
        return dir == 5 && bl3 && !bl4 && !bl;
    }

    public boolean canProvidePower() {
        return this.wiresProvidePower;
    }

    public static boolean isPowerProviderOrWire(IBlockAccess world, int x, int y, int z, int side) {
        int n = world.getBlockId(x, y, z);
        if (n == Block.redstoneWire.blockID) {
            return true;
        }
        if (n == 0) {
            return false;
        }
        if (Block.blocksList[n].canProvidePower()) {
            return true;
        }
        if (n == Block.redstoneRepeaterIdle.blockID || n == Block.redstoneRepeaterActive.blockID) {
            int n2 = world.getBlockMetadata(x, y, z);
            return side == ModelBed.OPPOSITE[n2 & 3];
        }
        return false;
    }
}

