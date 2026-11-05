package net.minecraft.src;


public class ItemMapBase
extends Item {
    protected ItemMapBase(int n) {
        super(n);
    }

    public boolean isNetworkSynced() {
        return true;
    }

    public Packet getUpdatePacket(ItemStack stack, World world, EntityPlayer player) {
        return null;
    }
}

