package net.minecraft.src;


public class ItemMinecart
extends Item {
    public int minecartType;

    public ItemMinecart(int id, int n) {
        super(id);
        this.maxStackSize = 1;
        this.minecartType = n;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        int n = world.getBlockId(x, y, z);
        if (BlockRail.isRail(n)) {
            if (!world.singleplayerWorld) {
                world.entityJoinedWorld(new EntityMinecart(world, (float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, this.minecartType));
            }
            --stack.stackSize;
            return true;
        }
        return false;
    }
}

