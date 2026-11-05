package net.minecraft.src;


public class RecipesFood {
    public void addRecipes(CraftingManager manager) {
        manager.addRecipe(new ItemStack(Item.bowlSoup), "Y", "X", "#", Character.valueOf('X'), Block.mushroomBrown, Character.valueOf('Y'), Block.mushroomRed, Character.valueOf('#'), Item.bowlEmpty);
        manager.addRecipe(new ItemStack(Item.bowlSoup), "Y", "X", "#", Character.valueOf('X'), Block.mushroomRed, Character.valueOf('Y'), Block.mushroomBrown, Character.valueOf('#'), Item.bowlEmpty);
        manager.addRecipe(new ItemStack(Item.cookie, 8), "#X#", Character.valueOf('X'), new ItemStack(Item.dyePowder, 1, 3), Character.valueOf('#'), Item.wheat);
    }
}

