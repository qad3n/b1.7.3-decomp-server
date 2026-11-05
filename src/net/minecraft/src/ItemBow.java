package net.minecraft.src;


public class ItemBow
extends Item {
    public ItemBow(int n) {
        super(n);
        this.maxStackSize = 1;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.inventory.consumeInventoryItem(Item.arrow.shiftedIndex)) {
            world.playSoundAtEntity(player, "random.bow", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 0.8f));
            if (!world.singleplayerWorld) {
                world.entityJoinedWorld(new EntityArrow(world, player));
            }
        }
        return stack;
    }
}

