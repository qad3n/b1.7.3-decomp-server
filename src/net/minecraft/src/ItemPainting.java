package net.minecraft.src;

public class ItemPainting extends Item {
    public ItemPainting(int var1) {
        super(var1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face == 0) {
            return false;
        }

        if (face == 1) {
            return false;
        }

        byte b0 = 0;
        if (face == 4) {
            b0 = 1;
        }

        if (face == 3) {
            b0 = 2;
        }

        if (face == 5) {
            b0 = 3;
        }

        EntityPainting entitypainting = new EntityPainting(world, x, y, z, b0);
        if (entitypainting.onValidSurface()) {
            if (!world.singleplayerWorld) {
                world.entityJoinedWorld(entitypainting);
            }

            stack.stackSize--;
        }

        return true;
    }
}
