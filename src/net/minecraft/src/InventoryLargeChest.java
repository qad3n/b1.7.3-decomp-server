package net.minecraft.src;


public class InventoryLargeChest
implements IInventory {
    private String name;
    private IInventory upperChest;
    private IInventory lowerChest;

    public InventoryLargeChest(String name, IInventory inventory1, IInventory inventory2) {
        this.name = name;
        this.upperChest = inventory1;
        this.lowerChest = inventory2;
    }

    public int getSizeInventory() {
        return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
    }

    public String getInvName() {
        return this.name;
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot >= this.upperChest.getSizeInventory()) {
            return this.lowerChest.getStackInSlot(slot - this.upperChest.getSizeInventory());
        }
        return this.upperChest.getStackInSlot(slot);
    }

    public ItemStack decrStackSize(int slot, int amount) {
        if (slot >= this.upperChest.getSizeInventory()) {
            return this.lowerChest.decrStackSize(slot - this.upperChest.getSizeInventory(), amount);
        }
        return this.upperChest.decrStackSize(slot, amount);
    }

    public void setInventorySlotContents(int slot, ItemStack item) {
        if (slot >= this.upperChest.getSizeInventory()) {
            this.lowerChest.setInventorySlotContents(slot - this.upperChest.getSizeInventory(), item);
        } else {
            this.upperChest.setInventorySlotContents(slot, item);
        }
    }

    public int getInventoryStackLimit() {
        return this.upperChest.getInventoryStackLimit();
    }

    public void onInventoryChanged() {
        this.upperChest.onInventoryChanged();
        this.lowerChest.onInventoryChanged();
    }

    public boolean canInteractWith(EntityPlayer player) {
        return this.upperChest.canInteractWith(player) && this.lowerChest.canInteractWith(player);
    }
}

