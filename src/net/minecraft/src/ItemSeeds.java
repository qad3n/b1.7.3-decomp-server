package net.minecraft.src;


public class ItemSeeds
extends Item {
    private int plantBlock;

    public ItemSeeds(int item, int plantBlock) {
        super(item);
        this.plantBlock = plantBlock;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face != 1) {
            return false;
        }
        int n = world.getBlockId(x, y, z);
        if (n == Block.tilledField.blockID && world.isAirBlock(x, y + 1, z)) {
            world.setBlockWithNotify(x, y + 1, z, this.plantBlock);
            --stack.stackSize;
            return true;
        }
        return false;
    }
}

