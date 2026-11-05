package net.minecraft.src;


public class ItemPiston
extends ItemBlock {
    public ItemPiston(int n) {
        super(n);
    }

    public int getMetadata(int metadata) {
        return 7;
    }
}

