// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class CraftingManager
{
    private static final CraftingManager instance;
    private List<IRecipe> recipes;
    
    public static final CraftingManager getInstance() {
        return CraftingManager.instance;
    }
    
    private CraftingManager() {
        this.recipes = new ArrayList<IRecipe>();
        new RecipesTools().addRecipes(this);
        new RecipesWeapons().addRecipes(this);
        new RecipesIngots().addRecipes(this);
        new RecipesFood().addRecipes(this);
        new RecipesCrafting().addRecipes(this);
        new RecipesArmor().addRecipes(this);
        new RecipesDyes().addRecipes(this);
        this.addRecipe(new ItemStack(Item.paper, 3), "###", '#', Item.reed);
        this.addRecipe(new ItemStack(Item.book, 1), "#", "#", "#", '#', Item.paper);
        this.addRecipe(new ItemStack(Block.fence, 2), "###", "###", '#', Item.stick);
        this.addRecipe(new ItemStack(Block.jukebox, 1), "###", "#X#", "###", '#', Block.planks, 'X', Item.diamond);
        this.addRecipe(new ItemStack(Block.musicBlock, 1), "###", "#X#", "###", '#', Block.planks, 'X', Item.redstone);
        this.addRecipe(new ItemStack(Block.bookShelf, 1), "###", "XXX", "###", '#', Block.planks, 'X', Item.book);
        this.addRecipe(new ItemStack(Block.blockSnow, 1), "##", "##", '#', Item.snowball);
        this.addRecipe(new ItemStack(Block.blockClay, 1), "##", "##", '#', Item.clay);
        this.addRecipe(new ItemStack(Block.brick, 1), "##", "##", '#', Item.brick);
        this.addRecipe(new ItemStack(Block.glowStone, 1), "##", "##", '#', Item.lightStoneDust);
        this.addRecipe(new ItemStack(Block.cloth, 1), "##", "##", '#', Item.silk);
        this.addRecipe(new ItemStack(Block.tnt, 1), "X#X", "#X#", "X#X", 'X', Item.gunpowder, '#', Block.sand);
        this.addRecipe(new ItemStack(Block.stairSingle, 3, 3), "###", '#', Block.cobblestone);
        this.addRecipe(new ItemStack(Block.stairSingle, 3, 0), "###", '#', Block.stone);
        this.addRecipe(new ItemStack(Block.stairSingle, 3, 1), "###", '#', Block.sandStone);
        this.addRecipe(new ItemStack(Block.stairSingle, 3, 2), "###", '#', Block.planks);
        this.addRecipe(new ItemStack(Block.ladder, 2), "# #", "###", "# #", '#', Item.stick);
        this.addRecipe(new ItemStack(Item.doorWood, 1), "##", "##", "##", '#', Block.planks);
        this.addRecipe(new ItemStack(Block.trapdoor, 2), "###", "###", '#', Block.planks);
        this.addRecipe(new ItemStack(Item.doorSteel, 1), "##", "##", "##", '#', Item.ingotIron);
        this.addRecipe(new ItemStack(Item.sign, 1), "###", "###", " X ", '#', Block.planks, 'X', Item.stick);
        this.addRecipe(new ItemStack(Item.cake, 1), "AAA", "BEB", "CCC", 'A', Item.bucketMilk, 'B', Item.sugar, 'C', Item.wheat, 'E', Item.egg);
        this.addRecipe(new ItemStack(Item.sugar, 1), "#", '#', Item.reed);
        this.addRecipe(new ItemStack(Block.planks, 4), "#", '#', Block.wood);
        this.addRecipe(new ItemStack(Item.stick, 4), "#", "#", '#', Block.planks);
        this.addRecipe(new ItemStack(Block.torchWood, 4), "X", "#", 'X', Item.coal, '#', Item.stick);
        this.addRecipe(new ItemStack(Block.torchWood, 4), "X", "#", 'X', new ItemStack(Item.coal, 1, 1), '#', Item.stick);
        this.addRecipe(new ItemStack(Item.bowlEmpty, 4), "# #", " # ", '#', Block.planks);
        this.addRecipe(new ItemStack(Block.minecartTrack, 16), "X X", "X#X", "X X", 'X', Item.ingotIron, '#', Item.stick);
        this.addRecipe(new ItemStack(Block.railPowered, 6), "X X", "X#X", "XRX", 'X', Item.ingotGold, 'R', Item.redstone, '#', Item.stick);
        this.addRecipe(new ItemStack(Block.railDetector, 6), "X X", "X#X", "XRX", 'X', Item.ingotIron, 'R', Item.redstone, '#', Block.pressurePlateStone);
        this.addRecipe(new ItemStack(Item.minecartEmpty, 1), "# #", "###", '#', Item.ingotIron);
        this.addRecipe(new ItemStack(Block.pumpkinLantern, 1), "A", "B", 'A', Block.pumpkin, 'B', Block.torchWood);
        this.addRecipe(new ItemStack(Item.minecartCrate, 1), "A", "B", 'A', Block.chest, 'B', Item.minecartEmpty);
        this.addRecipe(new ItemStack(Item.minecartPowered, 1), "A", "B", 'A', Block.stoneOvenIdle, 'B', Item.minecartEmpty);
        this.addRecipe(new ItemStack(Item.boat, 1), "# #", "###", '#', Block.planks);
        this.addRecipe(new ItemStack(Item.bucketEmpty, 1), "# #", " # ", '#', Item.ingotIron);
        this.addRecipe(new ItemStack(Item.flintAndSteel, 1), "A ", " B", 'A', Item.ingotIron, 'B', Item.flint);
        this.addRecipe(new ItemStack(Item.bread, 1), "###", '#', Item.wheat);
        this.addRecipe(new ItemStack(Block.stairCompactPlanks, 4), "#  ", "## ", "###", '#', Block.planks);
        this.addRecipe(new ItemStack(Item.fishingRod, 1), "  #", " #X", "# X", '#', Item.stick, 'X', Item.silk);
        this.addRecipe(new ItemStack(Block.stairCompactCobblestone, 4), "#  ", "## ", "###", '#', Block.cobblestone);
        this.addRecipe(new ItemStack(Item.painting, 1), "###", "#X#", "###", '#', Item.stick, 'X', Block.cloth);
        this.addRecipe(new ItemStack(Item.appleGold, 1), "###", "#X#", "###", '#', Block.blockGold, 'X', Item.appleRed);
        this.addRecipe(new ItemStack(Block.lever, 1), "X", "#", '#', Block.cobblestone, 'X', Item.stick);
        this.addRecipe(new ItemStack(Block.torchRedstoneActive, 1), "X", "#", '#', Item.stick, 'X', Item.redstone);
        this.addRecipe(new ItemStack(Item.redstoneRepeater, 1), "#X#", "III", '#', Block.torchRedstoneActive, 'X', Item.redstone, 'I', Block.stone);
        this.addRecipe(new ItemStack(Item.pocketSundial, 1), " # ", "#X#", " # ", '#', Item.ingotGold, 'X', Item.redstone);
        this.addRecipe(new ItemStack(Item.compass, 1), " # ", "#X#", " # ", '#', Item.ingotIron, 'X', Item.redstone);
        this.addRecipe(new ItemStack(Item.MAP, 1), "###", "#X#", "###", '#', Item.paper, 'X', Item.compass);
        this.addRecipe(new ItemStack(Block.button, 1), "#", "#", '#', Block.stone);
        this.addRecipe(new ItemStack(Block.pressurePlateStone, 1), "##", '#', Block.stone);
        this.addRecipe(new ItemStack(Block.pressurePlatePlanks, 1), "##", '#', Block.planks);
        this.addRecipe(new ItemStack(Block.dispenser, 1), "###", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.bow, 'R', Item.redstone);
        this.addRecipe(new ItemStack(Block.pistonBase, 1), "TTT", "#X#", "#R#", '#', Block.cobblestone, 'X', Item.ingotIron, 'R', Item.redstone, 'T', Block.planks);
        this.addRecipe(new ItemStack(Block.pistonStickyBase, 1), "S", "P", 'S', Item.slimeBall, 'P', Block.pistonBase);
        this.addRecipe(new ItemStack(Item.bed, 1), "###", "XXX", '#', Block.cloth, 'X', Block.planks);
        Collections.sort(this.recipes, new Comparator<IRecipe>() {
            public int compare(final IRecipe recipe, final IRecipe recipe2) {
                if (recipe instanceof ShapelessRecipes && recipe2 instanceof ShapedRecipes) {
                    return 1;
                }
                if (recipe2 instanceof ShapelessRecipes && recipe instanceof ShapedRecipes) {
                    return -1;
                }
                if (recipe2.getRecipeSize() < recipe.getRecipeSize()) {
                    return -1;
                }
                if (recipe2.getRecipeSize() > recipe.getRecipeSize()) {
                    return 1;
                }
                return 0;
            }
        });
        System.out.println(this.recipes.size() + " recipes");
    }
    
    void addRecipe(final ItemStack result, final Object... args) {
        String s = "";
        int i = 0;
        int width = 0;
        int height = 0;
        if (args[i] instanceof String[]) {
            final String[] array = (String[])args[i++];
            for (int j = 0; j < array.length; ++j) {
                final String str = array[j];
                ++height;
                width = str.length();
                s += str;
            }
        }
        else {
            while (args[i] instanceof String) {
                final String str2 = (String)args[i++];
                ++height;
                width = str2.length();
                s += str2;
            }
        }
        final HashMap hashMap = new HashMap();
        while (i < args.length) {
            final Character c = (Character)args[i];
            Object o = null;
            if (args[i + 1] instanceof Item) {
                o = new ItemStack((Item)args[i + 1]);
            }
            else if (args[i + 1] instanceof Block) {
                o = new ItemStack((Block)args[i + 1], 1, -1);
            }
            else if (args[i + 1] instanceof ItemStack) {
                o = args[i + 1];
            }
            hashMap.put(c, o);
            i += 2;
        }
        final ItemStack[] ingredients = new ItemStack[width * height];
        for (int k = 0; k < width * height; ++k) {
            final char char1 = s.charAt(k);
            if (hashMap.containsKey(char1)) {
                ingredients[k] = ((ItemStack)hashMap.get(char1)).copy();
            }
            else {
                ingredients[k] = null;
            }
        }
        this.recipes.add(new ShapedRecipes(width, height, ingredients, result));
    }
    
    void addShapelessRecipe(final ItemStack result, final Object... args) {
        final ArrayList ingredients = new ArrayList();
        for (final Object o : args) {
            if (o instanceof ItemStack) {
                ingredients.add(((ItemStack)o).copy());
            }
            else if (o instanceof Item) {
                ingredients.add(new ItemStack((Item)o));
            }
            else {
                if (!(o instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }
                ingredients.add(new ItemStack((Block)o));
            }
        }
        this.recipes.add(new ShapelessRecipes(result, ingredients));
    }
    
    public ItemStack findMatchingRecipe(final InventoryCrafting inventory) {
        for (int i = 0; i < this.recipes.size(); ++i) {
            final IRecipe recipe = this.recipes.get(i);
            if (recipe.matches(inventory)) {
                return recipe.getResult(inventory);
            }
        }
        return null;
    }
    
    public List<IRecipe> getRecipeList() {
        return this.recipes;
    }
    
    static {
        instance = new CraftingManager();
    }
}
