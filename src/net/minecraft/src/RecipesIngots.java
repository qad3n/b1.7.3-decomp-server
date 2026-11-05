package net.minecraft.src;


public class RecipesIngots {
    private Object[][] recipeItems = new Object[][]{{Block.blockGold, new ItemStack(Item.ingotGold, 9)}, {Block.blockSteel, new ItemStack(Item.ingotIron, 9)}, {Block.blockDiamond, new ItemStack(Item.diamond, 9)}, {Block.blockLapis, new ItemStack(Item.dyePowder, 9, 4)}};

    public void addRecipes(CraftingManager manager) {
        for (int i = 0; i < this.recipeItems.length; ++i) {
            Block block = (Block)this.recipeItems[i][0];
            ItemStack itemStack = (ItemStack)this.recipeItems[i][1];
            manager.addRecipe(new ItemStack(block), "###", "###", "###", Character.valueOf('#'), itemStack);
            manager.addRecipe(itemStack, "#", Character.valueOf('#'), block);
        }
    }
}

