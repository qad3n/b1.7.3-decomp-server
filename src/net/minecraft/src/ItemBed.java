package net.minecraft.src;


public class ItemBed
extends Item {
    public ItemBed(int n) {
        super(n);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face != 1) {
            return false;
        }
        ++y;
        BlockBed blockBed = (BlockBed)Block.bed;
        int n = MathHelper.floor_double((double)(player.rotationYaw * 4.0f / 360.0f) + 0.5) & 3;
        int n2 = 0;
        int n3 = 0;
        if (n == 0) {
            n3 = 1;
        }
        if (n == 1) {
            n2 = -1;
        }
        if (n == 2) {
            n3 = -1;
        }
        if (n == 3) {
            n2 = 1;
        }
        if (world.isAirBlock(x, y, z) && world.isAirBlock(x + n2, y, z + n3) && world.isBlockNormalCube(x, y - 1, z) && world.isBlockNormalCube(x + n2, y - 1, z + n3)) {
            world.setBlockAndMetadataWithNotify(x, y, z, blockBed.blockID, n);
            world.setBlockAndMetadataWithNotify(x + n2, y, z + n3, blockBed.blockID, n + 8);
            --stack.stackSize;
            return true;
        }
        return false;
    }
}

