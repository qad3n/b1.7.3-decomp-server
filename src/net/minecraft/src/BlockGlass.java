package net.minecraft.src;

import java.util.Random;

public class BlockGlass
extends BlockBreakable {
    public BlockGlass(int n, int n2, Material material, boolean bl) {
        super(n, n2, material, bl);
    }

    public int quantityDropped(Random random) {
        return 0;
    }
}

