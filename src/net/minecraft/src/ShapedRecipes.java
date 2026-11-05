package net.minecraft.src;

public class ShapedRecipes implements IRecipe {
    private int width;
    private int height;
    private ItemStack[] ingredients;
    private ItemStack result;
    public final int resultItem;

    public ShapedRecipes(int width, int height, ItemStack[] ingredients, ItemStack result) {
        this.resultItem = result.itemID;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
    }

    @Override
    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public boolean matches(InventoryCrafting inventory) {
        for (int i = 0; i <= 3 - this.width; i++) {
            for (int j = 0; j <= 3 - this.height; j++) {
                if (this.matches(inventory, i, j, true)) {
                    return true;
                }

                if (this.matches(inventory, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(InventoryCrafting inventory, int x, int y, boolean rotate) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int k = i - x;
                int l = j - y;
                ItemStack itemstack = null;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (rotate) {
                        itemstack = this.ingredients[this.width - k - 1 + l * this.width];
                    } else {
                        itemstack = this.ingredients[k + l * this.width];
                    }
                }

                ItemStack itemstack1 = inventory.getItem(i, j);
                if (itemstack1 != null || itemstack != null) {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null) {
                        return false;
                    }

                    if (itemstack.itemID != itemstack1.itemID) {
                        return false;
                    }

                    if (itemstack.getItemDamage() != -1 && itemstack.getItemDamage() != itemstack1.getItemDamage()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getResult(InventoryCrafting inventory) {
        return new ItemStack(this.result.itemID, this.result.stackSize, this.result.getItemDamage());
    }

    @Override
    public int getRecipeSize() {
        return this.width * this.height;
    }
}
