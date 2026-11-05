package net.minecraft.src;


public class InventoryCraftResult
implements IInventory {
    private ItemStack[] stackResult = new ItemStack[1];

    public int getSizeInventory() {
        return 1;
    }

    public ItemStack getStackInSlot(int slot) {
        return this.stackResult[slot];
    }

    public String getInvName() {
        return "Result";
    }

    public ItemStack decrStackSize(int slot, int amount) {
        if (this.stackResult[slot] != null) {
            ItemStack itemStack = this.stackResult[slot];
            this.stackResult[slot] = null;
            return itemStack;
        }
        return null;
    }

    public void setInventorySlotContents(int slot, ItemStack item) {
        this.stackResult[slot] = item;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void onInventoryChanged() {
    }

    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}

