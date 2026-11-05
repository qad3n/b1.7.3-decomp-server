package net.minecraft.src;

public class EntityGhast extends EntityFlying implements IMob {
    public int courseChangeCooldown = 0;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity = null;
    private int aggroCooldown = 0;
    public int prevAttackCounter = 0;
    public int attackCounter = 0;

    public EntityGhast(World var1) {
        super(var1);
        this.texture = "/mob/ghast.png";
        this.setSize(4.0F, 4.0F);
        this.isImmuneToFire = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        this.texture = b0 == 1 ? "/mob/ghast_fire.png" : "/mob/ghast.png";
    }

    @Override
    protected void updatePlayerActionState() {
        if (!this.worldObj.singleplayerWorld && this.worldObj.difficultySetting == 0) {
            this.setEntityDead();
        }

        this.checkDespawn();
        this.prevAttackCounter = this.attackCounter;
        double d0 = this.waypointX - this.posX;
        double d1 = this.waypointY - this.posY;
        double d2 = this.waypointZ - this.posZ;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        if (d3 < 1.0 || d3 > 60.0) {
            this.waypointX = this.posX + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointY = this.posY + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
            this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F;
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = this.courseChangeCooldown + this.rand.nextInt(5) + 2;
            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d3)) {
                this.motionX += d0 / d3 * 0.1;
                this.motionY += d1 / d3 * 0.1;
                this.motionZ += d2 / d3 * 0.1;
            } else {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isDead) {
            this.targetedEntity = null;
        }

        if (this.targetedEntity == null || this.aggroCooldown-- <= 0) {
            this.targetedEntity = this.worldObj.getClosestPlayerToEntity(this, 100.0);
            if (this.targetedEntity != null) {
                this.aggroCooldown = 20;
            }
        }

        double d4 = 64.0;
        if (this.targetedEntity != null && this.targetedEntity.getDistanceSqToEntity(this) < d4 * d4) {
            double d5 = this.targetedEntity.posX - this.posX;
            double d6 = this.targetedEntity.boundingBox.minY + this.targetedEntity.height / 2.0F - (this.posY + this.height / 2.0F);
            double d7 = this.targetedEntity.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(d5, d7)) * 180.0F / (float) Math.PI;
            if (this.canEntityBeSeen(this.targetedEntity)) {
                if (this.attackCounter == 10) {
                    this.worldObj
                        .playSoundAtEntity(this, "mob.ghast.charge", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                this.attackCounter++;
                if (this.attackCounter == 20) {
                    this.worldObj
                        .playSoundAtEntity(this, "mob.ghast.fireball", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    EntityFireball entityfireball = new EntityFireball(this.worldObj, this, d5, d6, d7);
                    double d8 = 4.0;
                    Vec3D vec3d = this.getLook(1.0F);
                    entityfireball.posX = this.posX + vec3d.xCoord * d8;
                    entityfireball.posY = this.posY + this.height / 2.0F + 0.5;
                    entityfireball.posZ = this.posZ + vec3d.zCoord * d8;
                    this.worldObj.entityJoinedWorld(entityfireball);
                    this.attackCounter = -40;
                }
            } else if (this.attackCounter > 0) {
                this.attackCounter--;
            }
        } else {
            this.renderYawOffset = this.rotationYaw = -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;
            if (this.attackCounter > 0) {
                this.attackCounter--;
            }
        }

        if (!this.worldObj.singleplayerWorld) {
            byte b1 = this.dataWatcher.getWatchableObjectByte(16);
            byte b0 = (byte)(this.attackCounter > 10 ? 1 : 0);
            if (b1 != b0) {
                this.dataWatcher.updateObject(16, b0);
            }
        }
    }

    private boolean isCourseTraversable(double targetX, double targetY, double targetZ, double distance) {
        double d0 = (this.waypointX - this.posX) / distance;
        double d1 = (this.waypointY - this.posY) / distance;
        double d2 = (this.waypointZ - this.posZ) / distance;
        AxisAlignedBB axisalignedbb = this.boundingBox.copy();

        for (int i = 1; i < distance; i++) {
            axisalignedbb.offset(d0, d1, d2);
            if (this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).size() > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected String getLivingSound() {
        return "mob.ghast.moan";
    }

    @Override
    protected String getHurtSound() {
        return "mob.ghast.scream";
    }

    @Override
    protected String getDeathSound() {
        return "mob.ghast.death";
    }

    @Override
    protected int getDropItemId() {
        return Item.gunpowder.shiftedIndex;
    }

    @Override
    protected float getSoundVolume() {
        return 10.0F;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.rand.nextInt(20) == 0 && super.getCanSpawnHere() && this.worldObj.difficultySetting > 0;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 1;
    }
}
