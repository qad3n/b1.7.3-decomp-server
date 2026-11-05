package net.minecraft.src;


public class EntityChicken
extends EntityAnimal {
    public boolean sheared = false;
    public float flapProgress = 0.0f;
    public float flapSpeed = 0.0f;
    public float lastFlapSpeed;
    public float lastFlapProgress;
    public float flapping = 1.0f;
    public int timeUntilNextEgg;

    public EntityChicken(World world) {
        super(world);
        this.texture = "/mob/chicken.png";
        this.setSize(0.3f, 0.4f);
        this.health = 4;
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.lastFlapProgress = this.flapProgress;
        this.lastFlapSpeed = this.flapSpeed;
        this.flapSpeed = (float)((double)this.flapSpeed + (double)(this.onGround ? -1 : 4) * 0.3);
        if (this.flapSpeed < 0.0f) {
            this.flapSpeed = 0.0f;
        }
        if (this.flapSpeed > 1.0f) {
            this.flapSpeed = 1.0f;
        }
        if (!this.onGround && this.flapping < 1.0f) {
            this.flapping = 1.0f;
        }
        this.flapping = (float)((double)this.flapping * 0.9);
        if (!this.onGround && this.motionY < 0.0) {
            this.motionY *= 0.6;
        }
        this.flapProgress += this.flapping * 2.0f;
        if (!this.worldObj.singleplayerWorld && --this.timeUntilNextEgg <= 0) {
            this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
            this.dropItem(Item.egg.shiftedIndex, 1);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }

    protected void fall(float distance) {
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    protected String getLivingSound() {
        return "mob.chicken";
    }

    protected String getHurtSound() {
        return "mob.chickenhurt";
    }

    protected String getDeathSound() {
        return "mob.chickenhurt";
    }

    protected int getDropItemId() {
        return Item.feather.shiftedIndex;
    }
}

