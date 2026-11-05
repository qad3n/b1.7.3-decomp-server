package net.minecraft.src;

import java.util.Random;

public class BlockGlowStone
extends Block {
    public BlockGlowStone(int n, int n2, Material material) {
        super(n, n2, material);
    }

    public int quantityDropped(Random random) {
        return 2 + random.nextInt(3);
    }

    public int idDropped(int metadata, Random random) {
        return Item.lightStoneDust.shiftedIndex;
    }
}

