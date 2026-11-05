package net.minecraft.src;

import java.util.Random;

public class BlockFlowing
extends BlockFluid {
    int adjacentSources = 0;
    boolean[] spread = new boolean[4];
    int[] distanceToGap = new int[4];

    protected BlockFlowing(int n, Material material) {
        super(n, material);
    }

    private void convertToSource(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z);
        world.setBlockAndMetadata(x, y, z, this.blockID + 1, n);
        world.markBlocksDirty(x, y, z, x, y, z);
        world.markBlockNeedsUpdate(x, y, z);
    }

    @Override
public void updateTick(World world, int x, int y, int z, Random random) {
    int i = this.getLiquidState(world, x, y, z);
    byte b0 = 1;
    if (this.blockMaterial == Material.lava && !world.worldProvider.isHellWorld) {
        b0 = 2;
    }
    boolean flag = true;
    if (i > 0) {
        int j = -100;
        this.adjacentSources = 0;
        j = this.getLowestDepth(world, x - 1, y, z, j);
        j = this.getLowestDepth(world, x + 1, y, z, j);
        j = this.getLowestDepth(world, x, y, z - 1, j);
        j = this.getLowestDepth(world, x, y, z + 1, j);
        int k = j + b0;
        if (k >= 8 || j < 0) {
            k = -1;
        }
        if (this.getLiquidState(world, x, y + 1, z) >= 0) {
            int l = this.getLiquidState(world, x, y + 1, z);
            if (l >= 8) {
                k = l;
            } else {
                k = l + 8;
            }
        }
        if (this.adjacentSources >= 2 && this.blockMaterial == Material.water) {
            if (world.getBlockMaterial(x, y - 1, z).isSolid()) {
                k = 0;
            } else if (world.getBlockMaterial(x, y - 1, z) == this.blockMaterial && world.getBlockMetadata(x, y, z) == 0) {
                k = 0;
            }
        }
        if (this.blockMaterial == Material.lava && i < 8 && k < 8 && k > i && random.nextInt(4) != 0) {
            k = i;
            flag = false;
        }
        if (k != i) {
            i = k;
            if (i < 0) {
                world.setBlockWithNotify(x, y, z, 0);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, i);
                world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
                world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            }
        } else if (flag) {
            this.convertToSource(world, x, y, z);
        }
    } else {
        this.convertToSource(world, x, y, z);
    }
    if (this.canSpreadTo(world, x, y - 1, z)) {
        if (i >= 8) {
            world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, i);
        } else {
            world.setBlockAndMetadataWithNotify(x, y - 1, z, this.blockID, i + 8);
        }
    } else if (i >= 0 && (i == 0 || this.isLiquidBlocking(world, x, y - 1, z))) {
        boolean[] aboolean = this.getSpread(world, x, y, z);
        int i1 = i + b0;
        if (i >= 8) {
            i1 = 1;
        }
        if (i1 >= 8) {
            return;
        }
        if (aboolean[0]) {
            this.spreadTo(world, x - 1, y, z, i1);
        }
        if (aboolean[1]) {
            this.spreadTo(world, x + 1, y, z, i1);
        }
        if (aboolean[2]) {
            this.spreadTo(world, x, y, z - 1, i1);
        }
        if (aboolean[3]) {
            this.spreadTo(world, x, y, z + 1, i1);
        }
    }
}

    private void spreadTo(World world, int x, int y, int z, int depth) {
        if (this.canSpreadTo(world, x, y, z)) {
            int n = world.getBlockId(x, y, z);
            if (n > 0) {
                if (this.blockMaterial == Material.lava) {
                    this.fizz(world, x, y, z);
                } else {
                    Block.blocksList[n].dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
                }
            }
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, depth);
        }
    }

    private int getDistanceToGap(World world, int x, int y, int z, int distance, int fromDir) {
        int n = 1000;
        for (int i = 0; i < 4; ++i) {
            int n2;
            if (i == 0 && fromDir == 1 || i == 1 && fromDir == 0 || i == 2 && fromDir == 3 || i == 3 && fromDir == 2) continue;
            int n3 = x;
            int n4 = y;
            int n5 = z;
            if (i == 0) {
                --n3;
            }
            if (i == 1) {
                ++n3;
            }
            if (i == 2) {
                --n5;
            }
            if (i == 3) {
                ++n5;
            }
            if (this.isLiquidBlocking(world, n3, n4, n5) || world.getBlockMaterial(n3, n4, n5) == this.blockMaterial && world.getBlockMetadata(n3, n4, n5) == 0) continue;
            if (!this.isLiquidBlocking(world, n3, n4 - 1, n5)) {
                return distance;
            }
            if (distance >= 4 || (n2 = this.getDistanceToGap(world, n3, n4, n5, distance + 1, i)) >= n) continue;
            n = n2;
        }
        return n;
    }

    private boolean[] getSpread(World world, int x, int y, int z) {
        int n;
        int n2;
        for (n2 = 0; n2 < 4; ++n2) {
            this.distanceToGap[n2] = 1000;
            n = x;
            int n3 = y;
            int n4 = z;
            if (n2 == 0) {
                --n;
            }
            if (n2 == 1) {
                ++n;
            }
            if (n2 == 2) {
                --n4;
            }
            if (n2 == 3) {
                ++n4;
            }
            if (this.isLiquidBlocking(world, n, n3, n4) || world.getBlockMaterial(n, n3, n4) == this.blockMaterial && world.getBlockMetadata(n, n3, n4) == 0) continue;
            if (!this.isLiquidBlocking(world, n, n3 - 1, n4)) {
                this.distanceToGap[n2] = 0;
            }
            else {
                this.distanceToGap[n2] = this.getDistanceToGap(world, n, n3, n4, 1, n2);
            }
        }
        n2 = this.distanceToGap[0];
        for (n = 1; n < 4; ++n) {
            if (this.distanceToGap[n] >= n2) continue;
            n2 = this.distanceToGap[n];
        }
        for (n = 0; n < 4; ++n) {
            this.spread[n] = this.distanceToGap[n] == n2;
        }
        return this.spread;
    }

    private boolean isLiquidBlocking(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y, z);
        if (n == Block.doorWood.blockID || n == Block.doorSteel.blockID || n == Block.signPost.blockID || n == Block.ladder.blockID || n == Block.reed.blockID) {
            return true;
        }
        if (n == 0) {
            return false;
        }
        Material material = Block.blocksList[n].blockMaterial;
        return material.getIsSolid();
    }

    protected int getLowestDepth(World world, int x, int y, int z, int depth) {
        int n = this.getLiquidState(world, x, y, z);
        if (n < 0) {
            return depth;
        }
        if (n == 0) {
            ++this.adjacentSources;
        }
        if (n >= 8) {
            n = 0;
        }
        return depth < 0 || n < depth ? n : depth;
    }

    private boolean canSpreadTo(World world, int x, int y, int z) {
        Material material = world.getBlockMaterial(x, y, z);
        if (material == this.blockMaterial) {
            return false;
        }
        if (material == Material.lava) {
            return false;
        }
        return !this.isLiquidBlocking(world, x, y, z);
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.getBlockId(x, y, z) == this.blockID) {
            world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
        }
    }
}

