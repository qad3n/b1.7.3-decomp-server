package net.minecraft.src;

import java.util.Random;

public class BlockStone
extends Block {
    public BlockStone(int id, int sprite) {
        super(id, sprite, Material.rock);
    }

    public int idDropped(int metadata, Random random) {
        return Block.cobblestone.blockID;
    }
}

