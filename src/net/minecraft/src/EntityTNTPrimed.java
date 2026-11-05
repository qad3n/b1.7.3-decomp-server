package net.minecraft.src;


public class EntityTNTPrimed
extends Entity {
    public int fuse = 0;

    public EntityTNTPrimed(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
        this.yOffset = this.height / 2.0f;
    }

    public EntityTNTPrimed(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * 3.1415927410125732 * 2.0);
        this.motionX = -MathHelper.sin(f * (float)Math.PI / 180.0f) * 0.02f;
        this.motionY = 0.2f;
        this.motionZ = -MathHelper.cos(f * (float)Math.PI / 180.0f) * 0.02f;
        this.fuse = 80;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    protected void entityInit() {
    }

    protected boolean makesSteps() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)0.04f;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
            this.motionY *= -0.5;
        }
        if (this.fuse-- <= 0) {
            if (!this.worldObj.singleplayerWorld) {
                this.setEntityDead();
                this.explode();
            } else {
                this.setEntityDead();
            }
        } else {
            this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        }
    }

    private void explode() {
        float f = 4.0f;
        this.worldObj.createExplosion(null, this.posX, this.posY, this.posZ, f);
    }

    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setByte("Fuse", (byte)this.fuse);
    }

    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.fuse = nbt.getByte("Fuse");
    }
}

