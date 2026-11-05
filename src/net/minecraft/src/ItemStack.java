package net.minecraft.src;


public final class ItemStack {
    public int stackSize = 0;
    public int animationsToGo;
    public int itemID;
    private int itemDamage;

    public ItemStack(Block block) {
        this(block, 1);
    }

    public ItemStack(Block block, int size) {
        this(block.blockID, size, 0);
    }

    public ItemStack(Block block, int size, int metadata) {
        this(block.blockID, size, metadata);
    }

    public ItemStack(Item item) {
        this(item.shiftedIndex, 1, 0);
    }

    public ItemStack(Item item, int size) {
        this(item.shiftedIndex, size, 0);
    }

    public ItemStack(Item item, int size, int metadata) {
        this(item.shiftedIndex, size, metadata);
    }

    public ItemStack(int id, int size, int metadata) {
        this.itemID = id;
        this.stackSize = size;
        this.itemDamage = metadata;
    }

    public ItemStack(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    public ItemStack splitStack(int amount) {
        this.stackSize -= amount;
        return new ItemStack(this.itemID, amount, this.itemDamage);
    }

    public Item getItem() {
        return Item.itemsList[this.itemID];
    }

    public boolean useItem(EntityPlayer player, World world, int x, int y, int z, int face) {
        boolean bl = this.getItem().onItemUse(this, player, world, x, y, z, face);
        if (bl) {
            player.addStat(StatList.ITEMS_USED[this.itemID], 1);
        }
        return bl;
    }

    public float getStrVsBlock(Block block) {
        return this.getItem().getStrVsBlock(this, block);
    }

    public ItemStack useItemRightClick(World world, EntityPlayer player) {
        return this.getItem().onItemRightClick(this, world, player);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setShort("id", (short)this.itemID);
        nbt.setByte("Count", (byte)this.stackSize);
        nbt.setShort("Damage", (short)this.itemDamage);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        this.itemID = nbt.getShort("id");
        this.stackSize = nbt.getByte("Count");
        this.itemDamage = nbt.getShort("Damage");
    }

    public int getMaxStackSize() {
        return this.getItem().getItemStackLimit();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
    }

    public boolean isItemStackDamageable() {
        return Item.itemsList[this.itemID].getMaxDamage() > 0;
    }

    public boolean getHasSubtypes() {
        return Item.itemsList[this.itemID].getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return this.isItemStackDamageable() && this.itemDamage > 0;
    }

    public int getItemDamageForDisplay() {
        return this.itemDamage;
    }

    public int getItemDamage() {
        return this.itemDamage;
    }

    public void setItemDamage(int damage) {
        this.itemDamage = damage;
    }

    public int getMaxDamage() {
        return Item.itemsList[this.itemID].getMaxDamage();
    }

    public void damageItem(int amount, Entity holder) {
        if (!this.isItemStackDamageable()) {
            return;
        }
        this.itemDamage += amount;
        if (this.itemDamage > this.getMaxDamage()) {
            if (holder instanceof EntityPlayer) {
                ((EntityPlayer)holder).addStat(StatList.ITEMS_BROKEN[this.itemID], 1);
            }
            --this.stackSize;
            if (this.stackSize < 0) {
                this.stackSize = 0;
            }
            this.itemDamage = 0;
        }
    }

    public void hitEntity(EntityLiving target, EntityPlayer attacker) {
        boolean bl = Item.itemsList[this.itemID].hitEntity(this, target, attacker);
        if (bl) {
            attacker.addStat(StatList.ITEMS_USED[this.itemID], 1);
        }
    }

    public void mineBlock(int block, int x, int y, int z, EntityPlayer player) {
        boolean bl = Item.itemsList[this.itemID].mineBlock(this, block, x, y, z, player);
        if (bl) {
            player.addStat(StatList.ITEMS_USED[this.itemID], 1);
        }
    }

    public int getDamageVsEntity(Entity target) {
        return Item.itemsList[this.itemID].getDamageVsEntity(target);
    }

    public boolean canHarvestBlock(Block block) {
        return Item.itemsList[this.itemID].canHarvestBlock(block);
    }

    public void onRemoved(EntityPlayer player) {
    }

    public void useItemOnEntity(EntityLiving entity) {
        Item.itemsList[this.itemID].saddleEntity(this, entity);
    }

    public ItemStack copy() {
        return new ItemStack(this.itemID, this.stackSize, this.itemDamage);
    }

    public static boolean areItemStacksEqual(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null && stack2 == null) {
            return true;
        }
        if (stack1 == null || stack2 == null) {
            return false;
        }
        return stack1.isItemStackEqual(stack2);
    }

    private boolean isItemStackEqual(ItemStack other) {
        if (this.stackSize != other.stackSize) {
            return false;
        }
        if (this.itemID != other.itemID) {
            return false;
        }
        return this.itemDamage == other.itemDamage;
    }

    public boolean isItemEqual(ItemStack other) {
        return this.itemID == other.itemID && this.itemDamage == other.itemDamage;
    }

    public static ItemStack copyOf(ItemStack stack) {
        return stack == null ? null : stack.copy();
    }

    public String toString() {
        return this.stackSize + "x" + Item.itemsList[this.itemID].getItemName() + "@" + this.itemDamage;
    }

    public void tick(World world, Entity entity, int slot, boolean selected) {
        if (this.animationsToGo > 0) {
            --this.animationsToGo;
        }
        Item.itemsList[this.itemID].tick(this, world, entity, slot, selected);
    }

    public void onResult(World world, EntityPlayer player) {
        player.addStat(StatList.ITEMS_CRAFTED[this.itemID], this.stackSize);
        Item.itemsList[this.itemID].onResult(this, world, player);
    }

    public boolean is(ItemStack other) {
        return this.itemID == other.itemID && this.stackSize == other.stackSize && this.itemDamage == other.itemDamage;
    }
}

