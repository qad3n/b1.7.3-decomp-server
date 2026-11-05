package net.minecraft.src;

public class ItemBucket extends Item {
    private int isFull;

    public ItemBucket(int id, int liquid) {
        super(id);
        this.maxStackSize = 1;
        this.isFull = liquid;
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
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks_do(vec3d, vec3d1, this.isFull == 0);
        if (movingobjectposition == null) {
            return stack;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.EnumMovingObjectType.TILE) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;
            if (!world.canMineBlock(player, i, j, k)) {
                return stack;
            }

            if (this.isFull == 0) {
                if (world.getBlockMaterial(i, j, k) == Material.water && world.getBlockMetadata(i, j, k) == 0) {
                    world.setBlockWithNotify(i, j, k, 0);
                    return new ItemStack(Item.bucketWater);
                }

                if (world.getBlockMaterial(i, j, k) == Material.lava && world.getBlockMetadata(i, j, k) == 0) {
                    world.setBlockWithNotify(i, j, k, 0);
                    return new ItemStack(Item.bucketLava);
                }
            } else {
                if (this.isFull < 0) {
                    return new ItemStack(Item.bucketEmpty);
                }

                if (movingobjectposition.sideHit == 0) {
                    j--;
                }

                if (movingobjectposition.sideHit == 1) {
                    j++;
                }

                if (movingobjectposition.sideHit == 2) {
                    k--;
                }

                if (movingobjectposition.sideHit == 3) {
                    k++;
                }

                if (movingobjectposition.sideHit == 4) {
                    i--;
                }

                if (movingobjectposition.sideHit == 5) {
                    i++;
                }

                if (world.isAirBlock(i, j, k) || !world.getBlockMaterial(i, j, k).isSolid()) {
                    if (world.worldProvider.isHellWorld && this.isFull == Block.waterMoving.blockID) {
                        world.playSoundEffect(
                            d0 + 0.5, d1 + 0.5, d2 + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
                        );

                        for (int l = 0; l < 8; l++) {
                            world.spawnParticle("largesmoke", i + Math.random(), j + Math.random(), k + Math.random(), 0.0, 0.0, 0.0);
                        }
                    } else {
                        world.setBlockAndMetadataWithNotify(i, j, k, this.isFull, 0);
                    }

                    return new ItemStack(Item.bucketEmpty);
                }
            }
        } else if (this.isFull == 0 && movingobjectposition.entityHit instanceof EntityCow) {
            return new ItemStack(Item.bucketMilk);
        }

        return stack;
    }
}
