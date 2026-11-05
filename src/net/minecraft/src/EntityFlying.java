package net.minecraft.src;


public class EntityFlying
extends EntityLiving {
    public EntityFlying(World world) {
        super(world);
    }

    protected void fall(float distance) {
    }

    public void moveEntityWithHeading(float sideways, float forwards) {
        if (this.isInWater()) {
            this.moveFlying(sideways, forwards, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)0.8f;
            this.motionY *= (double)0.8f;
            this.motionZ *= (double)0.8f;
        } else if (this.handleLavaMovement()) {
            this.moveFlying(sideways, forwards, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        } else {
            float f = 0.91f;
            if (this.onGround) {
                f = 0.54600006f;
                int n = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
                if (n > 0) {
                    f = Block.blocksList[n].slipperiness * 0.91f;
                }
            }
            float f2 = 0.16277136f / (f * f * f);
            this.moveFlying(sideways, forwards, this.onGround ? 0.1f * f2 : 0.02f);
            f = 0.91f;
            if (this.onGround) {
                f = 0.54600006f;
                int n = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
                if (n > 0) {
                    f = Block.blocksList[n].slipperiness * 0.91f;
                }
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f;
            this.motionY *= (double)f;
            this.motionZ *= (double)f;
        }
        this.lastWalkAnimationSpeed = this.walkAnimationSpeed;
        double d = this.posX - this.prevPosX;
        double d2 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.walkAnimationSpeed += (f - this.walkAnimationSpeed) * 0.4f;
        this.walkAnimationProgress += this.walkAnimationSpeed;
    }

    public boolean isOnLadder() {
        return false;
    }
}

