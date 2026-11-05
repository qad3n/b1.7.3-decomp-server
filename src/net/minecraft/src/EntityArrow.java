package net.minecraft.src;

import java.util.List;

public class EntityArrow
extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private int blockData = 0;
    private boolean inGround = false;
    public boolean player = false;
    public int arrowShake = 0;
    public EntityLiving owner;
    private int ticksInGround;
    private int ticksInAir = 0;

    public EntityArrow(World world) {
        super(world);
        this.setSize(0.5f, 0.5f);
    }

    public EntityArrow(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setPosition(x, y, z);
        this.yOffset = 0.0f;
    }

    public EntityArrow(World world, EntityLiving shooter) {
        super(world);
        this.owner = shooter;
        this.player = shooter instanceof EntityPlayer;
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        this.posY -= (double)0.1f;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * 0.16f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0f * (float)Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * (float)Math.PI);
        this.setArrowHeading(this.motionX, this.motionY, this.motionZ, 1.5f, 1.0f);
    }

    protected void entityInit() {
    }

    public void setArrowHeading(double velocityX, double velocityY, double velocityZ, float power, float variance) {
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
        this.ticksInGround = 0;
    }

    public void onUpdate() {
        float f;
        Object object;
        int n;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.1415927410125732);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0 / 3.1415927410125732);
        }
        if ((n = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile)) > 0) {
            Block.blocksList[n].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            object = Block.blocksList[n].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);
            if (object != null && ((AxisAlignedBB)object).isVecInXYZ(Vec3D.createVector(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            n = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            int n2 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            if (n != this.inTile || n2 != this.blockData) {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
                return;
            }
            ++this.ticksInGround;
            if (this.ticksInGround == 1200) {
                this.setEntityDead();
            }
            return;
        }
        ++this.ticksInAir;
        Vec3D vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        object = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingObjectPosition = this.worldObj.rayTrace(vec3D, (Vec3D)object, false, true);
        vec3D = Vec3D.createVector(this.posX, this.posY, this.posZ);
        object = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (movingObjectPosition != null) {
            object = Vec3D.createVector(movingObjectPosition.hitVec.xCoord, movingObjectPosition.hitVec.yCoord, movingObjectPosition.hitVec.zCoord);
        }
        Entity entity = null;
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double d = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            double d2;
            AxisAlignedBB axisAlignedBB;
            MovingObjectPosition movingObjectPosition2;
            Entity entity2 = list.get(i);
            if (!entity2.canBeCollidedWith() || entity2 == this.owner && this.ticksInAir < 5 || (movingObjectPosition2 = (axisAlignedBB = entity2.boundingBox.expand(f = 0.3f, f, f)).clip(vec3D, (Vec3D)object)) == null || !((d2 = vec3D.distanceTo(movingObjectPosition2.hitVec)) < d) && d != 0.0) continue;
            entity = entity2;
            d = d2;
        }
        if (entity != null) {
            movingObjectPosition = new MovingObjectPosition(entity);
        }
        if (movingObjectPosition != null) {
            if (movingObjectPosition.entityHit != null) {
                if (movingObjectPosition.entityHit.attackEntityFrom(this.owner, 4)) {
                    this.worldObj.playSoundAtEntity(this, "random.drr", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.setEntityDead();
                } else {
                    this.motionX *= (double)-0.1f;
                    this.motionY *= (double)-0.1f;
                    this.motionZ *= (double)-0.1f;
                    this.rotationYaw += 180.0f;
                    this.prevRotationYaw += 180.0f;
                    this.ticksInAir = 0;
                }
            } else {
                this.xTile = movingObjectPosition.blockX;
                this.yTile = movingObjectPosition.blockY;
                this.zTile = movingObjectPosition.blockZ;
                this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                this.blockData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                this.motionX = (float)(movingObjectPosition.hitVec.xCoord - this.posX);
                this.motionY = (float)(movingObjectPosition.hitVec.yCoord - this.posY);
                this.motionZ = (float)(movingObjectPosition.hitVec.zCoord - this.posZ);
                float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / (double)f3 * (double)0.05f;
                this.posY -= this.motionY / (double)f3 * (double)0.05f;
                this.posZ -= this.motionZ / (double)f3 * (double)0.05f;
                this.worldObj.playSoundAtEntity(this, "random.drr", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                this.inGround = true;
                this.arrowShake = 7;
            }
        }
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f4 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.1415927410125732);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f4) * 180.0 / 3.1415927410125732);
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
        float f5 = 0.99f;
        f = 0.03f;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f6 = 0.25f;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f6, this.posY - this.motionY * (double)f6, this.posZ - this.motionZ * (double)f6, this.motionX, this.motionY, this.motionZ);
            }
            f5 = 0.8f;
        }
        this.motionX *= (double)f5;
        this.motionY *= (double)f5;
        this.motionZ *= (double)f5;
        this.motionY -= (double)f;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("xTile", (short)this.xTile);
        nbt.setShort("yTile", (short)this.yTile);
        nbt.setShort("zTile", (short)this.zTile);
        nbt.setByte("inTile", (byte)this.inTile);
        nbt.setByte("inData", (byte)this.blockData);
        nbt.setByte("shake", (byte)this.arrowShake);
        nbt.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        nbt.setBoolean("player", this.player);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.xTile = nbt.getShort("xTile");
        this.yTile = nbt.getShort("yTile");
        this.zTile = nbt.getShort("zTile");
        this.inTile = nbt.getByte("inTile") & 0xFF;
        this.blockData = nbt.getByte("inData") & 0xFF;
        this.arrowShake = nbt.getByte("shake") & 0xFF;
        this.inGround = nbt.getByte("inGround") == 1;
        this.player = nbt.getBoolean("player");
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.worldObj.singleplayerWorld) {
            return;
        }
        if (this.inGround && this.player && this.arrowShake <= 0 && player.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
            this.worldObj.playSoundAtEntity(this, "random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup(this, 1);
            this.setEntityDead();
        }
    }
}

