package net.minecraft.src;


public class EntityCreeper
extends EntityMob {
    int timeSinceIgnited;
    int lastActiveTime;

    public EntityCreeper(World world) {
        super(world);
        this.texture = "/mob/creeper.png";
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)-1);
        this.dataWatcher.addObject(17, (byte)0);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (this.dataWatcher.getWatchableObjectByte(17) == 1) {
            nbt.setBoolean("powered", true);
        }
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataWatcher.updateObject(17, (byte)(nbt.getBoolean("powered") ? 1 : 0));
    }

    protected void targetOutOfSight(Entity target, float distance) {
        if (this.worldObj.singleplayerWorld) {
            return;
        }
        if (this.timeSinceIgnited > 0) {
            this.setCreeperState(-1);
            --this.timeSinceIgnited;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
        }
    }

    public void onUpdate() {
        this.lastActiveTime = this.timeSinceIgnited;
        if (this.worldObj.singleplayerWorld) {
            int n = this.getCreeperState();
            if (n > 0 && this.timeSinceIgnited == 0) {
                this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0f, 0.5f);
            }
            this.timeSinceIgnited += n;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
            if (this.timeSinceIgnited >= 30) {
                this.timeSinceIgnited = 30;
            }
        }
        super.onUpdate();
        if (this.playerToAttack == null && this.timeSinceIgnited > 0) {
            this.setCreeperState(-1);
            --this.timeSinceIgnited;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
        }
    }

    protected String getHurtSound() {
        return "mob.creeper";
    }

    protected String getDeathSound() {
        return "mob.creeperdeath";
    }

    public void onDeath(Entity killer) {
        super.onDeath(killer);
        if (killer instanceof EntitySkeleton) {
            this.dropItem(Item.record13.shiftedIndex + this.rand.nextInt(2), 1);
        }
    }

    protected void attackEntity(Entity target, float distance) {
        if (this.worldObj.singleplayerWorld) {
            return;
        }
        int n = this.getCreeperState();
        if (n <= 0 && distance < 3.0f || n > 0 && distance < 7.0f) {
            if (this.timeSinceIgnited == 0) {
                this.worldObj.playSoundAtEntity(this, "random.fuse", 1.0f, 0.5f);
            }
            this.setCreeperState(1);
            ++this.timeSinceIgnited;
            if (this.timeSinceIgnited >= 30) {
                if (this.getPowered()) {
                    this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 6.0f);
                } else {
                    this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0f);
                }
                this.setEntityDead();
            }
            this.hasAttacked = true;
        } else {
            this.setCreeperState(-1);
            --this.timeSinceIgnited;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }
        }
    }

    public boolean getPowered() {
        return this.dataWatcher.getWatchableObjectByte(17) == 1;
    }

    protected int getDropItemId() {
        return Item.gunpowder.shiftedIndex;
    }

    private int getCreeperState() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    private void setCreeperState(int dir) {
        this.dataWatcher.updateObject(16, (byte)dir);
    }

    public void onStruckByLightning(EntityLightningBolt lightning) {
        super.onStruckByLightning(lightning);
        this.dataWatcher.updateObject(17, (byte)1);
    }
}

