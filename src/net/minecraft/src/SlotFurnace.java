package net.minecraft.src;


public class SlotFurnace
extends Slot {
    private EntityPlayer player;

    public SlotFurnace(EntityPlayer player, IInventory resultInventory, int slot, int x, int y) {
        super(resultInventory, slot, x, y);
        this.player = player;
    }

    public boolean isItemValid(ItemStack item) {
        return false;
    }

    public void onPickupFromSlot(ItemStack item) {
        item.onResult(this.player.worldObj, this.player);
        if (item.itemID == Item.ingotIron.shiftedIndex) {
            this.player.addStat(AchievementList.GET_IRON_INGOT, 1);
        }
        if (item.itemID == Item.fishCooked.shiftedIndex) {
            this.player.addStat(AchievementList.COOK_FISH, 1);
        }
        super.onPickupFromSlot(item);
    }
}

