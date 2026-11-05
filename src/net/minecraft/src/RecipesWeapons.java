package net.minecraft.src;


public class RecipesWeapons {
    private String[][] recipePatterns = new String[][]{{"X", "X", "#"}};
    private Object[][] recipeItems = new Object[][]{{Block.planks, Block.cobblestone, Item.ingotIron, Item.diamond, Item.ingotGold}, {Item.swordWood, Item.swordStone, Item.swordSteel, Item.swordDiamond, Item.swordGold}};

    public void addRecipes(CraftingManager manager) {
        for (int i = 0; i < this.recipeItems[0].length; ++i) {
            Object object = this.recipeItems[0][i];
            for (int j = 0; j < this.recipeItems.length - 1; ++j) {
                Item item = (Item)this.recipeItems[j + 1][i];
                manager.addRecipe(new ItemStack(item), this.recipePatterns[j], Character.valueOf('#'), Item.stick, Character.valueOf('X'), object);
            }
        }
        manager.addRecipe(new ItemStack(Item.bow, 1), " #X", "# X", " #X", Character.valueOf('X'), Item.silk, Character.valueOf('#'), Item.stick);
        manager.addRecipe(new ItemStack(Item.arrow, 4), "X", "#", "Y", Character.valueOf('Y'), Item.feather, Character.valueOf('X'), Item.flint, Character.valueOf('#'), Item.stick);
    }
}

