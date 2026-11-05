// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

public class InventoryPlayer implements IInventory
{
    public ItemStack[] mainInventory;
    public ItemStack[] armorInventory;
    public int currentItem;
    public EntityPlayer player;
    private ItemStack itemStack;
    public boolean inventoryChanged;
    
    public InventoryPlayer(final EntityPlayer player) {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];
        this.currentItem = 0;
        this.inventoryChanged = false;
        this.player = player;
    }
    
    public ItemStack getCurrentItem() {
        if (this.currentItem < 9 && this.currentItem >= 0) {
            return this.mainInventory[this.currentItem];
        }
        return null;
    }
    
    public static int getHotbarSize() {
        return 9;
    }
    
    private int getInventorySlotContainItem(final int item) {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null && this.mainInventory[i].itemID == item) {
                return i;
            }
        }
        return -1;
    }
    
    private int getSlotWithSpace(final ItemStack item) {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null && this.mainInventory[i].itemID == item.itemID && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == item.getItemDamage())) {
                return i;
            }
        }
        return -1;
    }
    
    private int getFirstEmptyStack() {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    private int addItemStack(ItemStack item) {
        int i = item.itemID;
        int j = item.stackSize;
        int k = this.getSlotWithSpace(item);
        if (k < 0) {
            k = this.getFirstEmptyStack();
        }
        if (k < 0) {
            return j;
        }
        if (this.mainInventory[k] == null) {
            this.mainInventory[k] = new ItemStack(i, 0, item.getItemDamage());
        }
        int l = j;
        if (l > this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize) {
            l = this.mainInventory[k].getMaxStackSize() - this.mainInventory[k].stackSize;
        }
        if (l > this.getInventoryStackLimit() - this.mainInventory[k].stackSize) {
            l = this.getInventoryStackLimit() - this.mainInventory[k].stackSize;
        }
        if (l == 0) {
            return j;
        }
        j -= l;
        this.mainInventory[k].stackSize += l;
        this.mainInventory[k].animationsToGo = 5;
        return j;
    }
    
    public void decrementAnimations() {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null) {
                this.mainInventory[i].tick(this.player.worldObj, this.player, i, this.currentItem == i);
            }
        }
    }
    
    public boolean consumeInventoryItem(int item) {
        int n = this.getInventorySlotContainItem(item);
        if (n < 0) {
            return false;
        }
        if (--this.mainInventory[n].stackSize <= 0) {
            this.mainInventory[n] = null;
        }
        return true;
    }
    
    public boolean addItemStackToInventory(final ItemStack item) {
        if (!item.isItemDamaged()) {
            int stackSize;
            do {
                stackSize = item.stackSize;
                item.stackSize = this.addItemStack(item);
            } while (item.stackSize > 0 && item.stackSize < stackSize);
            return item.stackSize < stackSize;
        }
        final int firstEmptyStack = this.getFirstEmptyStack();
        if (firstEmptyStack >= 0) {
            this.mainInventory[firstEmptyStack] = ItemStack.copyOf(item);
            this.mainInventory[firstEmptyStack].animationsToGo = 5;
            item.stackSize = 0;
            return true;
        }
        return false;
    }
    
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack[] itemStackArray = this.mainInventory;
        if (slot >= this.mainInventory.length) {
            itemStackArray = this.armorInventory;
            slot -= this.mainInventory.length;
        }
        if (itemStackArray[slot] != null) {
            if (itemStackArray[slot].stackSize <= amount) {
                ItemStack itemStack = itemStackArray[slot];
                itemStackArray[slot] = null;
                return itemStack;
            }
            ItemStack itemStack = itemStackArray[slot].splitStack(amount);
            if (itemStackArray[slot].stackSize == 0) {
                itemStackArray[slot] = null;
            }
            return itemStack;
        }
        return null;
    }
    
    public void setInventorySlotContents(int slot, final ItemStack item) {
        ItemStack[] array = this.mainInventory;
        if (slot >= array.length) {
            slot -= array.length;
            array = this.armorInventory;
        }
        array[slot] = item;
    }
    
    public float getStrVsBlock(final Block block) {
        float n = 1.0f;
        if (this.mainInventory[this.currentItem] != null) {
            n *= this.mainInventory[this.currentItem].getStrVsBlock(block);
        }
        return n;
    }
    
    public NBTTagList writeToNBT(final NBTTagList list) {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null) {
                final NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound.setByte("Slot", (byte)i);
                this.mainInventory[i].writeToNBT(nbtTagCompound);
                list.setTag(nbtTagCompound);
            }
        }
        for (int j = 0; j < this.armorInventory.length; ++j) {
            if (this.armorInventory[j] != null) {
                final NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
                nbtTagCompound2.setByte("Slot", (byte)(j + 100));
                this.armorInventory[j].writeToNBT(nbtTagCompound2);
                list.setTag(nbtTagCompound2);
            }
        }
        return list;
    }
    
    public void readFromNBT(final NBTTagList list) {
        this.mainInventory = new ItemStack[36];
        this.armorInventory = new ItemStack[4];
        for (int i = 0; i < list.tagCount(); ++i) {
            final NBTTagCompound nbt = (NBTTagCompound)list.tagAt(i);
            final int n = nbt.getByte("Slot") & 0xFF;
            final ItemStack itemStack = new ItemStack(nbt);
            if (itemStack.getItem() != null) {
                if (n >= 0 && n < this.mainInventory.length) {
                    this.mainInventory[n] = itemStack;
                }
                if (n >= 100 && n < this.armorInventory.length + 100) {
                    this.armorInventory[n - 100] = itemStack;
                }
            }
        }
    }
    
    public int getSizeInventory() {
        return this.mainInventory.length + 4;
    }
    
    public ItemStack getStackInSlot(int slot) {
        ItemStack[] array = this.mainInventory;
        if (slot >= array.length) {
            slot -= array.length;
            array = this.armorInventory;
        }
        return array[slot];
    }
    
    public String getInvName() {
        return "Inventory";
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public int getDamageVsEntity(final Entity target) {
        final ItemStack stackInSlot = this.getStackInSlot(this.currentItem);
        if (stackInSlot != null) {
            return stackInSlot.getDamageVsEntity(target);
        }
        return 1;
    }
    
    public boolean canHarvestBlock(Block block) {
        if (block.blockMaterial.isToolNotRequired()) {
            return true;
        }
        ItemStack itemStack = this.getStackInSlot(this.currentItem);
        if (itemStack != null) {
            return itemStack.canHarvestBlock(block);
        }
        return false;
    }
    
    public int getTotalArmorValue() {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < this.armorInventory.length; ++i) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                final int maxDamage = this.armorInventory[i].getMaxDamage();
                n2 += maxDamage - this.armorInventory[i].getItemDamageForDisplay();
                n3 += maxDamage;
                n += ((ItemArmor)this.armorInventory[i].getItem()).damageReduceAmount;
            }
        }
        if (n3 == 0) {
            return 0;
        }
        return (n - 1) * n2 / n3 + 1;
    }
    
    public void damageArmor(final int damage) {
        for (int i = 0; i < this.armorInventory.length; ++i) {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor) {
                this.armorInventory[i].damageItem(damage, this.player);
                if (this.armorInventory[i].stackSize == 0) {
                    this.armorInventory[i].onRemoved(this.player);
                    this.armorInventory[i] = null;
                }
            }
        }
    }
    
    public void dropAllItems() {
        for (int i = 0; i < this.mainInventory.length; ++i) {
            if (this.mainInventory[i] != null) {
                this.player.dropPlayerItemWithRandomChoice(this.mainInventory[i], true);
                this.mainInventory[i] = null;
            }
        }
        for (int j = 0; j < this.armorInventory.length; ++j) {
            if (this.armorInventory[j] != null) {
                this.player.dropPlayerItemWithRandomChoice(this.armorInventory[j], true);
                this.armorInventory[j] = null;
            }
        }
    }
    
    public void onInventoryChanged() {
        this.inventoryChanged = true;
    }
    
    public void setItemStack(final ItemStack item) {
        this.itemStack = item;
        this.player.onItemStackChanged(item);
    }
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    public boolean canInteractWith(final EntityPlayer player) {
        if (this.player.isDead) {
            return false;
        }
        if (player.getDistanceSqToEntity(this.player) > 64.0) {
            return false;
        }
        return true;
    }
    
    public boolean contains(final ItemStack item) {
        for (int i = 0; i < this.armorInventory.length; ++i) {
            if (this.armorInventory[i] != null && this.armorInventory[i].is(item)) {
                return true;
            }
        }
        for (int j = 0; j < this.mainInventory.length; ++j) {
            if (this.mainInventory[j] != null && this.mainInventory[j].is(item)) {
                return true;
            }
        }
        return false;
    }
}
