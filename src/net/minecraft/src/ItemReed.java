package net.minecraft.src;


public class ItemReed
extends Item {
    private int block;

    public ItemReed(int id, Block block) {
        super(id);
        this.block = block.blockID;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (world.getBlockId(x, y, z) == Block.snow.blockID) {
            face = 0;
        } else {
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
        }
        if (stack.stackSize == 0) {
            return false;
        }
        if (world.canBlockBePlacedAt(this.block, x, y, z, false, face)) {
            Block block = Block.blocksList[this.block];
            if (world.setBlockWithNotify(x, y, z, this.block)) {
                Block.blocksList[this.block].onBlockPlaced(world, x, y, z, face);
                Block.blocksList[this.block].onBlockPlacedBy(world, x, y, z, player);
                world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.stepSound.getStepping(), (block.stepSound.getVolume() + 1.0f) / 2.0f, block.stepSound.getPitch() * 0.8f);
                --stack.stackSize;
            }
        }
        return true;
    }
}

