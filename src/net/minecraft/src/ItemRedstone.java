package net.minecraft.src;


public class ItemRedstone
extends Item {
    public ItemRedstone(int n) {
        super(n);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (world.getBlockId(x, y, z) != Block.snow.blockID) {
            if (face == 0) {
                --y;
            }
            if (face == 1) {
                ++y;
            }
            if (face == 2) {
                --z;
            }
            if (face == 3) {
                ++z;
            }
            if (face == 4) {
                --x;
            }
            if (face == 5) {
                ++x;
            }
            if (!world.isAirBlock(x, y, z)) {
                return false;
            }
        }
        if (Block.redstoneWire.canPlaceBlockAt(world, x, y, z)) {
            --stack.stackSize;
            world.setBlockWithNotify(x, y, z, Block.redstoneWire.blockID);
        }
        return true;
    }
}

