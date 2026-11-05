package net.minecraft.src;

import java.util.List;

public class EntityBoat
extends Entity {
    public int damageTaken = 0;
    public int damagedTimer = 0;
    public int forwardDirection = 1;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    public EntityBoat(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(1.5f, 0.6f);
        this.yOffset = this.height / 2.0f;
    }

    protected boolean makesSteps() {
        return false;
    }

    protected void entityInit() {
    }

    public AxisAlignedBB getCollisionAgainstShape(Entity other) {
        return other.boundingBox;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public boolean canBePushed() {
        return true;
    }

    public EntityBoat(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + (double)this.yOffset, z);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    public double getMountedYOffset() {
        return (double)this.height * 0.0 - (double)0.3f;
    }

    public boolean attackEntityFrom(Entity source, int amount) {
        if (this.worldObj.singleplayerWorld || this.isDead) {
            return true;
        }
        this.forwardDirection = -this.forwardDirection;
        this.damagedTimer = 10;
        this.damageTaken += amount * 10;
        this.setBeenAttacked();
        if (this.damageTaken > 40) {
            int n;
            if (this.riddenByEntity != null) {
                this.riddenByEntity.mountEntity(this);
            }
            for (n = 0; n < 3; ++n) {
                this.dropItemWithOffset(Block.planks.blockID, 1, 0.0f);
            }
            for (n = 0; n < 2; ++n) {
                this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0f);
            }
            this.setEntityDead();
        }
        return true;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        double d;
        double d2;
        double d3;
        super.onUpdate();
        if (this.damagedTimer > 0) {
            --this.damagedTimer;
        }
        if (this.damageTaken > 0) {
            --this.damageTaken;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        int n = 5;
        double d4 = 0.0;
        for (int i = 0; i < n; ++i) {
            double d5 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 0) / (double)n - 0.125;
            double d6 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i + 1) / (double)n - 0.125;
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.getBoundingBoxFromPool(this.boundingBox.minX, d5, this.boundingBox.minZ, this.boundingBox.maxX, d6, this.boundingBox.maxZ);
            if (!this.worldObj.isAABBInMaterial(axisAlignedBB, Material.water)) continue;
            d4 += 1.0 / (double)n;
        }
        if (this.worldObj.singleplayerWorld) {
            if (this.lerpSteps > 0) {
                double d7;
                double d8 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
                double d9 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
                double d10 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
                for (d7 = this.lerpYaw - (double)this.rotationYaw; d7 < -180.0; d7 += 360.0) {
                }
                while (d7 >= 180.0) {
                    d7 -= 360.0;
                }
                this.rotationYaw += d7 / (double)this.lerpSteps;
                this.rotationPitch += (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps;
                --this.lerpSteps;
                this.setPosition(d8, d9, d10);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                double d11 = this.posX + this.motionX;
                double d12 = this.posY + this.motionY;
                double d13 = this.posZ + this.motionZ;
                this.setPosition(d11, d12, d13);
                if (this.onGround) {
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                }
                this.motionX *= (double)0.99f;
                this.motionY *= (double)0.95f;
                this.motionZ *= (double)0.99f;
            }
            return;
        }
        if (d4 < 1.0) {
            double d14 = d4 * 2.0 - 1.0;
            this.motionY += (double)0.04f * d14;
        } else {
            if (this.motionY < 0.0) {
                this.motionY /= 2.0;
            }
            this.motionY += (double)0.007f;
        }
        if (this.riddenByEntity != null) {
            this.motionX += this.riddenByEntity.motionX * 0.2;
            this.motionZ += this.riddenByEntity.motionZ * 0.2;
        }
        d3 = 0.4;
        if (this.motionX < -d3) {
            this.motionX = -d3;
        }
        if (this.motionX > d3) {
            this.motionX = d3;
        }
        if (this.motionZ < -d3) {
            this.motionZ = -d3;
        }
        if (this.motionZ > d3) {
            this.motionZ = d3;
        }
        if (this.onGround) {
            this.motionX *= 0.5;
            this.motionY *= 0.5;
            this.motionZ *= 0.5;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (d15 > 0.15) {
            double d16 = Math.cos((double)this.rotationYaw * Math.PI / 180.0);
            d2 = Math.sin((double)this.rotationYaw * Math.PI / 180.0);
            int n2 = 0;
            while ((double)n2 < 1.0 + d15 * 60.0) {
                double d17;
                double d18;
                double d19 = this.rand.nextFloat() * 2.0f - 1.0f;
                double d20 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7;
                if (this.rand.nextBoolean()) {
                    d18 = this.posX - d16 * d19 * 0.8 + d2 * d20;
                    d17 = this.posZ - d2 * d19 * 0.8 - d16 * d20;
                    this.worldObj.spawnParticle("splash", d18, this.posY - 0.125, d17, this.motionX, this.motionY, this.motionZ);
                } else {
                    d18 = this.posX + d16 + d2 * d19 * 0.7;
                    d17 = this.posZ + d2 - d16 * d19 * 0.7;
                    this.worldObj.spawnParticle("splash", d18, this.posY - 0.125, d17, this.motionX, this.motionY, this.motionZ);
                }
                ++n2;
            }
        }
        if (this.isCollidedHorizontally && d15 > 0.15) {
            if (!this.worldObj.singleplayerWorld) {
                int n3;
                this.setEntityDead();
                for (n3 = 0; n3 < 3; ++n3) {
                    this.dropItemWithOffset(Block.planks.blockID, 1, 0.0f);
                }
                for (n3 = 0; n3 < 2; ++n3) {
                    this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0f);
                }
            }
        } else {
            this.motionX *= (double)0.99f;
            this.motionY *= (double)0.95f;
            this.motionZ *= (double)0.99f;
        }
        this.rotationPitch = 0.0f;
        double d21 = this.rotationYaw;
        d2 = this.prevPosX - this.posX;
        double d22 = this.prevPosZ - this.posZ;
        if (d2 * d2 + d22 * d22 > 0.001) {
            d21 = (float)(Math.atan2(d22, d2) * 180.0 / Math.PI);
        }
        for (d = d21 - (double)this.rotationYaw; d >= 180.0; d -= 360.0) {
        }
        while (d < -180.0) {
            d += 360.0;
        }
        if (d > 20.0) {
            d = 20.0;
        }
        if (d < -20.0) {
            d = -20.0;
        }
        this.rotationYaw += d;
        this.setRotation(this.rotationYaw, this.rotationPitch);
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2f, 0.0, 0.2f));
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                if (entity == this.riddenByEntity || !entity.canBePushed() || !(entity instanceof EntityBoat)) continue;
                entity.applyEntityCollision(this);
            }
        }
        for (int i = 0; i < 4; ++i) {
            int n4;
            int n5;
            int n6 = MathHelper.floor_double(this.posX + ((double)(i % 2) - 0.5) * 0.8);
            n5 = MathHelper.floor_double(this.posY);
            n4 = MathHelper.floor_double(this.posZ + ((double)(i / 2) - 0.5) * 0.8);
            if (this.worldObj.getBlockId(n6, n5, n4) != Block.snow.blockID) continue;
            this.worldObj.setBlockWithNotify(n6, n5, n4, 0);
        }
        if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            this.riddenByEntity = null;
        }
    }

    public void updateRiderPosition() {
        if (this.riddenByEntity == null) {
            return;
        }
        double d = Math.cos((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
        double d2 = Math.sin((double)this.rotationYaw * Math.PI / 180.0) * 0.4;
        this.riddenByEntity.setPosition(this.posX + d, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d2);
    }

    protected void writeEntityToNBT(NBTTagCompound nbt) {
    }

    protected void readEntityFromNBT(NBTTagCompound nbt) {
    }

    public boolean interact(EntityPlayer player) {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
            return true;
        }
        if (!this.worldObj.singleplayerWorld) {
            player.mountEntity(this);
        }
        return true;
    }
}

