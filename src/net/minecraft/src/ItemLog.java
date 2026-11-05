package net.minecraft.src;


public class ItemLog
extends ItemBlock {
    public ItemLog(int n) {
        super(n);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int metadata) {
        return metadata;
    }
}

