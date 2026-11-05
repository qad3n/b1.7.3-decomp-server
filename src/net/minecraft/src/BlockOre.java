package net.minecraft.src;

import java.util.Random;

public class BlockOre
extends Block {
    public BlockOre(int id, int sprite) {
        super(id, sprite, Material.rock);
    }

    public int idDropped(int metadata, Random random) {
        if (this.blockID == Block.oreCoal.blockID) {
            return Item.coal.shiftedIndex;
        }
        if (this.blockID == Block.oreDiamond.blockID) {
            return Item.diamond.shiftedIndex;
        }
        if (this.blockID == Block.oreLapis.blockID) {
            return Item.dyePowder.shiftedIndex;
        }
        return this.blockID;
    }

    public int quantityDropped(Random random) {
        if (this.blockID == Block.oreLapis.blockID) {
            return 4 + random.nextInt(5);
        }
        return 1;
    }

    protected int damageDropped(int metadata) {
        if (this.blockID == Block.oreLapis.blockID) {
            return 4;
        }
        return 0;
    }
}

