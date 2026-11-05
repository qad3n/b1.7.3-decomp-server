package net.minecraft.src;

public class ItemDye extends Item {
    public static final String[] dyeColors = new String[]{
        "black", "red", "green", "brown", "blue", "purple", "cyan", "silver", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"
    };
    public static final int[] COLORS = new int[]{
        1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 2651799, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320
    };

    public ItemDye(int var1) {
        super(var1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (stack.getItemDamage() == 15) {
            int i = world.getBlockId(x, y, z);
            if (i == Block.sapling.blockID) {
                if (!world.singleplayerWorld) {
                    ((BlockSapling)Block.sapling).growTree(world, x, y, z, world.rand);
                    stack.stackSize--;
                }

                return true;
            }

            if (i == Block.crops.blockID) {
                if (!world.singleplayerWorld) {
                    ((BlockCrops)Block.crops).fertilize(world, x, y, z);
                    stack.stackSize--;
                }

                return true;
            }

            if (i == Block.grass.blockID) {
                if (!world.singleplayerWorld) {
                    stack.stackSize--;

                    label53:
                    for (int j = 0; j < 128; j++) {
                        int k = x;
                        int l = y + 1;
                        int i1 = z;

                        for (int j1 = 0; j1 < j / 16; j1++) {
                            k += itemRand.nextInt(3) - 1;
                            l += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
                            i1 += itemRand.nextInt(3) - 1;
                            if (world.getBlockId(k, l - 1, i1) != Block.grass.blockID || world.isBlockNormalCube(k, l, i1)) {
                                continue label53;
                            }
                        }

                        if (world.getBlockId(k, l, i1) == 0) {
                            if (itemRand.nextInt(10) != 0) {
                                world.setBlockAndMetadataWithNotify(k, l, i1, Block.tallGrass.blockID, 1);
                            } else if (itemRand.nextInt(3) != 0) {
                                world.setBlockWithNotify(k, l, i1, Block.plantYellow.blockID);
                            } else {
                                world.setBlockWithNotify(k, l, i1, Block.plantRed.blockID);
                            }
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void saddleEntity(ItemStack stack, EntityLiving entity) {
        if (entity instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep)entity;
            int i = BlockCloth.getBlockMetadataForItemMetadata(stack.getItemDamage());
            if (!entitysheep.isSheared() && entitysheep.getFleeceColor() != i) {
                entitysheep.setFleeceColor(i);
                stack.stackSize--;
            }
        }
    }
}
