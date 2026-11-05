package net.minecraft.src;

import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICrafting {
    public void updateCraftingInventory(Container var1, List<ItemStack> var2);

    public void updateCraftingInventorySlot(Container var1, int var2, ItemStack var3);

    public void updateCraftingInventoryInfo(Container var1, int var2, int var3);
}

