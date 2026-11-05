package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipes implements IRecipe {
    private final ItemStack result;
    private final List<ItemStack> ingredients;

    public ShapelessRecipes(ItemStack result, List<ItemStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public boolean matches(InventoryCrafting inventory) {
        ArrayList<ItemStack> arraylist = new ArrayList<>(this.ingredients);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ItemStack itemstack = inventory.getItem(j, i);
                if (itemstack != null) {
                    boolean flag = false;

                    for (ItemStack itemstack1 : arraylist) {
                        if (itemstack.itemID == itemstack1.itemID
                            && (itemstack1.getItemDamage() == -1 || itemstack.getItemDamage() == itemstack1.getItemDamage())) {
                            flag = true;
                            arraylist.remove(itemstack1);
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }
            }
        }

        return arraylist.isEmpty();
    }

    @Override
    public ItemStack getResult(InventoryCrafting inventory) {
        return this.result.copy();
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }
}
