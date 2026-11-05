package net.minecraft.src;


public class EntitySpider
extends EntityMob {
    public EntitySpider(World world) {
        super(world);
        this.texture = "/mob/spider.png";
        this.setSize(1.4f, 0.9f);
        this.moveSpeed = 0.8f;
    }

    public double getMountedYOffset() {
        return (double)this.height * 0.75 - 0.5;
    }

    protected boolean makesSteps() {
        return false;
    }

    protected Entity findPlayerToAttack() {
        float f = this.getEntityBrightness(1.0f);
        if (f < 0.5f) {
            double d = 16.0;
            return this.worldObj.getClosestPlayerToEntity(this, d);
        }
        return null;
    }

    protected String getLivingSound() {
        return "mob.spider";
    }

    protected String getHurtSound() {
        return "mob.spider";
    }

    protected String getDeathSound() {
        return "mob.spiderdeath";
    }

    protected void attackEntity(Entity target, float distance) {
        float f = this.getEntityBrightness(1.0f);
        if (f > 0.5f && this.rand.nextInt(100) == 0) {
            this.playerToAttack = null;
            return;
        }
        if (distance > 2.0f && distance < 6.0f && this.rand.nextInt(10) == 0) {
            if (this.onGround) {
                double d = target.posX - this.posX;
                double d2 = target.posZ - this.posZ;
                float f2 = MathHelper.sqrt_double(d * d + d2 * d2);
                this.motionX = d / (double)f2 * 0.5 * (double)0.8f + this.motionX * (double)0.2f;
                this.motionZ = d2 / (double)f2 * 0.5 * (double)0.8f + this.motionZ * (double)0.2f;
                this.motionY = 0.4f;
            }
        } else {
            super.attackEntity(target, distance);
        }
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    protected int getDropItemId() {
        return Item.silk.shiftedIndex;
    }

    public boolean isOnLadder() {
        return this.isCollidedHorizontally;
    }
}

