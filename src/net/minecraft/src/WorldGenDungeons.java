package net.minecraft.src;

import java.util.Random;

public class WorldGenDungeons
extends WorldGenerator {
    public boolean generate(World world, Random random, int x, int y, int z) {
        int n;
        int n2;
        int n3;
        int n4 = 3;
        int n5 = random.nextInt(2) + 2;
        int n6 = random.nextInt(2) + 2;
        int n7 = 0;
        for (n3 = x - n5 - 1; n3 <= x + n5 + 1; ++n3) {
            for (n2 = y - 1; n2 <= y + n4 + 1; ++n2) {
                for (n = z - n6 - 1; n <= z + n6 + 1; ++n) {
                    Material material = world.getBlockMaterial(n3, n2, n);
                    if (n2 == y - 1 && !material.isSolid()) {
                        return false;
                    }
                    if (n2 == y + n4 + 1 && !material.isSolid()) {
                        return false;
                    }
                    if (n3 != x - n5 - 1 && n3 != x + n5 + 1 && n != z - n6 - 1 && n != z + n6 + 1 || n2 != y || !world.isAirBlock(n3, n2, n) || !world.isAirBlock(n3, n2 + 1, n)) continue;
                    ++n7;
                }
            }
        }
        if (n7 < 1 || n7 > 5) {
            return false;
        }
        for (n3 = x - n5 - 1; n3 <= x + n5 + 1; ++n3) {
            for (n2 = y + n4; n2 >= y - 1; --n2) {
                for (n = z - n6 - 1; n <= z + n6 + 1; ++n) {
                    if (n3 == x - n5 - 1 || n2 == y - 1 || n == z - n6 - 1 || n3 == x + n5 + 1 || n2 == y + n4 + 1 || n == z + n6 + 1) {
                        if (n2 >= 0 && !world.getBlockMaterial(n3, n2 - 1, n).isSolid()) {
                            world.setBlockWithNotify(n3, n2, n, 0);
                            continue;
                        }
                        if (!world.getBlockMaterial(n3, n2, n).isSolid()) continue;
                        if (n2 == y - 1 && random.nextInt(4) != 0) {
                            world.setBlockWithNotify(n3, n2, n, Block.cobblestoneMossy.blockID);
                            continue;
                        }
                        world.setBlockWithNotify(n3, n2, n, Block.cobblestone.blockID);
                        continue;
                    }
                    world.setBlockWithNotify(n3, n2, n, 0);
                }
            }
        }
        block6: for (n3 = 0; n3 < 2; ++n3) {
            for (n2 = 0; n2 < 3; ++n2) {
                int n8;
                int n9;
                n = x + random.nextInt(n5 * 2 + 1) - n5;
                if (!world.isAirBlock(n, n9 = y, n8 = z + random.nextInt(n6 * 2 + 1) - n6)) continue;
                int n10 = 0;
                if (world.getBlockMaterial(n - 1, n9, n8).isSolid()) {
                    ++n10;
                }
                if (world.getBlockMaterial(n + 1, n9, n8).isSolid()) {
                    ++n10;
                }
                if (world.getBlockMaterial(n, n9, n8 - 1).isSolid()) {
                    ++n10;
                }
                if (world.getBlockMaterial(n, n9, n8 + 1).isSolid()) {
                    ++n10;
                }
                if (n10 != 1) continue;
                world.setBlockWithNotify(n, n9, n8, Block.chest.blockID);
                TileEntityChest tileEntityChest = (TileEntityChest)world.getBlockTileEntity(n, n9, n8);
                for (int i = 0; i < 8; ++i) {
                    ItemStack itemStack = this.pickCheckLootItem(random);
                    if (itemStack == null) continue;
                    tileEntityChest.setInventorySlotContents(random.nextInt(tileEntityChest.getSizeInventory()), itemStack);
                }
                continue block6;
            }
        }
        world.setBlockWithNotify(x, y, z, Block.mobSpawner.blockID);
        TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner)world.getBlockTileEntity(x, y, z);
        tileEntityMobSpawner.setMobID(this.pickMobSpawner(random));
        return true;
    }

    private ItemStack pickCheckLootItem(Random random) {
        int n = random.nextInt(11);
        if (n == 0) {
            return new ItemStack(Item.saddle);
        }
        if (n == 1) {
            return new ItemStack(Item.ingotIron, random.nextInt(4) + 1);
        }
        if (n == 2) {
            return new ItemStack(Item.bread);
        }
        if (n == 3) {
            return new ItemStack(Item.wheat, random.nextInt(4) + 1);
        }
        if (n == 4) {
            return new ItemStack(Item.gunpowder, random.nextInt(4) + 1);
        }
        if (n == 5) {
            return new ItemStack(Item.silk, random.nextInt(4) + 1);
        }
        if (n == 6) {
            return new ItemStack(Item.bucketEmpty);
        }
        if (n == 7 && random.nextInt(100) == 0) {
            return new ItemStack(Item.appleGold);
        }
        if (n == 8 && random.nextInt(2) == 0) {
            return new ItemStack(Item.redstone, random.nextInt(4) + 1);
        }
        if (n == 9 && random.nextInt(10) == 0) {
            return new ItemStack(Item.itemsList[Item.record13.shiftedIndex + random.nextInt(2)]);
        }
        if (n == 10) {
            return new ItemStack(Item.dyePowder, 1, 3);
        }
        return null;
    }

    private String pickMobSpawner(Random random) {
        int n = random.nextInt(4);
        if (n == 0) {
            return "Skeleton";
        }
        if (n == 1) {
            return "Zombie";
        }
        if (n == 2) {
            return "Zombie";
        }
        if (n == 3) {
            return "Spider";
        }
        return "";
    }
}

