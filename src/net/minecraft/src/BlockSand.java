package net.minecraft.src;

import java.util.Random;

public class BlockSand
extends Block {
    public static boolean fallInstantly = false;

    public BlockSand(int id, int sprite) {
        super(id, sprite, Material.sand);
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        world.scheduleUpdateTick(x, y, z, this.blockID, this.tickRate());
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        this.tryToFall(world, x, y, z);
    }

    private void tryToFall(World world, int x, int y, int z) {
        int n = x;
        int n2 = y;
        int n3 = z;
        if (BlockSand.canFallBelow(world, n, n2 - 1, n3) && n2 >= 0) {
            int n4 = 32;
            if (fallInstantly || !world.checkChunksExist(x - n4, y - n4, z - n4, x + n4, y + n4, z + n4)) {
                world.setBlockWithNotify(x, y, z, 0);
                while (BlockSand.canFallBelow(world, x, y - 1, z) && y > 0) {
                    --y;
                }
                if (y > 0) {
                    world.setBlockWithNotify(x, y, z, this.blockID);
                }
            } else {
                EntityFallingSand entityFallingSand = new EntityFallingSand(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, this.blockID);
                world.entityJoinedWorld(entityFallingSand);
            }
        }
    }

    public int tickRate() {
        return 3;
    }

    public static boolean canFallBelow(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y, z);
        if (n == 0) {
            return true;
        }
        if (n == Block.fire.blockID) {
            return true;
        }
        Material material = Block.blocksList[n].blockMaterial;
        if (material == Material.water) {
            return true;
        }
        return material == Material.lava;
    }
}

