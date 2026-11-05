package net.minecraft.src;


public class ItemHoe
extends Item {
    public ItemHoe(int id, Item.EnumToolMaterial tier) {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(tier.getMaxUses());
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        int n = world.getBlockId(x, y, z);
        int n2 = world.getBlockId(x, y + 1, z);
        if (face != 0 && n2 == 0 && n == Block.grass.blockID || n == Block.dirt.blockID) {
            Block block = Block.tilledField;
            world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.stepSound.getStepping(), (block.stepSound.getVolume() + 1.0f) / 2.0f, block.stepSound.getPitch() * 0.8f);
            if (world.singleplayerWorld) {
                return true;
            }
            world.setBlockWithNotify(x, y, z, block.blockID);
            stack.damageItem(1, player);
            return true;
        }
        return false;
    }
}

