package net.minecraft.src;

import java.util.List;

public class EntityWolf
extends EntityAnimal {
    private boolean interested = false;
    private float headRollAngle;
    private float lastHeadRollAngle;
    private boolean isWet;
    private boolean shaking;
    private float shakeProgress;
    private float lastShakeProgress;

    public EntityWolf(World world) {
        super(world);
        this.texture = "/mob/wolf.png";
        this.setSize(0.8f, 0.8f);
        this.moveSpeed = 1.1f;
        this.health = 8;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
        this.dataWatcher.addObject(17, "");
        this.dataWatcher.addObject(18, new Integer(this.health));
    }

    protected boolean makesSteps() {
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Angry", this.getIsAngry());
        nbt.setBoolean("Sitting", this.getIsSitting());
        if (this.getOwner() == null) {
            nbt.setString("Owner", "");
        } else {
            nbt.setString("Owner", this.getOwner());
        }
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setIsAngry(nbt.getBoolean("Angry"));
        this.setIsSitting(nbt.getBoolean("Sitting"));
        String string = nbt.getString("Owner");
        if (string.length() > 0) {
            this.setOwner(string);
            this.setIsTamed(true);
        }
    }

    protected boolean canDespawn() {
        return !this.isTame();
    }

    protected String getLivingSound() {
        if (this.getIsAngry()) {
            return "mob.wolf.growl";
        }
        if (this.rand.nextInt(3) == 0) {
            if (this.isTame() && this.dataWatcher.getWatchableObjectInteger(18) < 10) {
                return "mob.wolf.whine";
            }
            return "mob.wolf.panting";
        }
        return "mob.wolf.bark";
    }

    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    protected float getSoundVolume() {
        return 0.4f;
    }

    protected int getDropItemId() {
        return -1;
    }

    @Override
protected void updatePlayerActionState() {
    super.updatePlayerActionState();
    if (!this.hasAttacked && !this.getGotPath() && this.isTame() && this.ridingEntity == null) {
        EntityPlayer entityplayer = this.worldObj.getPlayerEntityByName(this.getOwner());
        if (entityplayer != null) {
            float f = entityplayer.getDistanceToEntity(this);
            if (f > 5.0F) {
                this.setPathEntity(entityplayer, f);
            }
        } else if (!this.isInWater()) {
            this.setIsSitting(true);
        }
    } else if (this.playerToAttack == null && !this.getGotPath() && !this.isTame() && this.worldObj.rand.nextInt(100) == 0) {
        List list = this.worldObj.getEntitiesWithinAABB(EntitySheep.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).expand(16.0, 4.0, 16.0));
        if (!list.isEmpty()) {
            this.setEntityToAttack((Entity) list.get(this.worldObj.rand.nextInt(list.size())));
        }
    }
    if (this.isInWater()) {
        this.setIsSitting(false);
    }
    if (!this.worldObj.singleplayerWorld) {
        this.dataWatcher.updateObject(18, this.health);
    }
}

    @Override
public void onLivingUpdate() {
    super.onLivingUpdate();
    this.interested = false;
    if (this.hasLookTarget() && !this.getGotPath() && !this.getIsAngry()) {
        Entity entity = this.getCurrentTarget();
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            ItemStack itemstack = entityplayer.inventory.getCurrentItem();
            if (itemstack != null) {
                if (!this.isTame() && itemstack.itemID == Item.bone.shiftedIndex) {
                    this.interested = true;
                } else if (this.isTame() && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
                    this.interested = ((ItemFood) Item.itemsList[itemstack.itemID]).canBeCooked();
                }
            }
        }
    }
    if (!this.isMultiplayerEntity && this.isWet && !this.shaking && !this.getGotPath() && this.onGround) {
        this.shaking = true;
        this.shakeProgress = 0.0F;
        this.lastShakeProgress = 0.0F;
        this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 8);
    }
}

    @Override
public void onUpdate() {
    super.onUpdate();
    this.lastHeadRollAngle = this.headRollAngle;
    if (this.interested) {
        this.headRollAngle = this.headRollAngle + (1.0F - this.headRollAngle) * 0.4F;
    } else {
        this.headRollAngle = this.headRollAngle + (0.0F - this.headRollAngle) * 0.4F;
    }
    if (this.interested) {
        this.numTicksToChaseTarget = 10;
    }
    if (this.isInWaterOrRain()) {
        this.isWet = true;
        this.shaking = false;
        this.shakeProgress = 0.0F;
        this.lastShakeProgress = 0.0F;
    } else if ((this.isWet || this.shaking) && this.shaking) {
        if (this.shakeProgress == 0.0F) {
            this.worldObj.playSoundAtEntity(this, "mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
        this.lastShakeProgress = this.shakeProgress;
        this.shakeProgress += 0.05F;
        if (this.lastShakeProgress >= 2.0F) {
            this.isWet = false;
            this.shaking = false;
            this.lastShakeProgress = 0.0F;
            this.shakeProgress = 0.0F;
        }
        if (this.shakeProgress > 0.4F) {
            float f = (float) this.boundingBox.minY;
            int i = (int) (MathHelper.sin((this.shakeProgress - 0.4F) * (float) Math.PI) * 7.0F);
            for (int j = 0; j < i; j++) {
                float f1 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                this.worldObj.spawnParticle("splash", this.posX + f1, f + 0.8F, this.posZ + f2, this.motionX, this.motionY, this.motionZ);
            }
        }
    }
}

    public float getEyeHeight() {
        return this.height * 0.8f;
    }

    protected int verticleLookSpeed() {
        if (this.getIsSitting()) {
            return 20;
        }
        return super.verticleLookSpeed();
    }

    private void setPathEntity(Entity owner, float distance) {
        PathEntity pathEntity = this.worldObj.getPathToEntity(this, owner, 16.0f);
        if (pathEntity == null && distance > 12.0f) {
            int n = MathHelper.floor_double(owner.posX) - 2;
            int n2 = MathHelper.floor_double(owner.posZ) - 2;
            int n3 = MathHelper.floor_double(owner.boundingBox.minY);
            for (int i = 0; i <= 4; ++i) {
                for (int j = 0; j <= 4; ++j) {
                    if (i >= 1 && j >= 1 && i <= 3 && j <= 3 || !this.worldObj.isBlockNormalCube(n + i, n3 - 1, n2 + j) || this.worldObj.isBlockNormalCube(n + i, n3, n2 + j) || this.worldObj.isBlockNormalCube(n + i, n3 + 1, n2 + j)) continue;
                    this.setLocationAndAngles((float)(n + i) + 0.5f, n3, (float)(n2 + j) + 0.5f, this.rotationYaw, this.rotationPitch);
                    return;
                }
            }
        } else {
            this.setPathToEntity(pathEntity);
        }
    }

    protected boolean hasStoppedMoving() {
        return this.getIsSitting() || this.shaking;
    }

    public boolean attackEntityFrom(Entity source, int amount) {
        this.setIsSitting(false);
        if (source != null && !(source instanceof EntityPlayer) && !(source instanceof EntityArrow)) {
            amount = (amount + 1) / 2;
        }
        if (super.attackEntityFrom(source, amount)) {
            if (!this.isTame() && !this.getIsAngry()) {
                if (source instanceof EntityPlayer) {
                    this.setIsAngry(true);
                    this.playerToAttack = source;
                }
                if (source instanceof EntityArrow && ((EntityArrow)source).owner != null) {
                    source = ((EntityArrow)source).owner;
                }
                if (source instanceof EntityLiving) {
                    List<EntityWolf> list = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0).expand(16.0, 4.0, 16.0));
                    for (Entity entity : list) {
                        EntityWolf entityWolf = (EntityWolf)entity;
                        if (entityWolf.isTame() || entityWolf.playerToAttack != null) continue;
                        entityWolf.playerToAttack = source;
                        if (!(source instanceof EntityPlayer)) continue;
                        entityWolf.setIsAngry(true);
                    }
                }
            } else if (source != this && source != null) {
                if (this.isTame() && source instanceof EntityPlayer && ((EntityPlayer)source).username.equalsIgnoreCase(this.getOwner())) {
                    return true;
                }
                this.playerToAttack = source;
            }
            return true;
        }
        return false;
    }

    protected Entity findPlayerToAttack() {
        if (this.getIsAngry()) {
            return this.worldObj.getClosestPlayerToEntity(this, 16.0);
        }
        return null;
    }

    protected void attackEntity(Entity target, float distance) {
        if (distance > 2.0f && distance < 6.0f && this.rand.nextInt(10) == 0) {
            if (this.onGround) {
                double d = target.posX - this.posX;
                double d2 = target.posZ - this.posZ;
                float f = MathHelper.sqrt_double(d * d + d2 * d2);
                this.motionX = d / (double)f * 0.5 * (double)0.8f + this.motionX * (double)0.2f;
                this.motionZ = d2 / (double)f * 0.5 * (double)0.8f + this.motionZ * (double)0.2f;
                this.motionY = 0.4f;
            }
        } else if ((double)distance < 1.5 && target.boundingBox.maxY > this.boundingBox.minY && target.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            int n = 2;
            if (this.isTame()) {
                n = 4;
            }
            target.attackEntityFrom(this, n);
        }
    }

    @Override
public boolean interact(EntityPlayer player) {
    ItemStack itemstack = player.inventory.getCurrentItem();
    if (!this.isTame()) {
        if (itemstack != null && itemstack.itemID == Item.bone.shiftedIndex && !this.getIsAngry()) {
            itemstack.stackSize--;
            if (itemstack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            if (!this.worldObj.singleplayerWorld) {
                if (this.rand.nextInt(3) == 0) {
                    this.setIsTamed(true);
                    this.setPathToEntity(null);
                    this.setIsSitting(true);
                    this.health = 20;
                    this.setOwner(player.username);
                    this.isNowTamed(true);
                    this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 7);
                } else {
                    this.isNowTamed(false);
                    this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte) 6);
                }
            }
            return true;
        }
    } else {
        if (itemstack != null && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
            ItemFood itemfood = (ItemFood) Item.itemsList[itemstack.itemID];
            if (itemfood.canBeCooked() && this.dataWatcher.getWatchableObjectInteger(18) < 20) {
                itemstack.stackSize--;
                if (itemstack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
                this.heal(((ItemFood) Item.porkRaw).getHealAmount());
                return true;
            }
        }
        if (player.username.equalsIgnoreCase(this.getOwner())) {
            if (!this.worldObj.singleplayerWorld) {
                this.setIsSitting(!this.getIsSitting());
                this.isJumping = false;
                this.setPathToEntity(null);
            }
            return true;
        }
    }
    return false;
}

    void isNowTamed(boolean breeding) {
        String string = "heart";
        if (!breeding) {
            string = "smoke";
        }
        for (int i = 0; i < 7; ++i) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj.spawnParticle(string, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, this.posY + 0.5 + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0f) - (double)this.width, d, d2, d3);
        }
    }

    public int getMaxSpawnedInChunk() {
        return 8;
    }

    public String getOwner() {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    public void setOwner(String name) {
        this.dataWatcher.updateObject(17, name);
    }

    public boolean getIsSitting() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setIsSitting(boolean sitting) {
        byte by = this.dataWatcher.getWatchableObjectByte(16);
        if (sitting) {
            this.dataWatcher.updateObject(16, (byte)(by | 1));
        } else {
            this.dataWatcher.updateObject(16, (byte)(by & 0xFFFFFFFE));
        }
    }

    public boolean getIsAngry() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
    }

    public void setIsAngry(boolean angry) {
        byte by = this.dataWatcher.getWatchableObjectByte(16);
        if (angry) {
            this.dataWatcher.updateObject(16, (byte)(by | 2));
        } else {
            this.dataWatcher.updateObject(16, (byte)(by & 0xFFFFFFFD));
        }
    }

    public boolean isTame() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
    }

    public void setIsTamed(boolean tame) {
        byte by = this.dataWatcher.getWatchableObjectByte(16);
        if (tame) {
            this.dataWatcher.updateObject(16, (byte)(by | 4));
        } else {
            this.dataWatcher.updateObject(16, (byte)(by & 0xFFFFFFFB));
        }
    }
}

