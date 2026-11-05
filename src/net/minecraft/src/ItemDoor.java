package net.minecraft.src;

public class ItemDoor extends Item {
    private Material material;

    public ItemDoor(int id, Material var2) {
        super(id);
        this.material = var2;
        this.maxStackSize = 1;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face) {
        if (face != 1) {
            return false;
        }

        y++;
        Block block;
        if (this.material == Material.wood) {
            block = Block.doorWood;
        } else {
            block = Block.doorSteel;
        }

        if (!block.canPlaceBlockAt(world, x, y, z)) {
            return false;
        }

        int i = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5) & 3;
        byte b0 = 0;
        byte b1 = 0;
        if (i == 0) {
            b1 = 1;
        }

        if (i == 1) {
            b0 = -1;
        }

        if (i == 2) {
            b1 = -1;
        }

        if (i == 3) {
            b0 = 1;
        }

        int j = (world.isBlockNormalCube(x - b0, y, z - b1) ? 1 : 0) + (world.isBlockNormalCube(x - b0, y + 1, z - b1) ? 1 : 0);
        int k = (world.isBlockNormalCube(x + b0, y, z + b1) ? 1 : 0) + (world.isBlockNormalCube(x + b0, y + 1, z + b1) ? 1 : 0);
        boolean flag = world.getBlockId(x - b0, y, z - b1) == block.blockID || world.getBlockId(x - b0, y + 1, z - b1) == block.blockID;
        boolean flag1 = world.getBlockId(x + b0, y, z + b1) == block.blockID || world.getBlockId(x + b0, y + 1, z + b1) == block.blockID;
        boolean flag2 = false;
        if (flag && !flag1) {
            flag2 = true;
        } else if (k > j) {
            flag2 = true;
        }

        if (flag2) {
            i = i - 1 & 3;
            i += 4;
        }

        world.editingBlocks = true;
        world.setBlockAndMetadataWithNotify(x, y, z, block.blockID, i);
        world.setBlockAndMetadataWithNotify(x, y + 1, z, block.blockID, i + 8);
        world.editingBlocks = false;
        world.notifyBlocksOfNeighborChange(x, y, z, block.blockID);
        world.notifyBlocksOfNeighborChange(x, y + 1, z, block.blockID);
        stack.stackSize--;
        return true;
    }
}
