package net.minecraft.src;

public class EntitySkeleton extends EntityMob {
    private static final ItemStack defaultHeldItem = new ItemStack(Item.bow, 1);

    public EntitySkeleton(World var1) {
        super(var1);
        this.texture = "/mob/skeleton.png";
    }

    @Override
    protected String getLivingSound() {
        return "mob.skeleton";
    }

    @Override
    protected String getHurtSound() {
        return "mob.skeletonhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.skeletonhurt";
    }

    @Override
    public void onLivingUpdate() {
        if (this.worldObj.isDaytime()) {
            float f = this.getEntityBrightness(1.0F);
            if (f > 0.5F
                && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.fire = 300;
            }
        }

        super.onLivingUpdate();
    }

    @Override
    protected void attackEntity(Entity target, float distance) {
        if (distance < 10.0F) {
            double d0 = target.posX - this.posX;
            double d1 = target.posZ - this.posZ;
            if (this.attackTime == 0) {
                EntityArrow entityarrow = new EntityArrow(this.worldObj, this);
                entityarrow.posY += 1.4F;
                double d2 = target.posY + target.getEyeHeight() - 0.2F - entityarrow.posY;
                float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1) * 0.2F;
                this.worldObj.playSoundAtEntity(this, "random.bow", 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
                this.worldObj.entityJoinedWorld(entityarrow);
                entityarrow.setArrowHeading(d0, d2 + f, d1, 0.6F, 12.0F);
                this.attackTime = 30;
            }

            this.rotationYaw = (float)(Math.atan2(d1, d0) * 180.0 / (float) Math.PI) - 90.0F;
            this.hasAttacked = true;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
    protected int getDropItemId() {
        return Item.arrow.shiftedIndex;
    }

    @Override
    protected void dropFewItems() {
        int i = this.rand.nextInt(3);

        for (int j = 0; j < i; j++) {
            this.dropItem(Item.arrow.shiftedIndex, 1);
        }

        i = this.rand.nextInt(3);

        for (int k = 0; k < i; k++) {
            this.dropItem(Item.bone.shiftedIndex, 1);
        }
    }
}
