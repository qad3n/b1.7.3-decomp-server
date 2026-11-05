package net.minecraft.src;

import java.util.Random;

public class BlockStep
extends Block {
    public static final String[] VARIANTS = new String[]{"stone", "sand", "wood", "cobble"};
    private boolean blockType;

    public BlockStep(int id, boolean doubleSlab) {
        super(id, 6, Material.rock);
        this.blockType = doubleSlab;
        if (!doubleSlab) {
            this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
        }
        this.setLightOpacity(255);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (metadata == 0) {
            if (face <= 1) {
                return 6;
            }
            return 5;
        }
        if (metadata == 1) {
            if (face == 0) {
                return 208;
            }
            if (face == 1) {
                return 176;
            }
            return 192;
        }
        if (metadata == 2) {
            return 4;
        }
        if (metadata == 3) {
            return 16;
        }
        return 6;
    }

    public int getBlockTextureFromSide(int face) {
        return this.getBlockTextureFromSideAndMetadata(face, 0);
    }

    public boolean isOpaqueCube() {
        return this.blockType;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        int n;
        if (this != Block.stairSingle) {
            super.onBlockAdded(world, x, y, z);
        }
        int n2 = world.getBlockId(x, y - 1, z);
        int n3 = world.getBlockMetadata(x, y, z);
        if (n3 != (n = world.getBlockMetadata(x, y - 1, z))) {
            return;
        }
        if (n2 == BlockStep.stairSingle.blockID) {
            world.setBlockWithNotify(x, y, z, 0);
            world.setBlockAndMetadataWithNotify(x, y - 1, z, Block.stairDouble.blockID, n3);
        }
    }

    public int idDropped(int metadata, Random random) {
        return Block.stairSingle.blockID;
    }

    public int quantityDropped(Random random) {
        if (this.blockType) {
            return 2;
        }
        return 1;
    }

    protected int damageDropped(int metadata) {
        return metadata;
    }

    public boolean isACube() {
        return this.blockType;
    }
}

