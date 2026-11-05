package net.minecraft.src;


public class ContainerDispenser
extends Container {
    private TileEntityDispenser dispenser;

    public ContainerDispenser(IInventory playerInventory, TileEntityDispenser dispenser) {
        int n;
        int n2;
        this.dispenser = dispenser;
        for (n2 = 0; n2 < 3; ++n2) {
            for (n = 0; n < 3; ++n) {
                this.addSlot(new Slot(dispenser, n + n2 * 3, 62 + n * 18, 17 + n2 * 18));
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
    }

    public boolean canInteractWith(EntityPlayer player) {
        return this.dispenser.canInteractWith(player);
    }
}

