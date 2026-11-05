package net.minecraft.src;

import java.util.List;

public class EntityFireball extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;
    private int inTile = 0;
    private boolean inGround = false;
    public int shake = 0;
    public EntityLiving owner;
    private int inBlockTicks;
    private int ticksInAir = 0;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public EntityFireball(World var1) {
        super(var1);
        this.setSize(1.0F, 1.0F);
    }

    @Override
    protected void entityInit() {
    }

    public EntityFireball(World world, EntityLiving shooter, double accelerationX, double accelerationY, double accelerationZ) {
        super(world);
        this.owner = shooter;
        this.setSize(1.0F, 1.0F);
        this.setLocationAndAngles(shooter.posX, shooter.posY, shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0;
        accelerationX += this.rand.nextGaussian() * 0.4;
        accelerationY += this.rand.nextGaussian() * 0.4;
        accelerationZ += this.rand.nextGaussian() * 0.4;
        double d0 = MathHelper.sqrt_double(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ);
        this.accelerationX = accelerationX / d0 * 0.1;
        this.accelerationY = accelerationY / d0 * 0.1;
        this.accelerationZ = accelerationZ / d0 * 0.1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.fire = 10;
        if (this.shake > 0) {
            this.shake--;
        }

        if (this.inGround) {
            int i = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            if (i == this.inTile) {
                this.inBlockTicks++;
                if (this.inBlockTicks == 1200) {
                    this.setEntityDead();
                }

                return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.inBlockTicks = 0;
            this.ticksInAir = 0;
        } else {
            this.ticksInAir++;
        }

        Vec3D vec3d1 = Vec3D.createVector(this.posX, this.posY, this.posZ);
        Vec3D vec3d = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3d1, vec3d);
        vec3d1 = Vec3D.createVector(this.posX, this.posY, this.posZ);
        vec3d = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        if (movingobjectposition != null) {
            vec3d = Vec3D.createVector(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        }

        Entity entity = null;
        List list = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        double d0 = 0.0;

        for (int j = 0; j < list.size(); j++) {
            Entity entity1 = (Entity)list.get(j);
            if (entity1.canBeCollidedWith() && (entity1 != this.owner || this.ticksInAir >= 25)) {
                float f = 0.3F;
                AxisAlignedBB axisalignedbb = entity1.boundingBox.expand(f, f, f);
                MovingObjectPosition movingobjectposition1 = axisalignedbb.clip(vec3d1, vec3d);
                if (movingobjectposition1 != null) {
                    double d1 = vec3d1.distanceTo(movingobjectposition1.hitVec);
                    if (d1 < d0 || d0 == 0.0) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null) {
            if (!this.worldObj.singleplayerWorld) {
                if (movingobjectposition.entityHit != null && movingobjectposition.entityHit.attackEntityFrom(this.owner, 0)) {
                }

                this.worldObj.newExplosion(null, this.posX, this.posY, this.posZ, 1.0F, true);
            }

            this.setEntityDead();
        }

        this.posX = this.posX + this.motionX;
        this.posY = this.posY + this.motionY;
        this.posZ = this.posZ + this.motionZ;
        float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / (float) Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f1) * 180.0 / (float) Math.PI);

        while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
            this.prevRotationPitch -= 360.0F;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f2 = 0.95F;
        if (this.isInWater()) {
            for (int k = 0; k < 4; k++) {
                float f3 = 0.25F;
                this.worldObj
                    .spawnParticle(
                        "bubble",
                        this.posX - this.motionX * f3,
                        this.posY - this.motionY * f3,
                        this.posZ - this.motionZ * f3,
                        this.motionX,
                        this.motionY,
                        this.motionZ
                    );
            }

            f2 = 0.8F;
        }

        this.motionX = this.motionX + this.accelerationX;
        this.motionY = this.motionY + this.accelerationY;
        this.motionZ = this.motionZ + this.accelerationZ;
        this.motionX *= f2;
        this.motionY *= f2;
        this.motionZ *= f2;
        this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0);
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("xTile", (short)this.xTile);
        nbt.setShort("yTile", (short)this.yTile);
        nbt.setShort("zTile", (short)this.zTile);
        nbt.setByte("inTile", (byte)this.inTile);
        nbt.setByte("shake", (byte)this.shake);
        nbt.setByte("inGround", (byte)(this.inGround ? 1 : 0));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.xTile = nbt.getShort("xTile");
        this.yTile = nbt.getShort("yTile");
        this.zTile = nbt.getShort("zTile");
        this.inTile = nbt.getByte("inTile") & 255;
        this.shake = nbt.getByte("shake") & 255;
        this.inGround = nbt.getByte("inGround") == 1;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        this.setBeenAttacked();
        if (source != null) {
            Vec3D vec3d = source.getLookVec();
            if (vec3d != null) {
                this.motionX = vec3d.xCoord;
                this.motionY = vec3d.yCoord;
                this.motionZ = vec3d.zCoord;
                this.accelerationX = this.motionX * 0.1;
                this.accelerationY = this.motionY * 0.1;
                this.accelerationZ = this.motionZ * 0.1;
            }

            return true;
        } else {
            return false;
        }
    }
}
