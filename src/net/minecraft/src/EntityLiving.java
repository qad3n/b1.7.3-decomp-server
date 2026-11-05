package net.minecraft.src;

import java.util.List;

public abstract class EntityLiving extends Entity {
    public int invulnerableTicks = 20;
    public float field_9098_aw;
    public float field_9096_ay;
    public float renderYawOffset = 0.0F;
    public float prevRenderYawOffset = 0.0F;
    protected float lastWalkProgress;
    protected float walkProgress;
    protected float totalWalkDistance;
    protected float lastTotalWalkDistance;
    protected boolean field_9120_aF = true;
    protected String texture = "/mob/char.png";
    protected boolean field_9118_aH = true;
    protected float rotationOffset = 0.0F;
    protected String entityType = null;
    protected float field_9115_aK = 1.0F;
    protected int scoreValue = 0;
    protected float lastDamageTaken = 0.0F;
    public boolean isMultiplayerEntity = false;
    public float prevSwingProgress;
    public float swingProgress;
    public int health;
    public int prevHealth;
    private int livingSoundTime;
    public int hurtTime;
    public int maxHurtTime;
    public float attackedAtYaw = 0.0F;
    public int deathTime = 0;
    public int attackTime = 0;
    public float lastTilt;
    public float tilt;
    protected boolean unused_flag = false;
    public int field_9144_ba = -1;
    public float field_9143_bb = (float)(Math.random() * 0.9F + 0.1F);
    public float lastWalkAnimationSpeed;
    public float walkAnimationSpeed;
    public float walkAnimationProgress;
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;
    float field_9134_bl = 0.0F;
    protected int prevDamageTaken = 0;
    protected int age = 0;
    protected float moveStrafing;
    protected float moveForward;
    protected float randomYawVelocity;
    protected boolean isJumping = false;
    protected float defaultPitch = 0.0F;
    protected float moveSpeed = 0.7F;
    private Entity currentTarget;
    protected int numTicksToChaseTarget = 0;

    public EntityLiving(World var1) {
        super(var1);
        this.health = 10;
        this.preventEntitySpawning = true;
        this.field_9096_ay = (float)(Math.random() + 1.0) * 0.01F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.field_9098_aw = (float)Math.random() * 12398.0F;
        this.rotationYaw = (float)(Math.random() * (float) Math.PI * 2.0);
        this.stepHeight = 0.5F;
    }

    @Override
    protected void entityInit() {
    }

    public boolean canEntityBeSeen(Entity entity) {
        return this.worldObj
                .rayTraceBlocks(
                    Vec3D.createVector(this.posX, this.posY + this.getEyeHeight(), this.posZ),
                    Vec3D.createVector(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
                )
            == null;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    public int getTalkInterval() {
        return 80;
    }

    public void playLivingSound() {
        String s = this.getLivingSound();
        if (s != null) {
            this.worldObj.playSoundAtEntity(this, s, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    @Override
    public void onEntityUpdate() {
        this.prevSwingProgress = this.swingProgress;
        super.onEntityUpdate();
        if (this.rand.nextInt(1000) < this.livingSoundTime++) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playLivingSound();
        }

        if (this.isEntityAlive() && this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(null, 1);
        }

        if (this.isImmuneToFire || this.worldObj.singleplayerWorld) {
            this.fire = 0;
        }

        if (this.isEntityAlive() && this.isInsideOfMaterial(Material.water) && !this.canBreatheUnderwater()) {
            this.air--;
            if (this.air == -20) {
                this.air = 0;

                for (int i = 0; i < 8; i++) {
                    float f = this.rand.nextFloat() - this.rand.nextFloat();
                    float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                    float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                    this.worldObj.spawnParticle("bubble", this.posX + f, this.posY + f1, this.posZ + f2, this.motionX, this.motionY, this.motionZ);
                }

                this.attackEntityFrom(null, 2);
            }

            this.fire = 0;
        } else {
            this.air = this.maxAir;
        }

        this.lastTilt = this.tilt;
        if (this.attackTime > 0) {
            this.attackTime--;
        }

        if (this.hurtTime > 0) {
            this.hurtTime--;
        }

        if (this.invulnerableTimer > 0) {
            this.invulnerableTimer--;
        }

        if (this.health <= 0) {
            this.deathTime++;
            if (this.deathTime > 20) {
                this.beforeRemove();
                this.setEntityDead();

                for (int j = 0; j < 20; j++) {
                    double d1 = this.rand.nextGaussian() * 0.02;
                    double d2 = this.rand.nextGaussian() * 0.02;
                    double d0 = this.rand.nextGaussian() * 0.02;
                    this.worldObj
                        .spawnParticle(
                            "explode",
                            this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                            this.posY + this.rand.nextFloat() * this.height,
                            this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                            d1,
                            d2,
                            d0
                        );
                }
            }
        }

        this.lastTotalWalkDistance = this.totalWalkDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public void spawnExplosionParticle() {
        for (int i = 0; i < 20; i++) {
            double d0 = this.rand.nextGaussian() * 0.02;
            double d1 = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = 10.0;
            this.worldObj
                .spawnParticle(
                    "explode",
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width - d0 * d3,
                    this.posY + this.rand.nextFloat() * this.height - d1 * d3,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width - d2 * d3,
                    d0,
                    d1,
                    d2
                );
        }
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        this.lastWalkProgress = this.walkProgress;
        this.walkProgress = 0.0F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.onLivingUpdate();
        double d0 = this.posX - this.prevPosX;
        double d1 = this.posZ - this.prevPosZ;
        float f = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f1 = this.renderYawOffset;
        float f2 = 0.0F;
        this.lastWalkProgress = this.walkProgress;
        float f3 = 0.0F;
        if (!(f <= 0.05F)) {
            f3 = 1.0F;
            f2 = f * 3.0F;
            f1 = (float)Math.atan2(d1, d0) * 180.0F / (float) Math.PI - 90.0F;
        }

        if (this.swingProgress > 0.0F) {
            f1 = this.rotationYaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.walkProgress = this.walkProgress + (f3 - this.walkProgress) * 0.3F;
        float f4 = f1 - this.renderYawOffset;

        while (f4 < -180.0F) {
            f4 += 360.0F;
        }

        while (f4 >= 180.0F) {
            f4 -= 360.0F;
        }

        this.renderYawOffset += f4 * 0.3F;
        float f5 = this.rotationYaw - this.renderYawOffset;

        while (f5 < -180.0F) {
            f5 += 360.0F;
        }

        while (f5 >= 180.0F) {
            f5 -= 360.0F;
        }

        boolean flag = f5 < -90.0F || f5 >= 90.0F;
        if (f5 < -75.0F) {
            f5 = -75.0F;
        }

        if (f5 >= 75.0F) {
            f5 = 75.0F;
        }

        this.renderYawOffset = this.rotationYaw - f5;
        if (f5 * f5 > 2500.0F) {
            this.renderYawOffset += f5 * 0.2F;
        }

        if (flag) {
            f2 *= -1.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
            this.prevRenderYawOffset -= 360.0F;
        }

        while (this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
            this.prevRenderYawOffset += 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        this.totalWalkDistance += f2;
    }

    @Override
    protected void setSize(float width, float height) {
        super.setSize(width, height);
    }

    public void heal(int amount) {
        if (this.health <= 0) {
            return;
        }
        this.health += amount;
        if (this.health > 20) {
            this.health = 20;
        }
        this.invulnerableTimer = this.invulnerableTicks / 2;
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        if (this.worldObj.singleplayerWorld) {
            return false;
        }

        this.age = 0;
        if (this.health <= 0) {
            return false;
        }

        this.walkAnimationSpeed = 1.5F;
        boolean flag = true;
        if (this.invulnerableTimer > this.invulnerableTicks / 2.0F) {
            if (amount <= this.prevDamageTaken) {
                return false;
            }

            this.damageEntity(amount - this.prevDamageTaken);
            this.prevDamageTaken = amount;
            flag = false;
        } else {
            this.prevDamageTaken = amount;
            this.prevHealth = this.health;
            this.invulnerableTimer = this.invulnerableTicks;
            this.damageEntity(amount);
            this.hurtTime = this.maxHurtTime = 10;
        }

        this.attackedAtYaw = 0.0F;
        if (flag) {
            this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte)2);
            this.setBeenAttacked();
            if (source != null) {
                double d0 = source.posX - this.posX;

                double d1;
                for (d1 = source.posZ - this.posZ; d0 * d0 + d1 * d1 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01) {
                    d0 = (Math.random() - Math.random()) * 0.01;
                }

                this.attackedAtYaw = (float)(Math.atan2(d1, d0) * 180.0 / (float) Math.PI) - this.rotationYaw;
                this.knockBack(source, amount, d0, d1);
            } else {
                this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
            }
        }

        if (this.health <= 0) {
            if (flag) {
                this.worldObj
                    .playSoundAtEntity(this, this.getDeathSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.onDeath(source);
        } else if (flag) {
            this.worldObj.playSoundAtEntity(this, this.getHurtSound(), this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }

        return true;
    }

    protected void damageEntity(int amount) {
        this.health -= amount;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    protected String getLivingSound() {
        return null;
    }

    protected String getHurtSound() {
        return "random.hurt";
    }

    protected String getDeathSound() {
        return "random.hurt";
    }

    public void knockBack(Entity attacker, int amount, double dx, double dz) {
        float f = MathHelper.sqrt_double(dx * dx + dz * dz);
        float f1 = 0.4F;
        this.motionX /= 2.0;
        this.motionY /= 2.0;
        this.motionZ /= 2.0;
        this.motionX -= dx / f * f1;
        this.motionY += 0.4F;
        this.motionZ -= dz / f * f1;
        if (this.motionY > 0.4F) {
            this.motionY = 0.4F;
        }
    }

    public void onDeath(Entity killer) {
        if (this.scoreValue >= 0 && killer != null) {
            killer.addToPlayerScore(this, this.scoreValue);
        }

        if (killer != null) {
            killer.onKill(this);
        }

        this.unused_flag = true;
        if (!this.worldObj.singleplayerWorld) {
            this.dropFewItems();
        }

        this.worldObj.sendTrackedEntityStatusUpdatePacket(this, (byte)3);
    }

    protected void dropFewItems() {
        int i = this.getDropItemId();
        if (i > 0) {
            int j = this.rand.nextInt(3);

            for (int k = 0; k < j; k++) {
                this.dropItem(i, 1);
            }
        }
    }

    protected int getDropItemId() {
        return 0;
    }

    @Override
    protected void fall(float distance) {
        super.fall(distance);
        int i = (int)Math.ceil(distance - 3.0F);
        if (i > 0) {
            this.attackEntityFrom(null, i);
            int j = this.worldObj
                .getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2F - this.yOffset), MathHelper.floor_double(this.posZ));
            if (j > 0) {
                Block.StepSound block$stepsound = Block.blocksList[j].stepSound;
                this.worldObj.playSoundAtEntity(this, block$stepsound.getStepping(), block$stepsound.getVolume() * 0.5F, block$stepsound.getPitch() * 0.75F);
            }
        }
    }

    public void moveEntityWithHeading(float sideways, float forwards) {
        if (this.isInWater()) {
            double d = this.posY;
            this.moveFlying(sideways, forwards, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double) 0.8f;
            this.motionY *= (double) 0.8f;
            this.motionZ *= (double) 0.8f;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double) 0.6f - this.posY + d, this.motionZ)) {
                this.motionY = 0.3f;
            }
        } else if (this.handleLavaMovement()) {
            double d = this.posY;
            this.moveFlying(sideways, forwards, 0.02f);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
            this.motionY -= 0.02;
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double) 0.6f - this.posY + d, this.motionZ)) {
                this.motionY = 0.3f;
            }
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
            if (this.isOnLadder()) {
                float f3 = 0.15f;
                if (this.motionX < (double) (-f3)) {
                    this.motionX = -f3;
                }
                if (this.motionX > (double) f3) {
                    this.motionX = f3;
                }
                if (this.motionZ < (double) (-f3)) {
                    this.motionZ = -f3;
                }
                if (this.motionZ > (double) f3) {
                    this.motionZ = f3;
                }
                this.fallDistance = 0.0f;
                if (this.motionY < -0.15) {
                    this.motionY = -0.15;
                }
                if (this.isSneaking() && this.motionY < 0.0) {
                    this.motionY = 0.0;
                }
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            if (this.isCollidedHorizontally && this.isOnLadder()) {
                this.motionY = 0.2;
            }
            this.motionY -= 0.08;
            this.motionY *= (double) 0.98f;
            this.motionX *= (double) f;
            this.motionZ *= (double) f;
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
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.boundingBox.minY);
        int k = MathHelper.floor_double(this.posZ);
        return this.worldObj.getBlockId(i, j, k) == Block.ladder.blockID;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("Health", (short)this.health);
        nbt.setShort("HurtTime", (short)this.hurtTime);
        nbt.setShort("DeathTime", (short)this.deathTime);
        nbt.setShort("AttackTime", (short)this.attackTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.health = nbt.getShort("Health");
        if (!nbt.hasKey("Health")) {
            this.health = 10;
        }

        this.hurtTime = nbt.getShort("HurtTime");
        this.deathTime = nbt.getShort("DeathTime");
        this.attackTime = nbt.getShort("AttackTime");
    }

    @Override
    public boolean isEntityAlive() {
        return !this.isDead && this.health > 0;
    }

    public boolean canBreatheUnderwater() {
        return false;
    }

    public void onLivingUpdate() {
        if (this.lerpSteps > 0) {
            double d0 = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
            double d3 = this.lerpYaw - this.rotationYaw;

            while (d3 < -180.0) {
                d3 += 360.0;
            }

            while (d3 >= 180.0) {
                d3 -= 360.0;
            }

            this.rotationYaw = (float)(this.rotationYaw + d3 / this.lerpSteps);
            this.rotationPitch = (float)(this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);
            this.lerpSteps--;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
            List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getInsetBoundingBox(0.03125, 0.0, 0.03125));
            if (list.size() > 0) {
                double d4 = 0.0;

                for (int j = 0; j < list.size(); j++) {
                    AxisAlignedBB axisalignedbb = (AxisAlignedBB)list.get(j);
                    if (axisalignedbb.maxY > d4) {
                        d4 = axisalignedbb.maxY;
                    }
                }

                d1 += d4 - this.boundingBox.minY;
                this.setPosition(d0, d1, d2);
            }
        }

        if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
        } else if (!this.isMultiplayerEntity) {
            this.updatePlayerActionState();
        }

        boolean flag1 = this.isInWater();
        boolean flag = this.handleLavaMovement();
        if (this.isJumping) {
            if (flag1) {
                this.motionY += 0.04F;
            } else if (flag) {
                this.motionY += 0.04F;
            } else if (this.onGround) {
                this.jump();
            }
        }

        this.moveStrafing *= 0.98F;
        this.moveForward *= 0.98F;
        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        List list1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
        if (list1 != null && list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                Entity entity = (Entity)list1.get(i);
                if (entity.canBePushed()) {
                    entity.applyEntityCollision(this);
                }
            }
        }
    }

    protected boolean isMovementBlocked() {
        return this.health <= 0;
    }

    protected void jump() {
        this.motionY = 0.42F;
    }

    protected boolean canDespawn() {
        return true;
    }

    protected void checkDespawn() {
        EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, -1.0);
        if (this.canDespawn() && entityplayer != null) {
            double d0 = entityplayer.posX - this.posX;
            double d1 = entityplayer.posY - this.posY;
            double d2 = entityplayer.posZ - this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 > 16384.0) {
                this.setEntityDead();
            }

            if (this.age > 600 && this.rand.nextInt(800) == 0) {
                if (d3 < 1024.0) {
                    this.age = 0;
                } else {
                    this.setEntityDead();
                }
            }
        }
    }

    protected void updatePlayerActionState() {
        ++this.age;
        EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, -1.0);
        this.checkDespawn();
        this.moveStrafing = 0.0f;
        this.moveForward = 0.0f;
        float f = 8.0f;
        if (this.rand.nextFloat() < 0.02f) {
            entityPlayer = this.worldObj.getClosestPlayerToEntity(this, f);
            if (entityPlayer != null) {
                this.currentTarget = entityPlayer;
                this.numTicksToChaseTarget = 10 + this.rand.nextInt(20);
            } else {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5f) * 20.0f;
            }
        }
        if (this.currentTarget != null) {
            this.faceEntity(this.currentTarget, 10.0f, this.verticleLookSpeed());
            if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.isDead || this.currentTarget.getDistanceSqToEntity(this) > (double) (f * f)) {
                this.currentTarget = null;
            }
        } else {
            if (this.rand.nextFloat() < 0.05f) {
                this.randomYawVelocity = (this.rand.nextFloat() - 0.5f) * 20.0f;
            }
            this.rotationYaw += this.randomYawVelocity;
            this.rotationPitch = this.defaultPitch;
        }
        boolean bl = this.isInWater();
        boolean bl2 = this.handleLavaMovement();
        if (bl || bl2) {
            this.isJumping = this.rand.nextFloat() < 0.8f;
        }
    }

    protected int verticleLookSpeed() {
        return 40;
    }

    public void faceEntity(Entity target, float maxPitch, float maxYaw) {
        double d0 = target.posX - this.posX;
        double d2 = target.posZ - this.posZ;
        double d1;
        if (target instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving)target;
            d1 = this.posY + this.getEyeHeight() - (entityliving.posY + entityliving.getEyeHeight());
        } else {
            d1 = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0 - (this.posY + this.getEyeHeight());
        }

        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(Math.atan2(d2, d0) * 180.0 / (float) Math.PI) - 90.0F;
        float f1 = (float)(-(Math.atan2(d1, d3) * 180.0 / (float) Math.PI));
        this.rotationPitch = -this.updateRotation(this.rotationPitch, f1, maxYaw);
        this.rotationYaw = this.updateRotation(this.rotationYaw, f, maxPitch);
    }

    public boolean hasLookTarget() {
        return this.currentTarget != null;
    }

    public Entity getCurrentTarget() {
        return this.currentTarget;
    }

    private float updateRotation(float from, float to, float maxChange) {
        float f = to - from;

        while (f < -180.0F) {
            f += 360.0F;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f > maxChange) {
            f = maxChange;
        }

        if (f < -maxChange) {
            f = -maxChange;
        }

        return from + f;
    }

    public void beforeRemove() {
    }

    public boolean getCanSpawnHere() {
        return this.worldObj.checkIfAABBIsClear(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.worldObj.getIsAnyLiquid(this.boundingBox);
    }

    @Override
    protected void kill() {
        this.attackEntityFrom(null, 4);
    }

    @Override
    public Vec3D getLookVec() {
        return this.getLook(1.0F);
    }

    public Vec3D getLook(float tickDelta) {
        if (tickDelta == 1.0F) {
            float f6 = MathHelper.cos(-this.rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f7 = MathHelper.sin(-this.rotationYaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f8 = -MathHelper.cos(-this.rotationPitch * (float) (Math.PI / 180.0));
            float f9 = MathHelper.sin(-this.rotationPitch * (float) (Math.PI / 180.0));
            return Vec3D.createVector(f7 * f8, f9, f6 * f8);
        } else {
            float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * tickDelta;
            float f1 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * tickDelta;
            float f2 = MathHelper.cos(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f3 = MathHelper.sin(-f1 * (float) (Math.PI / 180.0) - (float) Math.PI);
            float f4 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
            float f5 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
            return Vec3D.createVector(f3 * f4, f5, f2 * f4);
        }
    }

    public int getMaxSpawnedInChunk() {
        return 4;
    }

    public boolean isSleeping() {
        return false;
    }
}
