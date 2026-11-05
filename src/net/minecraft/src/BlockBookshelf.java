package net.minecraft.src;

import java.util.Random;

public class BlockBookshelf
extends Block {
    public BlockBookshelf(int id, int sprite) {
        super(id, sprite, Material.wood);
    }

    public int getBlockTextureFromSide(int face) {
        if (face <= 1) {
            return 4;
        }
        return this.blockIndexInTexture;
    }

    public int quantityDropped(Random random) {
        return 0;
    }
}

