package net.minecraft.src;


public class ItemCloth
extends ItemBlock {
    public ItemCloth(int n) {
        super(n);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int metadata) {
        return metadata;
    }
}

