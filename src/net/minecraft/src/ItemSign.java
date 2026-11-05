package net.minecraft.src;


public class ItemSign
extends Item {
    public ItemSign(int n) {
        super(n);
        this.maxStackSize = 1;
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face == 0) {
            return false;
        }
        if (!world.getBlockMaterial(x, y, z).isSolid()) {
            return false;
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
        if (!Block.signPost.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }
        if (face == 1) {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.signPost.blockID, MathHelper.floor_double((double)((player.rotationYaw + 180.0f) * 16.0f / 360.0f) + 0.5) & 0xF);
        } else {
            world.setBlockAndMetadataWithNotify(x, y, z, Block.signWall.blockID, face);
        }
        --stack.stackSize;
        TileEntitySign tileEntitySign = (TileEntitySign)world.getBlockTileEntity(x, y, z);
        if (tileEntitySign != null) {
            player.displayGUIEditSign(tileEntitySign);
        }
        return true;
    }
}

