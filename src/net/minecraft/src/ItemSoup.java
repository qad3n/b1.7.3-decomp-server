package net.minecraft.src;


public class ItemSoup
extends ItemFood {
    public ItemSoup(int id, int hungerPoints) {
        super(id, hungerPoints, false);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        super.onItemRightClick(stack, world, player);
        return new ItemStack(Item.bowlEmpty);
    }
}

