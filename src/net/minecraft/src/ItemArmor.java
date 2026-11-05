package net.minecraft.src;


public class ItemArmor
extends Item {
    private static final int[] damageReduceAmountArray = new int[]{3, 8, 6, 3};
    private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
    public final int armorLevel;
    public final int armorType;
    public final int damageReduceAmount;
    public final int renderIndex;

    public ItemArmor(int id, int durability, int materialId, int slot) {
        super(id);
        this.armorLevel = durability;
        this.armorType = slot;
        this.renderIndex = materialId;
        this.damageReduceAmount = damageReduceAmountArray[slot];
        this.setMaxDamage(maxDamageArray[slot] * 3 << durability);
        this.maxStackSize = 1;
    }
}

