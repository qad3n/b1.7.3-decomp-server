package net.minecraft.src;


public class ItemSapling
extends ItemBlock {
    public ItemSapling(int n) {
        super(n);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int metadata) {
        return metadata;
    }
}

