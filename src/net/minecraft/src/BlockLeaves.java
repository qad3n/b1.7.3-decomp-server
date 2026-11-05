package net.minecraft.src;

import java.util.Random;

public class BlockLeaves extends BlockLeavesBase {
    private int baseIndexInPNG;
    int[] adjacentTreeBlocks;

    protected BlockLeaves(int id, int sprite) {
        super(id, sprite, Material.leaves, false);
        this.baseIndexInPNG = sprite;
        this.setTickOnLoad(true);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        byte b0 = 1;
        int i = b0 + 1;
        if (world.checkChunksExist(x - i, y - i, z - i, x + i, y + i, z + i)) {
            for (int j = -b0; j <= b0; j++) {
                for (int k = -b0; k <= b0; k++) {
                    for (int l = -b0; l <= b0; l++) {
                        int i1 = world.getBlockId(x + j, y + k, z + l);
                        if (i1 == Block.leaves.blockID) {
                            int j1 = world.getBlockMetadata(x + j, y + k, z + l);
                            world.setBlockMetadata(x + j, y + k, z + l, j1 | 8);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.singleplayerWorld) {
            int i = world.getBlockMetadata(x, y, z);
            if ((i & 8) != 0) {
                byte b0 = 4;
                int j = b0 + 1;
                byte b1 = 32;
                int k = b1 * b1;
                int l = b1 / 2;
                if (this.adjacentTreeBlocks == null) {
                    this.adjacentTreeBlocks = new int[b1 * b1 * b1];
                }

                if (world.checkChunksExist(x - j, y - j, z - j, x + j, y + j, z + j)) {
                    for (int i1 = -b0; i1 <= b0; i1++) {
                        for (int j1 = -b0; j1 <= b0; j1++) {
                            for (int k1 = -b0; k1 <= b0; k1++) {
                                int l1 = world.getBlockId(x + i1, y + j1, z + k1);
                                if (l1 == Block.wood.blockID) {
                                    this.adjacentTreeBlocks[(i1 + l) * k + (j1 + l) * b1 + k1 + l] = 0;
                                } else if (l1 == Block.leaves.blockID) {
                                    this.adjacentTreeBlocks[(i1 + l) * k + (j1 + l) * b1 + k1 + l] = -2;
                                } else {
                                    this.adjacentTreeBlocks[(i1 + l) * k + (j1 + l) * b1 + k1 + l] = -1;
                                }
                            }
                        }
                    }

                    for (int i2 = 1; i2 <= 4; i2++) {
                        for (int k2 = -b0; k2 <= b0; k2++) {
                            for (int l2 = -b0; l2 <= b0; l2++) {
                                for (int i3 = -b0; i3 <= b0; i3++) {
                                    if (this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l) * b1 + i3 + l] == i2 - 1) {
                                        if (this.adjacentTreeBlocks[(k2 + l - 1) * k + (l2 + l) * b1 + i3 + l] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l - 1) * k + (l2 + l) * b1 + i3 + l] = i2;
                                        }

                                        if (this.adjacentTreeBlocks[(k2 + l + 1) * k + (l2 + l) * b1 + i3 + l] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l + 1) * k + (l2 + l) * b1 + i3 + l] = i2;
                                        }

                                        if (this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l - 1) * b1 + i3 + l] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l - 1) * b1 + i3 + l] = i2;
                                        }

                                        if (this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l + 1) * b1 + i3 + l] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l + 1) * b1 + i3 + l] = i2;
                                        }

                                        if (this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l) * b1 + (i3 + l - 1)] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l) * b1 + (i3 + l - 1)] = i2;
                                        }

                                        if (this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l) * b1 + i3 + l + 1] == -2) {
                                            this.adjacentTreeBlocks[(k2 + l) * k + (l2 + l) * b1 + i3 + l + 1] = i2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                int j2 = this.adjacentTreeBlocks[l * k + l * b1 + l];
                if (j2 >= 0) {
                    world.setBlockMetadata(x, y, z, i & -9);
                } else {
                    this.removeLeaves(world, x, y, z);
                }
            }
        }
    }

    private void removeLeaves(World world, int x, int y, int z) {
        this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
        world.setBlockWithNotify(x, y, z, 0);
    }

    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(20) == 0 ? 1 : 0;
    }

    @Override
    public int idDropped(int metadata, Random random) {
        return Block.sapling.blockID;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        if (!world.singleplayerWorld && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().itemID == Item.SHEARS.shiftedIndex) {
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.leaves.blockID, 1, metadata & 3));
        } else {
            super.harvestBlock(world, player, x, y, z, metadata);
        }
    }

    @Override
    protected int damageDropped(int metadata) {
        return metadata & 3;
    }

    @Override
    public boolean isOpaqueCube() {
        return !this.graphicsLevel;
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if ((metadata & 3) == 1) {
            return this.blockIndexInTexture + 80;
        }
        return this.blockIndexInTexture;
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
        super.onEntityWalking(world, x, y, z, entity);
    }
}
