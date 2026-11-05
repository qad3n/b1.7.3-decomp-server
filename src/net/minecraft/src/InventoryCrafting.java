package net.minecraft.src;


public class InventoryCrafting
implements IInventory {
    private ItemStack[] stackList;
    private int size;
    private Container eventHandler;

    public InventoryCrafting(Container menu, int width, int height) {
        int n = width * height;
        this.stackList = new ItemStack[n];
        this.eventHandler = menu;
        this.size = width;
    }

    public int getSizeInventory() {
        return this.stackList.length;
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot >= this.getSizeInventory()) {
            return null;
        }
        return this.stackList[slot];
    }

    public ItemStack getItem(int column, int row) {
        if (column < 0 || column >= this.size) {
            return null;
        }
        int n = column + row * this.size;
        return this.getStackInSlot(n);
    }

    public String getInvName() {
        return "Crafting";
    }

    public ItemStack decrStackSize(int slot, int amount) {
        if (this.stackList[slot] != null) {
            if (this.stackList[slot].stackSize <= amount) {
                ItemStack itemStack = this.stackList[slot];
                this.stackList[slot] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return itemStack;
            }
            ItemStack itemStack = this.stackList[slot].splitStack(amount);
            if (this.stackList[slot].stackSize == 0) {
                this.stackList[slot] = null;
            }
            this.eventHandler.onCraftMatrixChanged(this);
            return itemStack;
        }
        return null;
    }

    public void setInventorySlotContents(int slot, ItemStack item) {
        this.stackList[slot] = item;
        this.eventHandler.onCraftMatrixChanged(this);
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

