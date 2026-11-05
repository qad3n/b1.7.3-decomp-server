package net.minecraft.src;

public class ItemBoat extends Item {
    public ItemBoat(int var1) {
        super(var1);
        this.maxStackSize = 1;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        float f = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d1 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62 - player.yOffset;
        double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3D vec3d = Vec3D.createVector(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * (float) (Math.PI / 180.0));
        float f6 = MathHelper.sin(-f1 * (float) (Math.PI / 180.0));
        float f7 = f4 * f5;
        float f8 = f6;
        float f9 = f3 * f5;
        double d3 = 5.0;
        Vec3D vec3d1 = vec3d.addVector(f7 * d3, f8 * d3, f9 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks_do(vec3d, vec3d1, true);
        if (movingobjectposition == null) {
            return stack;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.EnumMovingObjectType.TILE) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if (!world.singleplayerWorld) {
                if (world.getBlockId(i, j, k) == Block.snow.blockID) {
                    j--;
                }

                world.entityJoinedWorld(new EntityBoat(world, i + 0.5F, j + 1.0F, k + 0.5F));
            }

            stack.stackSize--;
        }

        return stack;
    }
}
