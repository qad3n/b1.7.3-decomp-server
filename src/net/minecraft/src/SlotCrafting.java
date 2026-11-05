package net.minecraft.src;

public class SlotCrafting extends Slot {
    private final IInventory craftMatrix;
    private EntityPlayer player;

    public SlotCrafting(EntityPlayer player, IInventory craftingInventory, IInventory resultInventory, int slot, int x, int y) {
        super(resultInventory, slot, x, y);
        this.player = player;
        this.craftMatrix = craftingInventory;
    }

    @Override
    public boolean isItemValid(ItemStack item) {
        return false;
    }

    @Override
    public void onPickupFromSlot(ItemStack item) {
        item.onResult(this.player.worldObj, this.player);
        if (item.itemID == Block.workbench.blockID) {
            this.player.addStat(AchievementList.CRAFT_CRAFTING_TABLE, 1);
        } else if (item.itemID == Item.pickaxeWood.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_PICKAXE, 1);
        } else if (item.itemID == Block.stoneOvenIdle.blockID) {
            this.player.addStat(AchievementList.CRAFT_FURNACE, 1);
        } else if (item.itemID == Item.hoeWood.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_WOODEN_HOE, 1);
        } else if (item.itemID == Item.bread.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_BREAD, 1);
        } else if (item.itemID == Item.cake.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_CAKE, 1);
        } else if (item.itemID == Item.pickaxeStone.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_BETTER_PICKAXE, 1);
        } else if (item.itemID == Item.swordWood.shiftedIndex) {
            this.player.addStat(AchievementList.CRAFT_SWORD, 1);
        }

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); i++) {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            if (itemstack != null) {
                this.craftMatrix.decrStackSize(i, 1);
                if (itemstack.getItem().hasContainerItem()) {
                    this.craftMatrix.setInventorySlotContents(i, new ItemStack(itemstack.getItem().getContainerItem()));
                }
            }
        }
    }
}
