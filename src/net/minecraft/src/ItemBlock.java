package net.minecraft.src;


public class ItemBlock
extends Item {
    private int blockID;

    public ItemBlock(int n) {
        super(n);
        this.blockID = n + 256;
        this.setIconIndex(Block.blocksList[n + 256].getBlockTextureFromSide(2));
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
        if (y == 127 && Block.blocksList[this.blockID].blockMaterial.isSolid()) {
            return false;
        }
        if (world.canBlockBePlacedAt(this.blockID, x, y, z, false, face)) {
            Block block = Block.blocksList[this.blockID];
            if (world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, this.getMetadata(stack.getItemDamage()))) {
                Block.blocksList[this.blockID].onBlockPlaced(world, x, y, z, face);
                Block.blocksList[this.blockID].onBlockPlacedBy(world, x, y, z, player);
                world.playSoundEffect((float)x + 0.5f, (float)y + 0.5f, (float)z + 0.5f, block.stepSound.getStepping(), (block.stepSound.getVolume() + 1.0f) / 2.0f, block.stepSound.getPitch() * 0.8f);
                --stack.stackSize;
            }
            return true;
        }
        return false;
    }

    public String getItemName() {
        return Block.blocksList[this.blockID].getBlockName();
    }
}

