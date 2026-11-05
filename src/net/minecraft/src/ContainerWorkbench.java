package net.minecraft.src;


public class ContainerWorkbench
extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private World world;
    private int x;
    private int y;
    private int z;

    public ContainerWorkbench(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        int n;
        int n2;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.addSlot(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.addSlot(new Slot(this.craftMatrix, n + n2 * 3, 30 + n * 18, 17 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + n2 * 9 + 9, 8 + n * 18, 84 + n2 * 18));
            }
        }
        for (n2 = 0; n2 < 9; ++n2) {
            this.addSlot(new Slot(playerInventory, n2, 8 + n2 * 18, 142));
        }
        this.onCraftMatrixChanged(this.craftMatrix);
    }

    public void onCraftMatrixChanged(IInventory inventory) {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
    }

    @Override
public void onCraftGuiClosed(final EntityPlayer player) {
    super.onCraftGuiClosed(player);
    if (this.world.singleplayerWorld) {
        return;
    }
    for (int i = 0; i < 9; ++i) {
        final ItemStack stackInSlot = this.craftMatrix.getStackInSlot(i);
        if (stackInSlot != null) {
            player.dropPlayerItem(stackInSlot);
        }
    }
}

    public boolean canInteractWith(EntityPlayer player) {
        if (this.world.getBlockId(this.x, this.y, this.z) != Block.workbench.blockID) {
            return false;
        }
        return !(player.getDistanceSq((double)this.x + 0.5, (double)this.y + 0.5, (double)this.z + 0.5) > 64.0);
    }

    public ItemStack quickMoveItem(int slot) {
        ItemStack itemStack = null;
        Slot slot2 = (Slot)this.inventorySlots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot == 0) {
                this.moveItem(itemStack2, 10, 46, true);
            } else if (slot >= 10 && slot < 37) {
                this.moveItem(itemStack2, 37, 46, false);
            } else if (slot >= 37 && slot < 46) {
                this.moveItem(itemStack2, 10, 37, false);
            } else {
                this.moveItem(itemStack2, 10, 46, false);
            }
            if (itemStack2.stackSize == 0) {
                slot2.putStack(null);
            } else {
                slot2.onSlotChanged();
            }
            if (itemStack2.stackSize != itemStack.stackSize) {
                slot2.onPickupFromSlot(itemStack2);
            } else {
                return null;
            }
        }
        return itemStack;
    }
}

