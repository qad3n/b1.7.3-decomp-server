package net.minecraft.src;


public class BlockFence
extends Block {
    public BlockFence(int id, int sprite) {
        super(id, sprite, Material.wood);
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        if (world.getBlockId(x, y - 1, z) == this.blockID) {
            return true;
        }
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            return false;
        }
        return super.canPlaceBlockAt(world, x, y, z);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, x + 1, (float)y + 1.5f, z + 1);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }
}

