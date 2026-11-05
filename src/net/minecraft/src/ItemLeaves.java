package net.minecraft.src;


public class ItemLeaves
extends ItemBlock {
    public ItemLeaves(int n) {
        super(n);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int metadata) {
        return metadata | 8;
    }
}

