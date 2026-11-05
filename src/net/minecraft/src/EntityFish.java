package net.minecraft.src;

import java.util.List;

public class EntityFish
extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private boolean inGround = false;
    public int shake = 0;
    public EntityPlayer angler;
    private int ticksInGround;
    private int ticksInAir = 0;
    private int ticksCatchable = 0;
    public Entity bobber = null;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    public EntityFish(World world) {
        super(world);
        this.setSize(0.25f, 0.25f);
        this.ignoreCameraFrustum = true;
    }

    public EntityFish(World world, EntityPlayer thrower) {
        super(world);
        this.ignoreCameraFrustum = true;
        this.angler = thrower;
        this.angler.fishEntity = this;
        this.setSize(0.25f, 0.25f);
        this.setLocationAndAngles(thrower.posX, thrower.posY + 1.62 - (double)thrower.yOffset, thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        this.posY -= (double)0.1f;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        float f = 0.4f;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI) * f;
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI) * f;
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * (float)Math.PI) * f;
        this.thrown(this.motionX, this.motionY, this.motionZ, 1.5f, 1.0f);
    }

    protected void entityInit() {
    }

    public void thrown(double velocityX, double velocityY, double velocityZ, float scale, float min) {
        float f = MathHelper.sqrt_double(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
        velocityX /= f;
        velocityY /= f;
        velocityZ /= f;
        velocityX += this.rand.nextGaussian() * 0.0075F * min;
        velocityY += this.rand.nextGaussian() * 0.0075F * min;
        velocityZ += this.rand.nextGaussian() * 0.0075F * min;
        velocityX *= scale;
        velocityY *= scale;
        velocityZ *= scale;
        this.motionX = velocityX;
        this.motionY = velocityY;
        this.motionZ = velocityZ;
        float f1 = MathHelper.sqrt_double(velocityX * velocityX + velocityZ * velocityZ);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(velocityY, f1) * 180.0 / (float) Math.PI);
        this.ticksInGround = 0;
    }

    public void onUpdate() {
        int n;
        block36: {
            super.onUpdate();
            if (this.lerpSteps > 0) {
                double d;
                double d2 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
                double d3 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
                double d4 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
                for (d = this.lerpYaw - (double)this.rotationYaw; d < -180.0; d += 360.0) {
                }
                while (d >= 180.0) {
                    d -= 360.0;
                }
                this.rotationYaw = (float)((double)this.rotationYaw + d / (double)this.lerpSteps);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
                --this.lerpSteps;
                this.setPosition(d2, d3, d4);
                this.setRotation(this.rotationYaw, this.rotationPitch);
                return;
            }
            if (!this.worldObj.singleplayerWorld) {
                ItemStack itemStack = this.angler.getCurrentEquippedItem();
                if (this.angler.isDead || !this.angler.isEntityAlive() || itemStack == null || itemStack.getItem() != Item.fishingRod || this.getDistanceSqToEntity(this.angler) > 1024.0) {
                    this.setEntityDead();
                    this.angler.fishEntity = null;
                    return;
                }
                if (this.bobber != null) {
                    if (this.bobber.isDead) {
                        this.bobber = null;
                    } else {
                        this.posX = this.bobber.posX;
                        this.posY = this.bobber.boundingBox.minY + (double)this.bobber.height * 0.8;
                        this.posZ = this.bobber.posZ;
                        return;
                    }
                }
            }
            if (this.shake > 0) {
                --this.shake;
            }
            if (this.inGround) {
                int n2 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                if (n2 != this.inTile) {
                    this.inGround = false;
                    this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
                    this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
                    this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
                    this.ticksInGround = 0;
                    this.ticksInAir = 0;
                    break block36;
                } else {
                    ++this.ticksInGround;
                    if (this.ticksInGround == 1200) {
                        this.setEntityDead();
                    }
                    return;
                }
            }
            ++this.ticksInAir;
        }
        Vec3D vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        Vec3D vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec3D, vec3D2);
        vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (movingObjectPosition != null) {
            vec3D2 = Vec3D.createVector(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
        }
        Entity entity = null;
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double d = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            double d5;
            float f;
            AxisAlignedBB axisAlignedBB;
            MovingObjectPosition movingObjectPosition2;
            Entity entity2 = list.get(i);
            if (!entity2.canBeCollidedWith() || entity2 == this.angler && this.ticksInAir < 5 || (movingObjectPosition2 = (axisAlignedBB = entity2.boundingBox.expand(f = 0.3f, f, f)).clip(vec3D, vec3D2)) == null || !((d5 = vec3D.distanceTo(movingObjectPosition2.hitVec)) < d) && d != 0.0) continue;
            entity = entity2;
            d = d5;
        }
        if (entity != null) {
            movingObjectPosition = new MovingObjectPosition(entity);
        }
        if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit != null) {
                if (movingObjectPosition.entityHit.attackEntityFrom(this.angler, 0)) {
                    this.bobber = movingObjectPosition.entityHit;
                }
            } else {
                this.inGround = true;
            }
        }
        if (this.inGround) {
            return;
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.1415927410125732);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f) * 180.0 / 3.1415927410125732);
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
        float f2 = 0.92f;
        if (this.onGround || this.isCollidedHorizontally) {
            f2 = 0.5f;
        }
        int n3 = 5;
        double d6 = 0.0;
        for (n = 0; n < n3; ++n) {
            double d7 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(n + 0) / (double)n3 - 0.125 + 0.125;
            double d8 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(n + 1) / (double)n3 - 0.125 + 0.125;
            AxisAlignedBB axisAlignedBB = AxisAlignedBB.getBoundingBoxFromPool(this.boundingBox.minX, d7, this.boundingBox.minZ, this.boundingBox.maxX, d8, this.boundingBox.maxZ);
            if (!this.worldObj.isAABBInMaterial(axisAlignedBB, Material.water)) continue;
            d6 += 1.0 / (double)n3;
        }
        if (d6 > 0.0) {
            if (this.ticksCatchable > 0) {
                --this.ticksCatchable;
            } else {
                n = 500;
                if (this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY) + 1, MathHelper.floor_double(this.posZ))) {
                    n = 300;
                }
                if (this.rand.nextInt(n) == 0) {
                    float f3;
                    this.ticksCatchable = this.rand.nextInt(30) + 10;
                    this.motionY -= (double)0.2f;
                    this.worldObj.playSoundAtEntity(this, "random.splash", 0.25f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    float f4 = MathHelper.floor_double(this.boundingBox.minY);
                    int n4 = 0;
                    while ((float)n4 < 1.0f + this.width * 20.0f) {
                        float f5 = (this.rand.nextFloat() * 2.0f - 1.0f) * this.width;
                        f3 = (this.rand.nextFloat() * 2.0f - 1.0f) * this.width;
                        this.worldObj.spawnParticle("bubble", this.posX + (double)f5, f4 + 1.0f, this.posZ + (double)f3, this.motionX, this.motionY - (double)(this.rand.nextFloat() * 0.2f), this.motionZ);
                        ++n4;
                    }
                    n4 = 0;
                    while ((float)n4 < 1.0f + this.width * 20.0f) {
                        float f6 = (this.rand.nextFloat() * 2.0f - 1.0f) * this.width;
                        f3 = (this.rand.nextFloat() * 2.0f - 1.0f) * this.width;
                        this.worldObj.spawnParticle("splash", this.posX + (double)f6, f4 + 1.0f, this.posZ + (double)f3, this.motionX, this.motionY, this.motionZ);
                        ++n4;
                    }
                }
            }
        }
        if (this.ticksCatchable > 0) {
            this.motionY -= (double)(this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat()) * 0.2;
        }
        double d9 = d6 * 2.0 - 1.0;
        this.motionY += (double)0.04f * d9;
        if (d6 > 0.0) {
            f2 = (float)((double)f2 * 0.9);
            this.motionY *= 0.8;
        }
        this.motionX *= (double)f2;
        this.motionY *= (double)f2;
        this.motionZ *= (double)f2;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("xTile", (short)this.xTile);
        nbt.setShort("yTile", (short)this.yTile);
        nbt.setShort("zTile", (short)this.zTile);
        nbt.setByte("inTile", (byte)this.inTile);
        nbt.setByte("shake", (byte)this.shake);
        nbt.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.xTile = nbt.getShort("xTile");
        this.yTile = nbt.getShort("yTile");
        this.zTile = nbt.getShort("zTile");
        this.inTile = nbt.getByte("inTile") & 0xFF;
        this.shake = nbt.getByte("shake") & 0xFF;
        this.inGround = nbt.getByte("inGround") == 1;
    }

    public int catchFish() {
        int n = 0;
        if (this.bobber != null) {
            double d = this.angler.posX - this.posX;
            double d2 = this.angler.posY - this.posY;
            double d3 = this.angler.posZ - this.posZ;
            double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
            double d5 = 0.1;
            this.bobber.motionX += d * d5;
            this.bobber.motionY += d2 * d5 + (double)MathHelper.sqrt_double(d4) * 0.08;
            this.bobber.motionZ += d3 * d5;
            n = 3;
        } else if (this.ticksCatchable > 0) {
            EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.fishRaw));
            double d = this.angler.posX - this.posX;
            double d6 = this.angler.posY - this.posY;
            double d7 = this.angler.posZ - this.posZ;
            double d8 = MathHelper.sqrt_double(d * d + d6 * d6 + d7 * d7);
            double d9 = 0.1;
            entityItem.motionX = d * d9;
            entityItem.motionY = d6 * d9 + (double)MathHelper.sqrt_double(d8) * 0.08;
            entityItem.motionZ = d7 * d9;
            this.worldObj.entityJoinedWorld(entityItem);
            this.angler.addStat(StatList.fishCaughtStat, 1);
            n = 1;
        }
        if (this.inGround) {
            n = 2;
        }
        this.setEntityDead();
        this.angler.fishEntity = null;
        return n;
    }
}

