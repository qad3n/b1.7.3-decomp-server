package net.minecraft.src;


public class TileEntityFurnace
extends TileEntity
implements IInventory {
    private ItemStack[] furnaceItemStacks = new ItemStack[3];
    public int furnaceBurnTime = 0;
    public int currentItemBurnTime = 0;
    public int furnaceCookTime = 0;

    public int getSizeInventory() {
        return this.furnaceItemStacks.length;
    }

    public ItemStack getStackInSlot(int slot) {
        return this.furnaceItemStacks[slot];
    }

    public ItemStack decrStackSize(int slot, int amount) {
        if (this.furnaceItemStacks[slot] != null) {
            if (this.furnaceItemStacks[slot].stackSize <= amount) {
                ItemStack itemStack = this.furnaceItemStacks[slot];
                this.furnaceItemStacks[slot] = null;
                return itemStack;
            }
            ItemStack itemStack = this.furnaceItemStacks[slot].splitStack(amount);
            if (this.furnaceItemStacks[slot].stackSize == 0) {
                this.furnaceItemStacks[slot] = null;
            }
            return itemStack;
        }
        return null;
    }

    public void setInventorySlotContents(int slot, ItemStack item) {
        this.furnaceItemStacks[slot] = item;
        if (item != null && item.stackSize > this.getInventoryStackLimit()) {
            item.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInvName() {
        return "Furnace";
    }

    @Override
public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);
    NBTTagList nbttaglist = nbt.getTagList("Items");
    this.furnaceItemStacks = new ItemStack[this.getSizeInventory()];
    for (int i = 0; i < nbttaglist.tagCount(); i++) {
        NBTTagCompound nbttagcompound = (NBTTagCompound) nbttaglist.tagAt(i);
        byte b0 = nbttagcompound.getByte("Slot");
        if (b0 >= 0 && b0 < this.furnaceItemStacks.length) {
            this.furnaceItemStacks[b0] = new ItemStack(nbttagcompound);
        }
    }
    this.furnaceBurnTime = nbt.getShort("BurnTime");
    this.furnaceCookTime = nbt.getShort("CookTime");
    this.currentItemBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
}

    @Override
public void writeToNBT(NBTTagCompound nbt) {
    super.writeToNBT(nbt);
    nbt.setShort("BurnTime", (short) this.furnaceBurnTime);
    nbt.setShort("CookTime", (short) this.furnaceCookTime);
    NBTTagList nbttaglist = new NBTTagList();
    for (int i = 0; i < this.furnaceItemStacks.length; i++) {
        if (this.furnaceItemStacks[i] != null) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte) i);
            this.furnaceItemStacks[i].writeToNBT(nbttagcompound);
            nbttaglist.setTag(nbttagcompound);
        }
    }
    nbt.setTag("Items", nbttaglist);
}

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isBurning() {
        return this.furnaceBurnTime > 0;
    }

    public void updateEntity() {
        boolean bl = this.furnaceBurnTime > 0;
        boolean bl2 = false;
        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }
        if (!this.worldObj.singleplayerWorld) {
            if (this.furnaceBurnTime == 0 && this.canSmelt()) {
                this.currentItemBurnTime = this.furnaceBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
                if (this.furnaceBurnTime > 0) {
                    bl2 = true;
                    if (this.furnaceItemStacks[1] != null) {
                        --this.furnaceItemStacks[1].stackSize;
                        if (this.furnaceItemStacks[1].stackSize == 0) {
                            this.furnaceItemStacks[1] = null;
                        }
                    }
                }
            }
            if (this.isBurning() && this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime == 200) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    bl2 = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }
            if (bl != this.furnaceBurnTime > 0) {
                bl2 = true;
                BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }
        if (bl2) {
            this.onInventoryChanged();
        }
    }

    private boolean canSmelt() {
        if (this.furnaceItemStacks[0] == null) {
            return false;
        }
        ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
        if (itemStack == null) {
            return false;
        }
        if (this.furnaceItemStacks[2] == null) {
            return true;
        }
        if (!this.furnaceItemStacks[2].isItemEqual(itemStack)) {
            return false;
        }
        if (this.furnaceItemStacks[2].stackSize < this.getInventoryStackLimit() && this.furnaceItemStacks[2].stackSize < this.furnaceItemStacks[2].getMaxStackSize()) {
            return true;
        }
        return this.furnaceItemStacks[2].stackSize < itemStack.getMaxStackSize();
    }

    public void smeltItem() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack itemStack = FurnaceRecipes.smelting().getSmeltingResult(this.furnaceItemStacks[0].getItem().shiftedIndex);
        if (this.furnaceItemStacks[2] == null) {
            this.furnaceItemStacks[2] = itemStack.copy();
        } else if (this.furnaceItemStacks[2].itemID == itemStack.itemID) {
            ++this.furnaceItemStacks[2].stackSize;
        }
        --this.furnaceItemStacks[0].stackSize;
        if (this.furnaceItemStacks[0].stackSize <= 0) {
            this.furnaceItemStacks[0] = null;
        }
    }

    private int getItemBurnTime(ItemStack item) {
        if (item == null) {
            return 0;
        }
        int n = item.getItem().shiftedIndex;
        if (n < 256 && Block.blocksList[n].blockMaterial == Material.wood) {
            return 300;
        }
        if (n == Item.stick.shiftedIndex) {
            return 100;
        }
        if (n == Item.coal.shiftedIndex) {
            return 1600;
        }
        if (n == Item.bucketLava.shiftedIndex) {
            return 20000;
        }
        if (n == Block.sapling.blockID) {
            return 100;
        }
        return 0;
    }

    public boolean canInteractWith(EntityPlayer player) {
        if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) {
            return false;
        }
        return !(player.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) > 64.0);
    }
}

