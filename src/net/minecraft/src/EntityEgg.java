package net.minecraft.src;

import java.util.List;

public class EntityEgg
extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private boolean inGround = false;
    public int shake = 0;
    private EntityLiving thrower;
    private int inBlockTicks;
    private int inAirTicks = 0;

    public EntityEgg(World world) {
        super(world);
        this.setSize(0.25f, 0.25f);
    }

    protected void entityInit() {
    }

    public EntityEgg(World world, EntityLiving thrower) {
        super(world);
        this.thrower = thrower;
        this.setSize(0.25f, 0.25f);
        this.setLocationAndAngles(thrower.posX, thrower.posY + (double)thrower.getEyeHeight(), thrower.posZ, thrower.rotationYaw, thrower.rotationPitch);
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

    public EntityEgg(World world, double x, double y, double z) {
        super(world);
        this.inBlockTicks = 0;
        this.setSize(0.25f, 0.25f);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }

    public void thrown(double velocityX, double velocityY, double velocityZ, float power, float variance) {
        float f = MathHelper.sqrt_double(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
        velocityX /= f;
        velocityY /= f;
        velocityZ /= f;
        velocityX += this.rand.nextGaussian() * 0.0075F * variance;
        velocityY += this.rand.nextGaussian() * 0.0075F * variance;
        velocityZ += this.rand.nextGaussian() * 0.0075F * variance;
        velocityX *= power;
        velocityY *= power;
        velocityZ *= power;
        this.motionX = velocityX;
        this.motionY = velocityY;
        this.motionZ = velocityZ;
        float f1 = MathHelper.sqrt_double(velocityX * velocityX + velocityZ * velocityZ);
        this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(velocityX, velocityZ) * 180.0 / (float) Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(velocityY, f1) * 180.0 / (float) Math.PI);
        this.inBlockTicks = 0;
    }

    /*
     * Enabled aggressive block sorting
     */
    public void onUpdate() {
        block21: {
            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;
            super.onUpdate();
            if (this.shake > 0) {
                --this.shake;
            }
            if (this.inGround) {
                int n = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                if (n != this.inTile) {
                    this.inGround = false;
                    this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
                    this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
                    this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
                    this.inBlockTicks = 0;
                    this.inAirTicks = 0;
                    break block21;
                } else {
                    ++this.inBlockTicks;
                    if (this.inBlockTicks == 1200) {
                        this.setEntityDead();
                    }
                    return;
                }
            }
            ++this.inAirTicks;
        }
        Vec3D vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        Vec3D vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingObjectPosition = this.worldObj.rayTraceBlocks(vec3D, vec3D2);
        vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (movingObjectPosition != null) {
            vec3D2 = Vec3D.createVector(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
        }
        if (!this.worldObj.singleplayerWorld) {
            Entity entity = null;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d = 0.0;
            for (int i = 0; i < list.size(); ++i) {
                double d2;
                float f;
                AxisAlignedBB axisAlignedBB;
                MovingObjectPosition movingObjectPosition2;
                Entity entity2 = list.get(i);
                if (!entity2.canBeCollidedWith() || entity2 == this.thrower && this.inAirTicks < 5 || (movingObjectPosition2 = (axisAlignedBB = entity2.boundingBox.expand(f = 0.3f, f, f)).clip(vec3D, vec3D2)) == null || !((d2 = vec3D.distanceTo(movingObjectPosition2.hitVec)) < d) && d != 0.0) continue;
                entity = entity2;
                d = d2;
            }
            if (entity != null) {
                movingObjectPosition = new MovingObjectPosition(entity);
            }
        }
        if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit == null || movingObjectPosition.entityHit.attackEntityFrom(this.thrower, 0)) {
                // empty if block
            }
            if (!this.worldObj.singleplayerWorld && this.rand.nextInt(8) == 0) {
                int n = 1;
                if (this.rand.nextInt(32) == 0) {
                    n = 4;
                }
                for (int i = 0; i < n; ++i) {
                    EntityChicken entityChicken = new EntityChicken(this.worldObj);
                    entityChicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
                    this.worldObj.entityJoinedWorld(entityChicken);
                }
            }
            for (int i = 0; i < 8; ++i) {
                this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
            }
            this.setEntityDead();
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
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
        float f2 = 0.99f;
        float f3 = 0.03f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f4 = 0.25f;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }
            f2 = 0.8f;
        }
        this.motionX *= (double)f2;
        this.motionY *= (double)f2;
        this.motionZ *= (double)f2;
        this.motionY -= (double)f3;
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

    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.inGround && this.thrower == player && this.shake <= 0 && player.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
            this.worldObj.playSoundAtEntity(this, "random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup(this, 1);
            this.setEntityDead();
        }
    }
}

