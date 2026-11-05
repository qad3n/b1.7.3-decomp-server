package net.minecraft.src;

import java.util.Random;

public class BlockDeadBush
extends BlockFlower {
    protected BlockDeadBush(int n, int n2) {
        super(n, n2);
        float f = 0.4f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.8f, 0.5f + f);
    }

    protected boolean canThisPlantGrowOnThisBlockID(int block) {
        return block == Block.sand.blockID;
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        return this.blockIndexInTexture;
    }

    public int idDropped(int metadata, Random random) {
        return -1;
    }
}

