package net.minecraft.src;


public class EntityPig
extends EntityAnimal {
    public EntityPig(World world) {
        super(world);
        this.texture = "/mob/pig.png";
        this.setSize(0.9f, 0.9f);
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (byte)0);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Saddle", this.getSaddled());
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSaddled(nbt.getBoolean("Saddle"));
    }

    protected String getLivingSound() {
        return "mob.pig";
    }

    protected String getHurtSound() {
        return "mob.pig";
    }

    protected String getDeathSound() {
        return "mob.pigdeath";
    }

    public boolean interact(EntityPlayer player) {
        if (this.getSaddled() && !this.worldObj.singleplayerWorld && (this.riddenByEntity == null || this.riddenByEntity == player)) {
            player.mountEntity(this);
            return true;
        }
        return false;
    }

    protected int getDropItemId() {
        if (this.fire > 0) {
            return Item.porkCooked.shiftedIndex;
        }
        return Item.porkRaw.shiftedIndex;
    }

    public boolean getSaddled() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setSaddled(boolean saddled) {
        if (saddled) {
            this.dataWatcher.updateObject(16, (byte)1);
        } else {
            this.dataWatcher.updateObject(16, (byte)0);
        }
    }

    public void onStruckByLightning(EntityLightningBolt lightning) {
        if (this.worldObj.singleplayerWorld) {
            return;
        }
        EntityPigZombie entityPigZombie = new EntityPigZombie(this.worldObj);
        entityPigZombie.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        this.worldObj.entityJoinedWorld(entityPigZombie);
        this.setEntityDead();
    }

    protected void fall(float distance) {
        super.fall(distance);
        if (distance > 5.0f && this.riddenByEntity instanceof EntityPlayer) {
            ((EntityPlayer)this.riddenByEntity).incrementStat(AchievementList.RIDE_PIG_OFF_CLIFF);
        }
    }
}

