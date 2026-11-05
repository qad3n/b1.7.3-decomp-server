package net.minecraft.src;


public class ItemSlab
extends ItemBlock {
    public ItemSlab(int n) {
        super(n);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int metadata) {
        return metadata;
    }
}

