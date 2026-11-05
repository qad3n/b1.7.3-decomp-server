package net.minecraft.src;


public class EntityFallingSand
extends Entity {
    public int blockID;
    public int fallTime = 0;

    public EntityFallingSand(World world) {
        super(world);
    }

    public EntityFallingSand(World world, double x, double y, double z, int block) {
        super(world);
        this.blockID = block;
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
        this.yOffset = this.height / 2.0f;
        this.setPosition(x, y, z);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    protected boolean makesSteps() {
        return false;
    }

    protected void entityInit() {
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        if (this.blockID == 0) {
            this.setEntityDead();
            return;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.fallTime;
        this.motionY -= (double)0.04f;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= (double)0.98f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.98f;
        int n = MathHelper.floor_double(this.posX);
        int n2 = MathHelper.floor_double(this.posY);
        int n3 = MathHelper.floor_double(this.posZ);
        if (this.worldObj.getBlockId(n, n2, n3) == this.blockID) {
            this.worldObj.setBlockWithNotify(n, n2, n3, 0);
        }
        if (this.onGround) {
            this.motionX *= (double)0.7f;
            this.motionZ *= (double)0.7f;
            this.motionY *= -0.5;
            this.setEntityDead();
            if (this.worldObj.canBlockBePlacedAt(this.blockID, n, n2, n3, true, 1) && !BlockSand.canFallBelow(this.worldObj, n, n2 - 1, n3) && this.worldObj.setBlockWithNotify(n, n2, n3, this.blockID) || this.worldObj.singleplayerWorld) return;
            this.dropItem(this.blockID, 1);
        } else {
            if (this.fallTime <= 100 || this.worldObj.singleplayerWorld) return;
            this.dropItem(this.blockID, 1);
            this.setEntityDead();
        }
    }

    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setByte("Tile", (byte)this.blockID);
    }

    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.blockID = nbt.getByte("Tile") & 0xFF;
    }
}

