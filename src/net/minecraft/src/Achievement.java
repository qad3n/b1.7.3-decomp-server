package net.minecraft.src;


public class Achievement
extends StatBase {
    public final int column;
    public final int row;
    public final Achievement parent;
    private final String description;
    public final ItemStack theItemStack;
    private boolean challenge;

    public Achievement(int id, String name, int column, int row, Item icon, Achievement parent) {
        this(id, name, column, row, new ItemStack(icon), parent);
    }

    public Achievement(int id, String name, int column, int row, Block icon, Achievement parent) {
        this(id, name, column, row, new ItemStack(icon), parent);
    }

    public Achievement(int id, String name, int column, int row, ItemStack icon, Achievement parent) {
        super(0x500000 + id, StatCollector.translateToLocal("achievement." + name));
        this.theItemStack = icon;
        this.description = StatCollector.translateToLocal("achievement." + name + ".desc");
        this.column = column;
        this.row = row;
        if (column < AchievementList.minColumn) {
            AchievementList.minColumn = column;
        }
        if (row < AchievementList.minRow) {
            AchievementList.minRow = row;
        }
        if (column > AchievementList.maxColumn) {
            AchievementList.maxColumn = column;
        }
        if (row > AchievementList.maxRow) {
            AchievementList.maxRow = row;
        }
        this.parent = parent;
    }

    public Achievement setLocal() {
        this.local = true;
        return this;
    }

    public Achievement setChallenge() {
        this.challenge = true;
        return this;
    }

    public Achievement register() {
        super.register();
        AchievementList.ALL.add(this);
        return this;
    }
}

