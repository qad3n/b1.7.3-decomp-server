package net.minecraft.src;


public interface IInventory {
    public int getSizeInventory();

    public ItemStack getStackInSlot(int var1);

    public ItemStack decrStackSize(int var1, int var2);

    public void setInventorySlotContents(int var1, ItemStack var2);

    public String getInvName();

    public int getInventoryStackLimit();

    public void onInventoryChanged();

    public boolean canInteractWith(EntityPlayer var1);
}

