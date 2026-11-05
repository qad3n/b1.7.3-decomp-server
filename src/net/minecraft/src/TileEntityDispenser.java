package net.minecraft.src;

import java.util.Random;

public class TileEntityDispenser extends TileEntity implements IInventory {
    private ItemStack[] dispenserContents = new ItemStack[9];
    private Random dispenserRandom = new Random();

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.dispenserContents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.dispenserContents[slot] != null) {
            if (this.dispenserContents[slot].stackSize <= amount) {
                ItemStack itemstack1 = this.dispenserContents[slot];
                this.dispenserContents[slot] = null;
                this.onInventoryChanged();
                return itemstack1;
            }

            ItemStack itemstack = this.dispenserContents[slot].splitStack(amount);
            if (this.dispenserContents[slot].stackSize == 0) {
                this.dispenserContents[slot] = null;
            }

            this.onInventoryChanged();
            return itemstack;
        } else {
            return null;
        }
    }

    public ItemStack getRandomStackFromInventory() {
        int slot = -1;
        int n = 1;
        for (int i = 0; i < this.dispenserContents.length; ++i) {
            if (this.dispenserContents[i] != null && this.dispenserRandom.nextInt(n++) == 0) {
                slot = i;
            }
        }
        if (slot >= 0) {
            return this.decrStackSize(slot, 1);
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        this.dispenserContents[slot] = item;
        if (item != null && item.stackSize > this.getInventoryStackLimit()) {
            item.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    @Override
    public String getInvName() {
        return "Trap";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Items");
        this.dispenserContents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;
            if (j >= 0 && j < this.dispenserContents.length) {
                this.dispenserContents[j] = new ItemStack(nbttagcompound);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.dispenserContents.length; i++) {
            if (this.dispenserContents[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                this.dispenserContents[i].writeToNBT(nbttagcompound);
                nbttaglist.setTag(nbttagcompound);
            }
        }

        nbt.setTag("Items", nbttaglist);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this
            ? false
            : !(player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) > 64.0);
    }
}
