package net.minecraft.src;


public class RecipesCrafting {
    public void addRecipes(CraftingManager manager) {
        manager.addRecipe(new ItemStack(Block.chest), "###", "# #", "###", Character.valueOf('#'), Block.planks);
        manager.addRecipe(new ItemStack(Block.stoneOvenIdle), "###", "# #", "###", Character.valueOf('#'), Block.cobblestone);
        manager.addRecipe(new ItemStack(Block.workbench), "##", "##", Character.valueOf('#'), Block.planks);
        manager.addRecipe(new ItemStack(Block.sandStone), "##", "##", Character.valueOf('#'), Block.sand);
    }
}

