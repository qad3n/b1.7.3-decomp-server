package net.minecraft.src;


public class ItemCookie
extends ItemFood {
    public ItemCookie(int id, int hungerPoints, boolean canBeCooked, int maxStackSize) {
        super(id, hungerPoints, canBeCooked);
        this.maxStackSize = maxStackSize;
    }
}

