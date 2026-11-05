package net.minecraft.src;

import java.util.List;
import java.util.Random;

public abstract class Entity {
    private static int nextEntityID = 0;
    public int entityId = nextEntityID++;
    public double renderDistanceWeight = 1.0;
    public boolean preventEntitySpawning = false;
    public Entity riddenByEntity;
    public Entity ridingEntity;
    public World worldObj;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    public double posX;
    public double posY;
    public double posZ;
    public double motionX;
    public double motionY;
    public double motionZ;
    public float rotationYaw;
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;
    public final AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    public boolean onGround = false;
    public boolean isCollidedHorizontally;
    public boolean isCollidedVertically;
    public boolean isCollided = false;
    public boolean beenAttacked = false;
    public boolean inCobweb;
    public boolean immovable = true;
    public boolean isDead = false;
    public float yOffset = 0.0F;
    public float width = 0.6F;
    public float height = 1.8F;
    public float prevDistanceWalkedModified = 0.0F;
    public float distanceWalkedModified = 0.0F;
    protected float fallDistance = 0.0F;
    private int nextStepDistance = 1;
    public double lastTickPosX;
    public double lastTickPosY;
    public double lastTickPosZ;
    public float ySize = 0.0F;
    public float stepHeight = 0.0F;
    public boolean noClip = false;
    public float entityCollisionReduction = 0.0F;
    protected Random rand = new Random();
    public int ticksExisted = 0;
    public int fireResistance = 1;
    public int fire = 0;
    protected int maxAir = 300;
    protected boolean inWater = false;
    public int invulnerableTimer = 0;
    public int air = 300;
    private boolean firstUpdate = true;
    protected boolean isImmuneToFire = false;
    protected DataWatcher dataWatcher = new DataWatcher();
    public float minimumBrightness = 0.0F;
    private double entityRiderPitchDelta;
    private double entityRiderYawDelta;
    public boolean addedToChunk = false;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    public boolean ignoreCameraFrustum;

    public Entity(World world) {
        this.worldObj = world;
        this.setPosition(0.0, 0.0, 0.0);
        this.dataWatcher.addObject(0, (byte)0);
        this.entityInit();
    }

    protected abstract void entityInit();

    public DataWatcher getDataWatcher() {
        return this.dataWatcher;
    }

    public boolean equals(Object object) {
        if (object instanceof Entity) {
            return ((Entity) object).entityId == this.entityId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.entityId;
    }

    public void setEntityDead() {
        this.isDead = true;
    }

    protected void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    protected void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.boundingBox.setBounds(x - f, y - this.yOffset + this.ySize, z - f, x + f, y - this.yOffset + this.ySize + f1, z + f);
    }

    public void onUpdate() {
        this.onEntityUpdate();
    }

    public void onEntityUpdate() {
        if (this.ridingEntity != null && this.ridingEntity.isDead) {
            this.ridingEntity = null;
        }

        this.ticksExisted++;
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        if (this.handleWaterMovement()) {
            if (!this.inWater && !this.firstUpdate) {
                float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.2F + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.2F) * 0.2F;
                if (f > 1.0F) {
                    f = 1.0F;
                }

                this.worldObj.playSoundAtEntity(this, "random.splash", f, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                float f1 = MathHelper.floor_double(this.boundingBox.minY);

                for (int i = 0; i < 1.0F + this.width * 20.0F; i++) {
                    float f2 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    float f3 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj
                        .spawnParticle(
                            "bubble", this.posX + f2, f1 + 1.0F, this.posZ + f3, this.motionX, this.motionY - this.rand.nextFloat() * 0.2F, this.motionZ
                        );
                }

                for (int j = 0; j < 1.0F + this.width * 20.0F; j++) {
                    float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
                    this.worldObj.spawnParticle("splash", this.posX + f4, f1 + 1.0F, this.posZ + f5, this.motionX, this.motionY, this.motionZ);
                }
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        } else {
            this.inWater = false;
        }

        if (this.worldObj.singleplayerWorld) {
            this.fire = 0;
        } else if (this.fire > 0) {
            if (this.isImmuneToFire) {
                this.fire -= 4;
                if (this.fire < 0) {
                    this.fire = 0;
                }
            } else {
                if (this.fire % 20 == 0) {
                    this.attackEntityFrom(null, 1);
                }

                this.fire--;
            }
        }

        if (this.handleLavaMovement()) {
            this.setOnFireFromLava();
        }

        if (this.posY < -64.0) {
            this.kill();
        }

        if (!this.worldObj.singleplayerWorld) {
            this.setFlag(0, this.fire > 0);
            this.setFlag(2, this.ridingEntity != null);
        }

        this.firstUpdate = false;
    }

    protected void setOnFireFromLava() {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(null, 4);
            this.fire = 600;
        }
    }

    protected void kill() {
        this.setEntityDead();
    }

    public boolean isOffsetPositionInLiquid(double dx, double dy, double dz) {
        AxisAlignedBB axisalignedbb = this.boundingBox.getOffsetBoundingBox(dx, dy, dz);
        List list = this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb);
        return list.size() > 0 ? false : !this.worldObj.getIsAnyLiquid(axisalignedbb);
    }

    public void moveEntity(double dx, double dy, double dz) {
        if (this.noClip) {
            this.boundingBox.offset(dx, dy, dz);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
        } else {
            this.ySize *= 0.4F;
            double d0 = this.posX;
            double d1 = this.posZ;
            if (this.inCobweb) {
                this.inCobweb = false;
                dx *= 0.25;
                dy *= 0.05F;
                dz *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            double d2 = dx;
            double d3 = dy;
            double d4 = dz;
            AxisAlignedBB axisalignedbb = this.boundingBox.copy();
            boolean flag = this.onGround && this.isSneaking();
            if (flag) {
                double d5 = 0.05;

                while (dx != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(dx, -1.0, 0.0)).size() == 0) {
                    if (dx < d5 && dx >= -d5) {
                        dx = 0.0;
                    } else if (dx > 0.0) {
                        dx -= d5;
                    } else {
                        dx += d5;
                    }

                    d2 = dx;
                }

                for (; dz != 0.0 && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, -1.0, dz)).size() == 0; d4 = dz) {
                    if (dz < d5 && dz >= -d5) {
                        dz = 0.0;
                    } else if (dz > 0.0) {
                        dz -= d5;
                    } else {
                        dz += d5;
                    }
                }
            }

            List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(dx, dy, dz));

            for (int i = 0; i < list.size(); i++) {
                dy = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, dy);
            }

            this.boundingBox.offset(0.0, dy, 0.0);
            if (!this.immovable && d3 != dy) {
                dz = 0.0;
                dy = 0.0;
                dx = 0.0;
            }

            boolean flag1 = this.onGround || d3 != dy && d3 < 0.0;

            for (int j = 0; j < list.size(); j++) {
                dx = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, dx);
            }

            this.boundingBox.offset(dx, 0.0, 0.0);
            if (!this.immovable && d2 != dx) {
                dz = 0.0;
                dy = 0.0;
                dx = 0.0;
            }

            for (int j2 = 0; j2 < list.size(); j2++) {
                dz = ((AxisAlignedBB)list.get(j2)).calculateZOffset(this.boundingBox, dz);
            }

            this.boundingBox.offset(0.0, 0.0, dz);
            if (!this.immovable && d4 != dz) {
                dz = 0.0;
                dy = 0.0;
                dx = 0.0;
            }

            if (this.stepHeight > 0.0F && flag1 && (flag || this.ySize < 0.05F) && (d2 != dx || d4 != dz)) {
                double d8 = dx;
                double d6 = dy;
                double d7 = dz;
                dx = d2;
                dy = this.stepHeight;
                dz = d4;
                AxisAlignedBB axisalignedbb1 = this.boundingBox.copy();
                this.boundingBox.setBB(axisalignedbb);
                list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(dx, dy, dz));

                for (int l = 0; l < list.size(); l++) {
                    dy = ((AxisAlignedBB)list.get(l)).calculateYOffset(this.boundingBox, dy);
                }

                this.boundingBox.offset(0.0, dy, 0.0);
                if (!this.immovable && d3 != dy) {
                    dz = 0.0;
                    dy = 0.0;
                    dx = 0.0;
                }

                for (int l3 = 0; l3 < list.size(); l3++) {
                    dx = ((AxisAlignedBB)list.get(l3)).calculateXOffset(this.boundingBox, dx);
                }

                this.boundingBox.offset(dx, 0.0, 0.0);
                if (!this.immovable && d2 != dx) {
                    dz = 0.0;
                    dy = 0.0;
                    dx = 0.0;
                }

                for (int i4 = 0; i4 < list.size(); i4++) {
                    dz = ((AxisAlignedBB)list.get(i4)).calculateZOffset(this.boundingBox, dz);
                }

                this.boundingBox.offset(0.0, 0.0, dz);
                if (!this.immovable && d4 != dz) {
                    dz = 0.0;
                    dy = 0.0;
                    dx = 0.0;
                }

                if (!this.immovable && d3 != dy) {
                    dz = 0.0;
                    dy = 0.0;
                    dx = 0.0;
                } else {
                    dy = -this.stepHeight;

                    for (int j4 = 0; j4 < list.size(); j4++) {
                        dy = ((AxisAlignedBB)list.get(j4)).calculateYOffset(this.boundingBox, dy);
                    }

                    this.boundingBox.offset(0.0, dy, 0.0);
                }

                if (d8 * d8 + d7 * d7 >= dx * dx + dz * dz) {
                    dx = d8;
                    dy = d6;
                    dz = d7;
                    this.boundingBox.setBB(axisalignedbb1);
                } else {
                    double d11 = this.boundingBox.minY - (int)this.boundingBox.minY;
                    if (d11 > 0.0) {
                        this.ySize = (float)(this.ySize + (d11 + 0.01));
                    }
                }
            }

            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
            this.isCollidedHorizontally = d2 != dx || d4 != dz;
            this.isCollidedVertically = d3 != dy;
            this.onGround = d3 != dy && d3 < 0.0;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            this.updateFallState(dy, this.onGround);
            if (d2 != dx) {
                this.motionX = 0.0;
            }

            if (d3 != dy) {
                this.motionY = 0.0;
            }

            if (d4 != dz) {
                this.motionZ = 0.0;
            }

            double d9 = this.posX - d0;
            double d10 = this.posZ - d1;
            if (this.makesSteps() && !flag && this.ridingEntity == null) {
                this.distanceWalkedModified = (float)(this.distanceWalkedModified + MathHelper.sqrt_double(d9 * d9 + d10 * d10) * 0.6);
                int k2 = MathHelper.floor_double(this.posX);
                int k = MathHelper.floor_double(this.posY - 0.2F - this.yOffset);
                int j3 = MathHelper.floor_double(this.posZ);
                int k4 = this.worldObj.getBlockId(k2, k, j3);
                if (this.worldObj.getBlockId(k2, k - 1, j3) == Block.fence.blockID) {
                    k4 = this.worldObj.getBlockId(k2, k - 1, j3);
                }

                if (this.distanceWalkedModified > this.nextStepDistance && k4 > 0) {
                    this.nextStepDistance++;
                    Block.StepSound block$stepsound = Block.blocksList[k4].stepSound;
                    if (this.worldObj.getBlockId(k2, k + 1, j3) == Block.snow.blockID) {
                        block$stepsound = Block.snow.stepSound;
                        this.worldObj.playSoundAtEntity(this, block$stepsound.getStepping(), block$stepsound.getVolume() * 0.15F, block$stepsound.getPitch());
                    } else if (!Block.blocksList[k4].blockMaterial.getIsLiquid()) {
                        this.worldObj.playSoundAtEntity(this, block$stepsound.getStepping(), block$stepsound.getVolume() * 0.15F, block$stepsound.getPitch());
                    }

                    Block.blocksList[k4].onEntityWalking(this.worldObj, k2, k, j3, this);
                }
            }

            int l2 = MathHelper.floor_double(this.boundingBox.minX + 0.001);
            int i3 = MathHelper.floor_double(this.boundingBox.minY + 0.001);
            int k3 = MathHelper.floor_double(this.boundingBox.minZ + 0.001);
            int l4 = MathHelper.floor_double(this.boundingBox.maxX - 0.001);
            int i5 = MathHelper.floor_double(this.boundingBox.maxY - 0.001);
            int i1 = MathHelper.floor_double(this.boundingBox.maxZ - 0.001);
            if (this.worldObj.checkChunksExist(l2, i3, k3, l4, i5, i1)) {
                for (int j1 = l2; j1 <= l4; j1++) {
                    for (int k1 = i3; k1 <= i5; k1++) {
                        for (int l1 = k3; l1 <= i1; l1++) {
                            int i2 = this.worldObj.getBlockId(j1, k1, l1);
                            if (i2 > 0) {
                                Block.blocksList[i2].onEntityCollidedWithBlock(this.worldObj, j1, k1, l1, this);
                            }
                        }
                    }
                }
            }

            boolean flag2 = this.isInWaterOrRain();
            if (this.worldObj.isBoundingBoxBurning(this.boundingBox.getInsetBoundingBox(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!flag2) {
                    this.fire++;
                    if (this.fire == 0) {
                        this.fire = 300;
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.fireResistance;
            }

            if (flag2 && this.fire > 0) {
                this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.fire = -this.fireResistance;
            }
        }
    }

    protected boolean makesSteps() {
        return true;
    }

    protected void updateFallState(double dy, boolean onGround) {
        if (onGround) {
            if (this.fallDistance > 0.0F) {
                this.fall(this.fallDistance);
                this.fallDistance = 0.0F;
            }
        } else if (dy < 0.0) {
            this.fallDistance = (float)(this.fallDistance - dy);
        }
    }

    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    protected void dealFireDamage(int amount) {
        if (!this.isImmuneToFire) {
            this.attackEntityFrom(null, amount);
        }
    }

    protected void fall(float distance) {
        if (this.riddenByEntity != null) {
            this.riddenByEntity.fall(distance);
        }
    }

    public boolean isInWaterOrRain() {
        return this.inWater
            || this.worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
    }

    public boolean isInWater() {
        return this.inWater;
    }

    public boolean handleWaterMovement() {
        return this.worldObj
            .handleMaterialAcceleration(this.boundingBox.expand(0.0, -0.4F, 0.0).getInsetBoundingBox(0.001, 0.001, 0.001), Material.water, this);
    }

    public boolean isInsideOfMaterial(Material liquid) {
        double d0 = this.posY + this.getEyeHeight();
        int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_float(MathHelper.floor_double(d0));
        int k = MathHelper.floor_double(this.posZ);
        int l = this.worldObj.getBlockId(i, j, k);
        if (l != 0 && Block.blocksList[l].blockMaterial == liquid) {
            float f = BlockFluid.setFluidHeight(this.worldObj.getBlockMetadata(i, j, k)) - 0.11111111F;
            float f1 = j + 1 - f;
            return d0 < f1;
        } else {
            return false;
        }
    }

    public float getEyeHeight() {
        return 0.0F;
    }

    public boolean handleLavaMovement() {
        return this.worldObj.isMaterialInBB(this.boundingBox.expand(-0.1F, -0.4F, -0.1F), Material.lava);
    }

    public void moveFlying(float sideways, float forwards, final float scale) {
        float sqrt_float = MathHelper.sqrt_float(sideways * sideways + forwards * forwards);
        if (sqrt_float < 0.01f) {
            return;
        }
        if (sqrt_float < 1.0f) {
            sqrt_float = 1.0f;
        }
        final float n = scale / sqrt_float;
        sideways *= n;
        forwards *= n;
        final float sin = MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f);
        final float cos = MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f);
        this.motionX += sideways * cos - forwards * sin;
        this.motionZ += forwards * cos + sideways * sin;
    }

    public float getEntityBrightness(float tickDelta) {
        int i = MathHelper.floor_double(this.posX);
        double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66;
        int j = MathHelper.floor_double(this.posY - this.yOffset + d0);
        int k = MathHelper.floor_double(this.posZ);
        if (this.worldObj
            .checkChunksExist(
                MathHelper.floor_double(this.boundingBox.minX),
                MathHelper.floor_double(this.boundingBox.minY),
                MathHelper.floor_double(this.boundingBox.minZ),
                MathHelper.floor_double(this.boundingBox.maxX),
                MathHelper.floor_double(this.boundingBox.maxY),
                MathHelper.floor_double(this.boundingBox.maxZ)
            )) {
            float f = this.worldObj.getLightBrightness(i, j, k);
            if (f < this.minimumBrightness) {
                f = this.minimumBrightness;
            }

            return f;
        } else {
            return this.minimumBrightness;
        }
    }

    public void setWorldHandler(World world) {
        this.worldObj = world;
    }

    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        this.ySize = 0.0F;
        double d0 = this.prevRotationYaw - yaw;
        if (d0 < -180.0) {
            this.prevRotationYaw += 360.0F;
        }

        if (d0 >= 180.0) {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
    }

    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.lastTickPosX = this.prevPosX = this.posX = x;
        this.lastTickPosY = this.prevPosY = this.posY = y + this.yOffset;
        this.lastTickPosZ = this.prevPosZ = this.posZ = z;
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public float getDistanceToEntity(Entity entity) {
        float f = (float)(this.posX - entity.posX);
        float f1 = (float)(this.posY - entity.posY);
        float f2 = (float)(this.posZ - entity.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public double getDistanceSq(double x, double y, double z) {
        double d0 = this.posX - x;
        double d1 = this.posY - y;
        double d2 = this.posZ - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double getDistance(double x, double y, double z) {
        double d0 = this.posX - x;
        double d1 = this.posY - y;
        double d2 = this.posZ - z;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public double getDistanceSqToEntity(Entity entity) {
        double d0 = this.posX - entity.posX;
        double d1 = this.posY - entity.posY;
        double d2 = this.posZ - entity.posZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void onCollideWithPlayer(EntityPlayer player) {
    }

    public void applyEntityCollision(final Entity entity) {
        if (entity.riddenByEntity == this || entity.ridingEntity == this) {
            return;
        }
        final double a = entity.posX - this.posX;
        final double b = entity.posZ - this.posZ;
        final double abs_max = MathHelper.abs_max(a, b);
        if (abs_max >= 0.009999999776482582) {
            final double n = MathHelper.sqrt_double(abs_max);
            final double n2 = a / n;
            final double n3 = b / n;
            double n4 = 1.0 / n;
            if (n4 > 1.0) {
                n4 = 1.0;
            }
            final double n5 = n2 * n4;
            final double n6 = n3 * n4;
            final double n7 = n5 * 0.05000000074505806;
            final double n8 = n6 * 0.05000000074505806;
            final double dx = n7 * (1.0f - this.entityCollisionReduction);
            final double dz = n8 * (1.0f - this.entityCollisionReduction);
            this.addVelocity(-dx, 0.0, -dz);
            entity.addVelocity(dx, 0.0, dz);
        }
    }

    public void addVelocity(double dx, double dy, double dz) {
        this.motionX += dx;
        this.motionY += dy;
        this.motionZ += dz;
    }

    protected void setBeenAttacked() {
        this.beenAttacked = true;
    }

    public boolean attackEntityFrom(Entity source, int amount) {
        this.setBeenAttacked();
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public boolean canBePushed() {
        return false;
    }

    public void addToPlayerScore(Entity victim, int score) {
    }

    public boolean addEntityID(NBTTagCompound nbt) {
        String string = this.getEntityString();
        if (this.isDead || string == null) {
            return false;
        }
        nbt.setString("id", string);
        this.writeToNBT(nbt);
        return true;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY + this.ySize, this.posZ));
        nbt.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
        nbt.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
        nbt.setFloat("FallDistance", this.fallDistance);
        nbt.setShort("Fire", (short)this.fire);
        nbt.setShort("Air", (short)this.air);
        nbt.setBoolean("OnGround", this.onGround);
        this.writeEntityToNBT(nbt);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList nbttaglist = nbt.getTagList("Pos");
        NBTTagList nbttaglist1 = nbt.getTagList("Motion");
        NBTTagList nbttaglist2 = nbt.getTagList("Rotation");
        this.motionX = ((NBTTagDouble)nbttaglist1.tagAt(0)).doubleValue;
        this.motionY = ((NBTTagDouble)nbttaglist1.tagAt(1)).doubleValue;
        this.motionZ = ((NBTTagDouble)nbttaglist1.tagAt(2)).doubleValue;
        if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
        }

        if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
        }

        if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
        }

        this.prevPosX = this.lastTickPosX = this.posX = ((NBTTagDouble)nbttaglist.tagAt(0)).doubleValue;
        this.prevPosY = this.lastTickPosY = this.posY = ((NBTTagDouble)nbttaglist.tagAt(1)).doubleValue;
        this.prevPosZ = this.lastTickPosZ = this.posZ = ((NBTTagDouble)nbttaglist.tagAt(2)).doubleValue;
        this.prevRotationYaw = this.rotationYaw = ((NBTTagFloat)nbttaglist2.tagAt(0)).floatValue;
        this.prevRotationPitch = this.rotationPitch = ((NBTTagFloat)nbttaglist2.tagAt(1)).floatValue;
        this.fallDistance = nbt.getFloat("FallDistance");
        this.fire = nbt.getShort("Fire");
        this.air = nbt.getShort("Air");
        this.onGround = nbt.getBoolean("OnGround");
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(this.rotationYaw, this.rotationPitch);
        this.readEntityFromNBT(nbt);
    }

    protected final String getEntityString() {
        return EntityList.getEntityString(this);
    }

    protected abstract void readEntityFromNBT(NBTTagCompound nbt);

    protected abstract void writeEntityToNBT(NBTTagCompound nbt);

    protected NBTTagList newDoubleNBTList(double... values) {
        NBTTagList nbttaglist = new NBTTagList();

        for (double d0 : values) {
            nbttaglist.setTag(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    protected NBTTagList newFloatNBTList(float... values) {
        NBTTagList nbttaglist = new NBTTagList();

        for (float f : values) {
            nbttaglist.setTag(new NBTTagFloat(f));
        }

        return nbttaglist;
    }

    public EntityItem dropItem(int item, int amount) {
        return this.dropItemWithOffset(item, amount, 0.0F);
    }

    public EntityItem dropItemWithOffset(int item, int amount, float offsetY) {
        return this.entityDropItem(new ItemStack(item, amount, 0), offsetY);
    }

    public EntityItem entityDropItem(ItemStack item, float offsetY) {
        EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + offsetY, this.posZ, item);
        entityitem.delayBeforeCanPickup = 10;
        this.worldObj.entityJoinedWorld(entityitem);
        return entityitem;
    }

    public boolean isEntityAlive() {
        return !this.isDead;
    }

    public boolean isEntityInsideOpaqueBlock() {
        for (int i = 0; i < 8; i++) {
            float f = ((i >> 0) % 2 - 0.5F) * this.width * 0.9F;
            float f1 = ((i >> 1) % 2 - 0.5F) * 0.1F;
            float f2 = ((i >> 2) % 2 - 0.5F) * this.width * 0.9F;
            int j = MathHelper.floor_double(this.posX + f);
            int k = MathHelper.floor_double(this.posY + this.getEyeHeight() + f1);
            int l = MathHelper.floor_double(this.posZ + f2);
            if (this.worldObj.isBlockNormalCube(j, k, l)) {
                return true;
            }
        }

        return false;
    }

    public boolean interact(EntityPlayer player) {
        return false;
    }

    public AxisAlignedBB getCollisionAgainstShape(Entity other) {
        return null;
    }

    public void updateRidden() {
        if (this.ridingEntity.isDead) {
            this.ridingEntity = null;
        } else {
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
            this.onUpdate();
            if (this.ridingEntity != null) {
                this.ridingEntity.updateRiderPosition();
                this.entityRiderYawDelta = this.entityRiderYawDelta + (this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
                this.entityRiderPitchDelta = this.entityRiderPitchDelta + (this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

                while (this.entityRiderYawDelta >= 180.0) {
                    this.entityRiderYawDelta -= 360.0;
                }

                while (this.entityRiderYawDelta < -180.0) {
                    this.entityRiderYawDelta += 360.0;
                }

                while (this.entityRiderPitchDelta >= 180.0) {
                    this.entityRiderPitchDelta -= 360.0;
                }

                while (this.entityRiderPitchDelta < -180.0) {
                    this.entityRiderPitchDelta += 360.0;
                }

                double d0 = this.entityRiderYawDelta * 0.5;
                double d1 = this.entityRiderPitchDelta * 0.5;
                float f = 10.0F;
                if (d0 > f) {
                    d0 = f;
                }

                if (d0 < -f) {
                    d0 = -f;
                }

                if (d1 > f) {
                    d1 = f;
                }

                if (d1 < -f) {
                    d1 = -f;
                }

                this.entityRiderYawDelta -= d0;
                this.entityRiderPitchDelta -= d1;
                this.rotationYaw = (float)(this.rotationYaw + d0);
                this.rotationPitch = (float)(this.rotationPitch + d1);
            }
        }
    }

    public void updateRiderPosition() {
        this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getMountedYOffset() {
        return this.height * 0.75;
    }

    public void mountEntity(Entity entity) {
        this.entityRiderPitchDelta = 0.0;
        this.entityRiderYawDelta = 0.0;
        if (entity == null) {
            if (this.ridingEntity != null) {
                this.setLocationAndAngles(this.ridingEntity.posX, this.ridingEntity.boundingBox.minY + (double) this.ridingEntity.height, this.ridingEntity.posZ, this.rotationYaw, this.rotationPitch);
                this.ridingEntity.riddenByEntity = null;
            }
            this.ridingEntity = null;
            return;
        }
        if (this.ridingEntity == entity) {
            this.ridingEntity.riddenByEntity = null;
            this.ridingEntity = null;
            this.setLocationAndAngles(entity.posX, entity.boundingBox.minY + (double) entity.height, entity.posZ, this.rotationYaw, this.rotationPitch);
            return;
        }
        if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
        }
        if (entity.riddenByEntity != null) {
            entity.riddenByEntity.ridingEntity = null;
        }
        this.ridingEntity = entity;
        entity.riddenByEntity = this;
    }

    public Vec3D getLookVec() {
        return null;
    }

    public void setInPortal() {
    }

    public ItemStack[] getInventory() {
        return null;
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public void setSneaking(boolean sneaking) {
        this.setFlag(1, sneaking);
    }

    protected boolean getFlag(int index) {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << index) != 0;
    }

    protected void setFlag(int index, boolean value) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(0);
        if (value) {
            this.dataWatcher.updateObject(0, (byte)(b0 | 1 << index));
        } else {
            this.dataWatcher.updateObject(0, (byte)(b0 & ~(1 << index)));
        }
    }

    public void onStruckByLightning(EntityLightningBolt lightning) {
        this.dealFireDamage(5);
        this.fire++;
        if (this.fire == 0) {
            this.fire = 300;
        }
    }

    public void onKill(EntityLiving victim) {
    }

    protected boolean pushAwayFrom(double x, double y, double z) {
        int i = MathHelper.floor_double(x);
        int j = MathHelper.floor_double(y);
        int k = MathHelper.floor_double(z);
        double d0 = x - i;
        double d1 = y - j;
        double d2 = z - k;
        if (this.worldObj.isBlockNormalCube(i, j, k)) {
            boolean flag = !this.worldObj.isBlockNormalCube(i - 1, j, k);
            boolean flag1 = !this.worldObj.isBlockNormalCube(i + 1, j, k);
            boolean flag2 = !this.worldObj.isBlockNormalCube(i, j - 1, k);
            boolean flag3 = !this.worldObj.isBlockNormalCube(i, j + 1, k);
            boolean flag4 = !this.worldObj.isBlockNormalCube(i, j, k - 1);
            boolean flag5 = !this.worldObj.isBlockNormalCube(i, j, k + 1);
            byte b0 = -1;
            double d3 = 9999.0;
            if (flag && d0 < d3) {
                d3 = d0;
                b0 = 0;
            }

            if (flag1 && 1.0 - d0 < d3) {
                d3 = 1.0 - d0;
                b0 = 1;
            }

            if (flag2 && d1 < d3) {
                d3 = d1;
                b0 = 2;
            }

            if (flag3 && 1.0 - d1 < d3) {
                d3 = 1.0 - d1;
                b0 = 3;
            }

            if (flag4 && d2 < d3) {
                d3 = d2;
                b0 = 4;
            }

            if (flag5 && 1.0 - d2 < d3) {
                d3 = 1.0 - d2;
                b0 = 5;
            }

            float f = this.rand.nextFloat() * 0.2F + 0.1F;
            if (b0 == 0) {
                this.motionX = -f;
            }

            if (b0 == 1) {
                this.motionX = f;
            }

            if (b0 == 2) {
                this.motionY = -f;
            }

            if (b0 == 3) {
                this.motionY = f;
            }

            if (b0 == 4) {
                this.motionZ = -f;
            }

            if (b0 == 5) {
                this.motionZ = f;
            }
        }

        return false;
    }
}
