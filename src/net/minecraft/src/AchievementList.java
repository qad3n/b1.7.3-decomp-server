package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;

public class AchievementList {
    public static int minColumn;
    public static int minRow;
    public static int maxColumn;
    public static int maxRow;
    public static List<Achievement> ALL;
    public static Achievement OPEN_INVENTORY;
    public static Achievement GET_LOG;
    public static Achievement CRAFT_CRAFTING_TABLE;
    public static Achievement CRAFT_PICKAXE;
    public static Achievement CRAFT_FURNACE;
    public static Achievement GET_IRON_INGOT;
    public static Achievement CRAFT_WOODEN_HOE;
    public static Achievement CRAFT_BREAD;
    public static Achievement CRAFT_CAKE;
    public static Achievement CRAFT_BETTER_PICKAXE;
    public static Achievement COOK_FISH;
    public static Achievement TRAVEL_KILOMETER_BY_MINECART;
    public static Achievement CRAFT_SWORD;
    public static Achievement KILL_ENEMY;
    public static Achievement KILL_COW;
    public static Achievement RIDE_PIG_OFF_CLIFF;

    public static void init() {
    }

    static {
        ALL = new ArrayList<Achievement>();
        OPEN_INVENTORY = new Achievement(0, "openInventory", 0, 0, Item.book, null).setLocal().register();
        GET_LOG = new Achievement(1, "mineWood", 2, 1, Block.wood, OPEN_INVENTORY).register();
        CRAFT_CRAFTING_TABLE = new Achievement(2, "buildWorkBench", 4, -1, Block.workbench, GET_LOG).register();
        CRAFT_PICKAXE = new Achievement(3, "buildPickaxe", 4, 2, Item.pickaxeWood, CRAFT_CRAFTING_TABLE).register();
        CRAFT_FURNACE = new Achievement(4, "buildFurnace", 3, 4, Block.stoneOvenActive, CRAFT_PICKAXE).register();
        GET_IRON_INGOT = new Achievement(5, "acquireIron", 1, 4, Item.ingotIron, CRAFT_FURNACE).register();
        CRAFT_WOODEN_HOE = new Achievement(6, "buildHoe", 2, -3, Item.hoeWood, CRAFT_CRAFTING_TABLE).register();
        CRAFT_BREAD = new Achievement(7, "makeBread", -1, -3, Item.bread, CRAFT_WOODEN_HOE).register();
        CRAFT_CAKE = new Achievement(8, "bakeCake", 0, -5, Item.cake, CRAFT_WOODEN_HOE).register();
        CRAFT_BETTER_PICKAXE = new Achievement(9, "buildBetterPickaxe", 6, 2, Item.pickaxeStone, CRAFT_PICKAXE).register();
        COOK_FISH = new Achievement(10, "cookFish", 2, 6, Item.fishCooked, CRAFT_FURNACE).register();
        TRAVEL_KILOMETER_BY_MINECART = new Achievement(11, "onARail", 2, 3, Block.minecartTrack, GET_IRON_INGOT).setChallenge().register();
        CRAFT_SWORD = new Achievement(12, "buildSword", 6, -1, Item.swordWood, CRAFT_CRAFTING_TABLE).register();
        KILL_ENEMY = new Achievement(13, "killEnemy", 8, -1, Item.bone, CRAFT_SWORD).register();
        KILL_COW = new Achievement(14, "killCow", 7, -3, Item.leather, CRAFT_SWORD).register();
        RIDE_PIG_OFF_CLIFF = new Achievement(15, "flyPig", 8, -4, Item.saddle, KILL_COW).setChallenge().register();
        System.out.println(ALL.size() + " achievements");
    }
}

