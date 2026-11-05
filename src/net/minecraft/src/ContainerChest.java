package net.minecraft.src;


public class ContainerChest
extends Container {
    private IInventory inventory;
    private int rows;

    public ContainerChest(IInventory playerInventory, IInventory inventory) {
        int n;
        int n2;
        this.inventory = inventory;
        this.rows = inventory.getSizeInventory() / 9;
        int n3 = (this.rows - 4) * 18;
        for (n2 = 0; n2 < this.rows; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(inventory, n + n2 * 9, 8 + n * 18, 18 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + n2 * 9 + 9, 8 + n * 18, 103 + n2 * 18 + n3));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(playerInventory, n2, 8 + n2 * 18, 161 + n3));
        }
    }

    public boolean canInteractWith(EntityPlayer player) {
        return this.inventory.canInteractWith(player);
    }

    public ItemStack quickMoveItem(int slot) {
        ItemStack itemStack = null;
        Slot slot2 = (Slot)this.inventorySlots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9) {
                this.moveItem(itemStack2, this.rows * 9, this.inventorySlots.size(), true);
            } else {
                this.moveItem(itemStack2, 0, this.rows * 9, false);
            }
            if (itemStack2.stackSize == 0) {
                slot2.putStack(null);
            } else {
                slot2.onSlotChanged();
            }
        }
        return itemStack;
    }
}

