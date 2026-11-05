package net.minecraft.src;

import java.util.Random;

public class BlockGravel
extends BlockSand {
    public BlockGravel(int n, int n2) {
        super(n, n2);
    }

    public int idDropped(int metadata, Random random) {
        if (random.nextInt(10) == 0) {
            return Item.flint.shiftedIndex;
        }
        return this.blockID;
    }
}

