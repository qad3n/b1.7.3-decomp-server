package net.minecraft.src;

import java.util.Random;

public class BlockTallGrass
extends BlockFlower {
    protected BlockTallGrass(int n, int n2) {
        super(n, n2);
        float f = 0.4f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.8f, 0.5f + f);
    }

    public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
        if (metadata == 1) {
            return this.blockIndexInTexture;
        }
        if (metadata == 2) {
            return this.blockIndexInTexture + 16 + 1;
        }
        if (metadata == 0) {
            return this.blockIndexInTexture + 16;
        }
        return this.blockIndexInTexture;
    }

    public int idDropped(int metadata, Random random) {
        if (random.nextInt(8) == 0) {
            return Item.seeds.shiftedIndex;
        }
        return -1;
    }
}

