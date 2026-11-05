package net.minecraft.src;

import java.util.Random;

public class BlockObsidian
extends BlockStone {
    public BlockObsidian(int n, int n2) {
        super(n, n2);
    }

    public int quantityDropped(Random random) {
        return 1;
    }

    public int idDropped(int metadata, Random random) {
        return Block.obsidian.blockID;
    }
}

