package net.minecraft.src;

import java.util.List;

public class EntityMinecart extends Entity implements IInventory {
    private ItemStack[] cargoItems = new ItemStack[36];
    public int damageTaken = 0;
    public int damagedTimer = 0;
    public int forwardDirection = 1;
    private boolean flipped = false;
    public int minecartType;
    public int fuel;
    public double pushX;
    public double pushZ;
    private static final int[][][] ADJACENT_RAIL_POSITIONS_BY_SHAPE = new int[][][]{
        {{0, 0, -1}, {0, 0, 1}},
        {{-1, 0, 0}, {1, 0, 0}},
        {{-1, -1, 0}, {1, 0, 0}},
        {{-1, 0, 0}, {1, -1, 0}},
        {{0, 0, -1}, {0, -1, 1}},
        {{0, -1, -1}, {0, 0, 1}},
        {{0, 0, 1}, {1, 0, 0}},
        {{0, 0, 1}, {-1, 0, 0}},
        {{0, 0, -1}, {-1, 0, 0}},
        {{0, 0, -1}, {1, 0, 0}}
    };
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    public EntityMinecart(World var1) {
        super(var1);
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;
    }

    @Override
    protected boolean makesSteps() {
        return false;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public AxisAlignedBB getCollisionAgainstShape(Entity other) {
        return other.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    public EntityMinecart(World world, double x, double y, double z, int type) {
        this(world);
        this.setPosition(x, y + this.yOffset, z);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.minecartType = type;
    }

    @Override
    public double getMountedYOffset() {
        return this.height * 0.0 - 0.3F;
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        if (!this.worldObj.singleplayerWorld && !this.isDead) {
            this.forwardDirection = -this.forwardDirection;
            this.damagedTimer = 10;
            this.setBeenAttacked();
            this.damageTaken += amount * 10;
            if (this.damageTaken > 40) {
                if (this.riddenByEntity != null) {
                    this.riddenByEntity.mountEntity(this);
                }

                this.setEntityDead();
                this.dropItemWithOffset(Item.minecartEmpty.shiftedIndex, 1, 0.0F);
                if (this.minecartType == 1) {
                    EntityMinecart entityminecart = this;

                    for (int i = 0; i < entityminecart.getSizeInventory(); i++) {
                        ItemStack itemstack = entityminecart.getStackInSlot(i);
                        if (itemstack != null) {
                            float f = this.rand.nextFloat() * 0.8F + 0.1F;
                            float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                            float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                            while (itemstack.stackSize > 0) {
                                int j = this.rand.nextInt(21) + 10;
                                if (j > itemstack.stackSize) {
                                    j = itemstack.stackSize;
                                }

                                itemstack.stackSize -= j;
                                EntityItem entityitem = new EntityItem(
                                    this.worldObj, this.posX + f, this.posY + f1, this.posZ + f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage())
                                );
                                float f3 = 0.05F;
                                entityitem.motionX = (float)this.rand.nextGaussian() * f3;
                                entityitem.motionY = (float)this.rand.nextGaussian() * f3 + 0.2F;
                                entityitem.motionZ = (float)this.rand.nextGaussian() * f3;
                                this.worldObj.entityJoinedWorld(entityitem);
                            }
                        }
                    }

                    this.dropItemWithOffset(Block.chest.blockID, 1, 0.0F);
                } else if (this.minecartType == 2) {
                    this.dropItemWithOffset(Block.stoneOvenIdle.blockID, 1, 0.0F);
                }
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void setEntityDead() {
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack itemstack = this.getStackInSlot(i);
            if (itemstack != null) {
                float f = this.rand.nextFloat() * 0.8F + 0.1F;
                float f1 = this.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = this.rand.nextFloat() * 0.8F + 0.1F;

                while (itemstack.stackSize > 0) {
                    int j = this.rand.nextInt(21) + 10;
                    if (j > itemstack.stackSize) {
                        j = itemstack.stackSize;
                    }

                    itemstack.stackSize -= j;
                    EntityItem entityitem = new EntityItem(
                        this.worldObj, this.posX + f, this.posY + f1, this.posZ + f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage())
                    );
                    float f3 = 0.05F;
                    entityitem.motionX = (float)this.rand.nextGaussian() * f3;
                    entityitem.motionY = (float)this.rand.nextGaussian() * f3 + 0.2F;
                    entityitem.motionZ = (float)this.rand.nextGaussian() * f3;
                    this.worldObj.entityJoinedWorld(entityitem);
                }
            }
        }

        super.setEntityDead();
    }

    @Override
    public void onUpdate() {
        if (this.damagedTimer > 0) {
            this.damagedTimer--;
        }

        if (this.damageTaken > 0) {
            this.damageTaken--;
        }

        if (this.worldObj.singleplayerWorld && this.lerpSteps > 0) {
            if (this.lerpSteps > 0) {
                double d20 = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
                double d21 = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
                double d1 = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
                double d22 = this.lerpYaw - this.rotationYaw;

                while (d22 < -180.0) {
                    d22 += 360.0;
                }

                while (d22 >= 180.0) {
                    d22 -= 360.0;
                }

                this.rotationYaw = (float)(this.rotationYaw + d22 / this.lerpSteps);
                this.rotationPitch = (float)(this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);
                this.lerpSteps--;
                this.setPosition(d20, d21, d1);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.04F;
            int i = MathHelper.floor_double(this.posX);
            int j = MathHelper.floor_double(this.posY);
            int k = MathHelper.floor_double(this.posZ);
            if (BlockRail.isRail(this.worldObj, i, j - 1, k)) {
                j--;
            }

            double d0 = 0.4;
            boolean flag = false;
            double d2 = 0.0078125;
            int l = this.worldObj.getBlockId(i, j, k);
            if (BlockRail.isRail(l)) {
                Vec3D vec3d = this.snapPositionToRail(this.posX, this.posY, this.posZ);
                int i1 = this.worldObj.getBlockMetadata(i, j, k);
                this.posY = j;
                boolean flag1 = false;
                boolean flag2 = false;
                if (l == Block.railPowered.blockID) {
                    flag1 = (i1 & 8) != 0;
                    flag2 = !flag1;
                }

                if (((BlockRail)Block.blocksList[l]).isAlwaysStraight()) {
                    i1 &= 7;
                }

                if (i1 >= 2 && i1 <= 5) {
                    this.posY = j + 1;
                }

                if (i1 == 2) {
                    this.motionX -= d2;
                }

                if (i1 == 3) {
                    this.motionX += d2;
                }

                if (i1 == 4) {
                    this.motionZ += d2;
                }

                if (i1 == 5) {
                    this.motionZ -= d2;
                }

                int[][] aint = ADJACENT_RAIL_POSITIONS_BY_SHAPE[i1];
                double d3 = aint[1][0] - aint[0][0];
                double d4 = aint[1][2] - aint[0][2];
                double d5 = Math.sqrt(d3 * d3 + d4 * d4);
                double d6 = this.motionX * d3 + this.motionZ * d4;
                if (d6 < 0.0) {
                    d3 = -d3;
                    d4 = -d4;
                }

                double d7 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.motionX = d7 * d3 / d5;
                this.motionZ = d7 * d4 / d5;
                if (flag2) {
                    double d8 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    if (d8 < 0.03) {
                        this.motionX *= 0.0;
                        this.motionY *= 0.0;
                        this.motionZ *= 0.0;
                    } else {
                        this.motionX *= 0.5;
                        this.motionY *= 0.0;
                        this.motionZ *= 0.5;
                    }
                }

                double d26 = 0.0;
                double d9 = i + 0.5 + aint[0][0] * 0.5;
                double d10 = k + 0.5 + aint[0][2] * 0.5;
                double d11 = i + 0.5 + aint[1][0] * 0.5;
                double d12 = k + 0.5 + aint[1][2] * 0.5;
                d3 = d11 - d9;
                d4 = d12 - d10;
                if (d3 == 0.0) {
                    this.posX = i + 0.5;
                    d26 = this.posZ - k;
                } else if (d4 == 0.0) {
                    this.posZ = k + 0.5;
                    d26 = this.posX - i;
                } else {
                    double d13 = this.posX - d9;
                    double d14 = this.posZ - d10;
                    double d15 = (d13 * d3 + d14 * d4) * 2.0;
                    d26 = d15;
                }

                this.posX = d9 + d3 * d26;
                this.posZ = d10 + d4 * d26;
                this.setPosition(this.posX, this.posY + this.yOffset, this.posZ);
                double d27 = this.motionX;
                double d28 = this.motionZ;
                if (this.riddenByEntity != null) {
                    d27 *= 0.75;
                    d28 *= 0.75;
                }

                if (d27 < -d0) {
                    d27 = -d0;
                }

                if (d27 > d0) {
                    d27 = d0;
                }

                if (d28 < -d0) {
                    d28 = -d0;
                }

                if (d28 > d0) {
                    d28 = d0;
                }

                this.moveEntity(d27, 0.0, d28);
                if (aint[0][1] != 0 && MathHelper.floor_double(this.posX) - i == aint[0][0] && MathHelper.floor_double(this.posZ) - k == aint[0][2]) {
                    this.setPosition(this.posX, this.posY + aint[0][1], this.posZ);
                } else if (aint[1][1] != 0 && MathHelper.floor_double(this.posX) - i == aint[1][0] && MathHelper.floor_double(this.posZ) - k == aint[1][2]) {
                    this.setPosition(this.posX, this.posY + aint[1][1], this.posZ);
                }

                if (this.riddenByEntity != null) {
                    this.motionX *= 0.997F;
                    this.motionY *= 0.0;
                    this.motionZ *= 0.997F;
                } else {
                    if (this.minecartType == 2) {
                        double d29 = MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
                        if (d29 > 0.01) {
                            flag = true;
                            this.pushX /= d29;
                            this.pushZ /= d29;
                            double d17 = 0.04;
                            this.motionX *= 0.8F;
                            this.motionY *= 0.0;
                            this.motionZ *= 0.8F;
                            this.motionX = this.motionX + this.pushX * d17;
                            this.motionZ = this.motionZ + this.pushZ * d17;
                        } else {
                            this.motionX *= 0.9F;
                            this.motionY *= 0.0;
                            this.motionZ *= 0.9F;
                        }
                    }

                    this.motionX *= 0.96F;
                    this.motionY *= 0.0;
                    this.motionZ *= 0.96F;
                }

                Vec3D vec3d1 = this.snapPositionToRail(this.posX, this.posY, this.posZ);
                if (vec3d1 != null && vec3d != null) {
                    double d16 = (vec3d.yCoord - vec3d1.yCoord) * 0.05;
                    d7 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    if (d7 > 0.0) {
                        this.motionX = this.motionX / d7 * (d7 + d16);
                        this.motionZ = this.motionZ / d7 * (d7 + d16);
                    }

                    this.setPosition(this.posX, vec3d1.yCoord, this.posZ);
                }

                int k1 = MathHelper.floor_double(this.posX);
                int l1 = MathHelper.floor_double(this.posZ);
                if (k1 != i || l1 != k) {
                    d7 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    this.motionX = d7 * (k1 - i);
                    this.motionZ = d7 * (l1 - k);
                }

                if (this.minecartType == 2) {
                    double d18 = MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
                    if (d18 > 0.01 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
                        this.pushX /= d18;
                        this.pushZ /= d18;
                        if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
                            this.pushX = 0.0;
                            this.pushZ = 0.0;
                        } else {
                            this.pushX = this.motionX;
                            this.pushZ = this.motionZ;
                        }
                    }
                }

                if (flag1) {
                    double d30 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    if (d30 > 0.01) {
                        double d19 = 0.06;
                        this.motionX = this.motionX + this.motionX / d30 * d19;
                        this.motionZ = this.motionZ + this.motionZ / d30 * d19;
                    } else if (i1 == 1) {
                        if (this.worldObj.isBlockNormalCube(i - 1, j, k)) {
                            this.motionX = 0.02;
                        } else if (this.worldObj.isBlockNormalCube(i + 1, j, k)) {
                            this.motionX = -0.02;
                        }
                    } else if (i1 == 0) {
                        if (this.worldObj.isBlockNormalCube(i, j, k - 1)) {
                            this.motionZ = 0.02;
                        } else if (this.worldObj.isBlockNormalCube(i, j, k + 1)) {
                            this.motionZ = -0.02;
                        }
                    }
                }
            } else {
                if (this.motionX < -d0) {
                    this.motionX = -d0;
                }

                if (this.motionX > d0) {
                    this.motionX = d0;
                }

                if (this.motionZ < -d0) {
                    this.motionZ = -d0;
                }

                if (this.motionZ > d0) {
                    this.motionZ = d0;
                }

                if (this.onGround) {
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                }

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                if (!this.onGround) {
                    this.motionX *= 0.95F;
                    this.motionY *= 0.95F;
                    this.motionZ *= 0.95F;
                }
            }

            this.rotationPitch = 0.0F;
            double d23 = this.prevPosX - this.posX;
            double d24 = this.prevPosZ - this.posZ;
            if (d23 * d23 + d24 * d24 > 0.001) {
                this.rotationYaw = (float)(Math.atan2(d24, d23) * 180.0 / Math.PI);
                if (this.flipped) {
                    this.rotationYaw += 180.0F;
                }
            }

            double d25 = this.rotationYaw - this.prevRotationYaw;

            while (d25 >= 180.0) {
                d25 -= 360.0;
            }

            while (d25 < -180.0) {
                d25 += 360.0;
            }

            if (d25 < -170.0 || d25 >= 170.0) {
                this.rotationYaw += 180.0F;
                this.flipped = !this.flipped;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2F, 0.0, 0.2F));
            if (list != null && list.size() > 0) {
                for (int j1 = 0; j1 < list.size(); j1++) {
                    Entity entity = (Entity)list.get(j1);
                    if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
                this.riddenByEntity = null;
            }

            if (flag && this.rand.nextInt(4) == 0) {
                this.fuel--;
                if (this.fuel < 0) {
                    this.pushX = this.pushZ = 0.0;
                }

                this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
            }
        }
    }

    public Vec3D snapPositionToRail(double x, double y, double z) {
        int i = MathHelper.floor_double(x);
        int j = MathHelper.floor_double(y);
        int k = MathHelper.floor_double(z);
        if (BlockRail.isRail(this.worldObj, i, j - 1, k)) {
            j--;
        }

        int l = this.worldObj.getBlockId(i, j, k);
        if (BlockRail.isRail(l)) {
            int i1 = this.worldObj.getBlockMetadata(i, j, k);
            y = j;
            if (((BlockRail)Block.blocksList[l]).isAlwaysStraight()) {
                i1 &= 7;
            }

            if (i1 >= 2 && i1 <= 5) {
                y = j + 1;
            }

            int[][] aint = ADJACENT_RAIL_POSITIONS_BY_SHAPE[i1];
            double d0 = 0.0;
            double d1 = i + 0.5 + aint[0][0] * 0.5;
            double d2 = j + 0.5 + aint[0][1] * 0.5;
            double d3 = k + 0.5 + aint[0][2] * 0.5;
            double d4 = i + 0.5 + aint[1][0] * 0.5;
            double d5 = j + 0.5 + aint[1][1] * 0.5;
            double d6 = k + 0.5 + aint[1][2] * 0.5;
            double d7 = d4 - d1;
            double d8 = (d5 - d2) * 2.0;
            double d9 = d6 - d3;
            if (d7 == 0.0) {
                x = i + 0.5;
                d0 = z - k;
            } else if (d9 == 0.0) {
                z = k + 0.5;
                d0 = x - i;
            } else {
                double d10 = x - d1;
                double d11 = z - d3;
                double d12 = (d10 * d7 + d11 * d9) * 2.0;
                d0 = d12;
            }

            x = d1 + d7 * d0;
            y = d2 + d8 * d0;
            z = d3 + d9 * d0;
            if (d8 < 0.0) {
                y++;
            }

            if (d8 > 0.0) {
                y += 0.5;
            }

            return Vec3D.createVector(x, y, z);
        } else {
            return null;
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Type", this.minecartType);
        if (this.minecartType == 2) {
            nbt.setDouble("PushX", this.pushX);
            nbt.setDouble("PushZ", this.pushZ);
            nbt.setShort("Fuel", (short)this.fuel);
        } else if (this.minecartType == 1) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 0; i < this.cargoItems.length; i++) {
                if (this.cargoItems[i] != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    nbttagcompound.setByte("Slot", (byte)i);
                    this.cargoItems[i].writeToNBT(nbttagcompound);
                    nbttaglist.setTag(nbttagcompound);
                }
            }

            nbt.setTag("Items", nbttaglist);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        this.minecartType = nbt.getInteger("Type");
        if (this.minecartType == 2) {
            this.pushX = nbt.getDouble("PushX");
            this.pushZ = nbt.getDouble("PushZ");
            this.fuel = nbt.getShort("Fuel");
        } else if (this.minecartType == 1) {
            NBTTagList nbttaglist = nbt.getTagList("Items");
            this.cargoItems = new ItemStack[this.getSizeInventory()];

            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound nbttagcompound = (NBTTagCompound)nbttaglist.tagAt(i);
                int j = nbttagcompound.getByte("Slot") & 255;
                if (j >= 0 && j < this.cargoItems.length) {
                    this.cargoItems[j] = new ItemStack(nbttagcompound);
                }
            }
        }
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        if (!this.worldObj.singleplayerWorld) {
            if (entity != this.riddenByEntity) {
                if (entity instanceof EntityLiving
                    && !(entity instanceof EntityPlayer)
                    && this.minecartType == 0
                    && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
                    && this.riddenByEntity == null
                    && entity.ridingEntity == null) {
                    entity.mountEntity(this);
                }

                double d0 = entity.posX - this.posX;
                double d1 = entity.posZ - this.posZ;
                double d2 = d0 * d0 + d1 * d1;
                if (d2 >= 1.0E-4F) {
                    d2 = MathHelper.sqrt_double(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0 / d2;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.1F;
                    d1 *= 0.1F;
                    d0 *= 1.0F - this.entityCollisionReduction;
                    d1 *= 1.0F - this.entityCollisionReduction;
                    d0 *= 0.5;
                    d1 *= 0.5;
                    if (entity instanceof EntityMinecart) {
                        double d4 = entity.posX - this.posX;
                        double d5 = entity.posZ - this.posZ;
                        double d6 = d4 * entity.motionZ + d5 * entity.prevPosX;
                        d6 *= d6;
                        if (d6 > 5.0) {
                            return;
                        }

                        double d7 = entity.motionX + this.motionX;
                        double d8 = entity.motionZ + this.motionZ;
                        if (((EntityMinecart)entity).minecartType == 2 && this.minecartType != 2) {
                            this.motionX *= 0.2F;
                            this.motionZ *= 0.2F;
                            this.addVelocity(entity.motionX - d0, 0.0, entity.motionZ - d1);
                            entity.motionX *= 0.7F;
                            entity.motionZ *= 0.7F;
                        } else if (((EntityMinecart)entity).minecartType != 2 && this.minecartType == 2) {
                            entity.motionX *= 0.2F;
                            entity.motionZ *= 0.2F;
                            entity.addVelocity(this.motionX + d0, 0.0, this.motionZ + d1);
                            this.motionX *= 0.7F;
                            this.motionZ *= 0.7F;
                        } else {
                            d7 /= 2.0;
                            d8 /= 2.0;
                            this.motionX *= 0.2F;
                            this.motionZ *= 0.2F;
                            this.addVelocity(d7 - d0, 0.0, d8 - d1);
                            entity.motionX *= 0.2F;
                            entity.motionZ *= 0.2F;
                            entity.addVelocity(d7 + d0, 0.0, d8 + d1);
                        }
                    } else {
                        this.addVelocity(-d0, 0.0, -d1);
                        entity.addVelocity(d0 / 4.0, 0.0, d1 / 4.0);
                    }
                }
            }
        }
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.cargoItems[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (this.cargoItems[slot] != null) {
            if (this.cargoItems[slot].stackSize <= amount) {
                ItemStack itemstack1 = this.cargoItems[slot];
                this.cargoItems[slot] = null;
                return itemstack1;
            }

            ItemStack itemstack = this.cargoItems[slot].splitStack(amount);
            if (this.cargoItems[slot].stackSize == 0) {
                this.cargoItems[slot] = null;
            }

            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack item) {
        this.cargoItems[slot] = item;
        if (item != null && item.stackSize > this.getInventoryStackLimit()) {
            item.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName() {
        return "Minecart";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void onInventoryChanged() {
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.minecartType == 0) {
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
                return true;
            }

            if (!this.worldObj.singleplayerWorld) {
                player.mountEntity(this);
            }
        } else if (this.minecartType == 1) {
            if (!this.worldObj.singleplayerWorld) {
                player.displayGUIChest(this);
            }
        } else if (this.minecartType == 2) {
            ItemStack itemstack = player.inventory.getCurrentItem();
            if (itemstack != null && itemstack.itemID == Item.coal.shiftedIndex) {
                if (--itemstack.stackSize == 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }

                this.fuel += 1200;
            }

            this.pushX = this.posX - player.posX;
            this.pushZ = this.posZ - player.posZ;
        }

        return true;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.isDead ? false : !(player.getDistanceSqToEntity(this) > 64.0);
    }
}
