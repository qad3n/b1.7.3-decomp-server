package net.minecraft.src;

import java.util.Random;

public class BlockClay
extends Block {
    public BlockClay(int id, int sprite) {
        super(id, sprite, Material.clay);
    }

    public int idDropped(int metadata, Random random) {
        return Item.clay.shiftedIndex;
    }

    public int quantityDropped(Random random) {
        return 4;
    }
}

