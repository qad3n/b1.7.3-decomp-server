package net.minecraft.src;

public class ItemFlintAndSteel extends Item {
    public ItemFlintAndSteel(int var1) {
        super(var1);
        this.maxStackSize = 1;
        this.setMaxDamage(64);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face == 0) {
            y--;
        }

        if (face == 1) {
            y++;
        }

        if (face == 2) {
            z--;
        }

        if (face == 3) {
            z++;
        }

        if (face == 4) {
            x--;
        }

        if (face == 5) {
            x++;
        }

        int i = world.getBlockId(x, y, z);
        if (i == 0) {
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlockWithNotify(x, y, z, Block.fire.blockID);
        }

        stack.damageItem(1, player);
        return true;
    }
}
