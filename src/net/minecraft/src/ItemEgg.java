package net.minecraft.src;


public class ItemEgg
extends Item {
    public ItemEgg(int n) {
        super(n);
        this.maxStackSize = 16;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        --stack.stackSize;
        world.playSoundAtEntity(player, "random.bow", 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        if (!world.singleplayerWorld) {
            world.entityJoinedWorld(new EntityEgg(world, player));
        }
        return stack;
    }
}

