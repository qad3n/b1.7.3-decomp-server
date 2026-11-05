package net.minecraft.src;


public interface IRecipe {
    public boolean matches(InventoryCrafting var1);

    public ItemStack getResult(InventoryCrafting var1);

    public int getRecipeSize();

    public ItemStack getResult();
}

