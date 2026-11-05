package net.minecraft.src;

public class ContainerPlayer extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public boolean isMP = false;

    public ContainerPlayer(InventoryPlayer inventory) {
        this(inventory, true);
    }

    public ContainerPlayer(InventoryPlayer inventory, boolean active) {
        this.isMP = active;
        this.addSlot(new SlotCrafting(inventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlot(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }

        for (int k = 0; k < 4; k++) {
            final int j1 = k;
            this.addSlot(new Slot(inventory, inventory.getSizeInventory() - 1 - k, 8, 8 + k * 18) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack item) {
                    if (item.getItem() instanceof ItemArmor) {
                        return ((ItemArmor)item.getItem()).armorType == j1;
                    } else {
                        return item.getItem().shiftedIndex == Block.pumpkin.blockID ? j1 == 0 : false;
                    }
                }
            });
        }

        for (int l = 0; l < 3; l++) {
            for (int k1 = 0; k1 < 9; k1++) {
                this.addSlot(new Slot(inventory, k1 + (l + 1) * 9, 8 + k1 * 18, 84 + l * 18));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(inventory, i1, 8 + i1 * 18, 142));
        }

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
    }

    @Override
    public void onCraftGuiClosed(EntityPlayer player) {
        super.onCraftGuiClosed(player);

        for (int i = 0; i < 4; i++) {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            if (itemstack != null) {
                player.dropPlayerItem(itemstack);
                this.craftMatrix.setInventorySlotContents(i, null);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack quickMoveItem(int slot) {
        ItemStack itemstack = null;
        Slot slotx = this.inventorySlots.get(slot);
        if (slotx != null && slotx.hasItem()) {
            ItemStack itemstack1 = slotx.getStack();
            itemstack = itemstack1.copy();
            if (slot == 0) {
                this.moveItem(itemstack1, 9, 45, true);
            } else if (slot >= 9 && slot < 36) {
                this.moveItem(itemstack1, 36, 45, false);
            } else if (slot >= 36 && slot < 45) {
                this.moveItem(itemstack1, 9, 36, false);
            } else {
                this.moveItem(itemstack1, 9, 45, false);
            }

            if (itemstack1.stackSize == 0) {
                slotx.putStack(null);
            } else {
                slotx.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slotx.onPickupFromSlot(itemstack1);
        }

        return itemstack;
    }
}
