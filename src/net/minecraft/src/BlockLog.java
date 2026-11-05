package net.minecraft.src;

import java.util.Random;

public class BlockLog extends Block {
    protected BlockLog(int id) {
        super(id, Material.wood);
        this.blockIndexInTexture = 20;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int idDropped(int metadata, Random random) {
        return Block.wood.blockID;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.harvestBlock(world, player, x, y, z, metadata);
    }

    @Override
    public void onBlockRemoval(World world, int x, int y, int z) {
        byte b0 = 4;
        int i = b0 + 1;
        if (world.checkChunksExist(x - i, y - i, z - i, x + i, y + i, z + i)) {
            for (int j = -b0; j <= b0; j++) {
                for (int k = -b0; k <= b0; k++) {
                    for (int l = -b0; l <= b0; l++) {
                        int i1 = world.getBlockId(x + j, y + k, z + l);
                        if (i1 == Block.leaves.blockID) {
                            int j1 = world.getBlockMetadata(x + j, y + k, z + l);
                            if ((j1 & 8) == 0) {
                                world.setBlockMetadata(x + j, y + k, z + l, j1 | 8);
                            }
                        }
                    }
                }
            }
        }
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (face == 1) {
            return 21;
        }
        if (face == 0) {
            return 21;
        }
        if (metadata == 1) {
            return 116;
        }
        if (metadata == 2) {
            return 117;
        }
        return 20;
    }

    @Override
    protected int damageDropped(int metadata) {
        return metadata;
    }
}
