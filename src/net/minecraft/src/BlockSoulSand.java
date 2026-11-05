package net.minecraft.src;


public class BlockSoulSand
extends Block {
    public BlockSoulSand(int id, int sprite) {
        super(id, sprite, Material.sand);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float f = 0.125f;
        return AxisAlignedBB.getBoundingBoxFromPool(x, y, z, x + 1, (float)(y + 1) - f, z + 1);
    }

    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.4;
        entity.motionZ *= 0.4;
    }
}

