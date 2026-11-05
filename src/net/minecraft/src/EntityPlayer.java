package net.minecraft.src;

import java.util.List;

public abstract class EntityPlayer extends EntityLiving {
    public InventoryPlayer inventory = new InventoryPlayer(this);
    public Container personalCraftingInventory;
    public Container currentCraftingInventory;
    public byte userType = 0;
    public int score = 0;
    public float lastBob;
    public float bob;
    public boolean isSwinging = false;
    public int swingProgressInt = 0;
    public String username;
    public int dimension;
    public double lastCapeX;
    public double lastCapeY;
    public double lastCapeZ;
    public double capeX;
    public double capeY;
    public double capeZ;
    protected boolean sleeping;
    public ChunkCoordinates playerLocation;
    private int sleepTimer;
    public float sleepingCameraOffsetX;
    public float sleepingCameraOffsetZ;
    private ChunkCoordinates spawnChunk;
    private ChunkCoordinates minecartTravelStartPos;
    public int timeUntilPortal = 20;
    protected boolean inPortal = false;
    public float timeInPortal;
    private int damageRemainder = 0;
    public EntityFish fishEntity = null;

    public EntityPlayer(World var1) {
        super(var1);
        this.personalCraftingInventory = new ContainerPlayer(this.inventory, !var1.singleplayerWorld);
        this.currentCraftingInventory = this.personalCraftingInventory;
        this.yOffset = 1.62F;
        ChunkCoordinates chunkcoordinates = var1.getSpawnPoint();
        this.setLocationAndAngles(chunkcoordinates.posX + 0.5, chunkcoordinates.posY + 1, chunkcoordinates.posZ + 0.5, 0.0F, 0.0F);
        this.health = 20;
        this.entityType = "humanoid";
        this.rotationOffset = 180.0F;
        this.fireResistance = 20;
        this.texture = "/mob/char.png";
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
    }

    @Override
    public void onUpdate() {
        if (this.isSleeping()) {
            this.sleepTimer++;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }

            if (!this.worldObj.singleplayerWorld) {
                if (!this.isInBed()) {
                    this.wakeUpPlayer(true, true, false);
                } else if (this.worldObj.isDaytime()) {
                    this.wakeUpPlayer(false, true, true);
                }
            }
        } else if (this.sleepTimer > 0) {
            this.sleepTimer++;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }

        super.onUpdate();
        if (!this.worldObj.singleplayerWorld && this.currentCraftingInventory != null && !this.currentCraftingInventory.canInteractWith(this)) {
            this.usePersonalCraftingInventory();
            this.currentCraftingInventory = this.personalCraftingInventory;
        }

        this.lastCapeX = this.capeX;
        this.lastCapeY = this.capeY;
        this.lastCapeZ = this.capeZ;
        double d0 = this.posX - this.capeX;
        double d1 = this.posY - this.capeY;
        double d2 = this.posZ - this.capeZ;
        double d3 = 10.0;
        if (d0 > d3) {
            this.lastCapeX = this.capeX = this.posX;
        }

        if (d2 > d3) {
            this.lastCapeZ = this.capeZ = this.posZ;
        }

        if (d1 > d3) {
            this.lastCapeY = this.capeY = this.posY;
        }

        if (d0 < -d3) {
            this.lastCapeX = this.capeX = this.posX;
        }

        if (d2 < -d3) {
            this.lastCapeZ = this.capeZ = this.posZ;
        }

        if (d1 < -d3) {
            this.lastCapeY = this.capeY = this.posY;
        }

        this.capeX += d0 * 0.25;
        this.capeZ += d2 * 0.25;
        this.capeY += d1 * 0.25;
        this.addStat(StatList.MINUTES_PLAYED, 1);
        if (this.ridingEntity == null) {
            this.minecartTravelStartPos = null;
        }
    }

    @Override
    protected boolean isMovementBlocked() {
        return this.health <= 0 || this.isSleeping();
    }

    protected void usePersonalCraftingInventory() {
        this.currentCraftingInventory = this.personalCraftingInventory;
    }

    @Override
    public void updateRidden() {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        super.updateRidden();
        this.lastBob = this.bob;
        this.bob = 0.0F;
        this.tickRidingRelatedStats(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    @Override
    protected void updatePlayerActionState() {
        if (this.isSwinging) {
            this.swingProgressInt++;
            if (this.swingProgressInt >= 8) {
                this.swingProgressInt = 0;
                this.isSwinging = false;
            }
        } else {
            this.swingProgressInt = 0;
        }

        this.swingProgress = this.swingProgressInt / 8.0F;
    }

    @Override
    public void onLivingUpdate() {
        if (this.worldObj.difficultySetting == 0 && this.health < 20 && this.ticksExisted % 20 * 12 == 0) {
            this.heal(1);
        }

        this.inventory.decrementAnimations();
        this.lastBob = this.bob;
        super.onLivingUpdate();
        float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float f1 = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.health <= 0) {
            f = 0.0F;
        }

        if (this.onGround || this.health <= 0) {
            f1 = 0.0F;
        }

        this.bob = this.bob + (f - this.bob) * 0.4F;
        this.tilt = this.tilt + (f1 - this.tilt) * 0.8F;
        if (this.health > 0) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0, 0.0, 1.0));
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Entity entity = (Entity)list.get(i);
                    if (!entity.isDead) {
                        this.onEntityCollision(entity);
                    }
                }
            }
        }
    }

    private void onEntityCollision(Entity entity) {
        entity.onCollideWithPlayer(this);
    }

    @Override
    public void onDeath(Entity killer) {
        super.onDeath(killer);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.1F;
        if (this.username.equals("Notch")) {
            this.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
        }

        this.inventory.dropAllItems();
        if (killer != null) {
            this.motionX = -MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
            this.motionZ = -MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F) * 0.1F;
        } else {
            this.motionX = this.motionZ = 0.0;
        }

        this.yOffset = 0.1F;
        this.addStat(StatList.DEATHS, 1);
    }

    @Override
    public void addToPlayerScore(Entity victim, int score) {
        this.score += score;
        if (victim instanceof EntityPlayer) {
            this.addStat(StatList.PLAYERS_KILLED, 1);
        } else {
            this.addStat(StatList.MOBS_KILLED, 1);
        }
    }

    public void dropCurrentItem() {
        this.dropPlayerItemWithRandomChoice(this.inventory.decrStackSize(this.inventory.currentItem, 1), false);
    }

    public void dropPlayerItem(ItemStack item) {
        this.dropPlayerItemWithRandomChoice(item, false);
    }

    public void dropPlayerItemWithRandomChoice(ItemStack item, boolean dead) {
        if (item != null) {
            EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY - 0.3F + this.getEyeHeight(), this.posZ, item);
            entityitem.delayBeforeCanPickup = 40;
            float f = 0.1F;
            if (dead) {
                float f1 = this.rand.nextFloat() * 0.5F;
                float f2 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                entityitem.motionX = -MathHelper.sin(f2) * f1;
                entityitem.motionZ = MathHelper.cos(f2) * f1;
                entityitem.motionY = 0.2F;
            } else {
                f = 0.3F;
                entityitem.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI)
                    * f;
                entityitem.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI)
                    * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI)
                    * f;
                entityitem.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f + 0.1F;
                f = 0.02F;
                float f3 = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                f *= this.rand.nextFloat();
                entityitem.motionX = entityitem.motionX + Math.cos(f3) * f;
                entityitem.motionY = entityitem.motionY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                entityitem.motionZ = entityitem.motionZ + Math.sin(f3) * f;
            }

            this.joinEntityItemWithWorld(entityitem);
            this.addStat(StatList.DROPS, 1);
        }
    }

    protected void joinEntityItemWithWorld(EntityItem item) {
        this.worldObj.entityJoinedWorld(item);
    }

    public float getCurrentPlayerStrVsBlock(Block block) {
        float f = this.inventory.getStrVsBlock(block);
        if (this.isInsideOfMaterial(Material.water)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean canHarvestBlock(Block block) {
        return this.inventory.canHarvestBlock(block);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        NBTTagList nbttaglist = nbt.getTagList("Inventory");
        this.inventory.readFromNBT(nbttaglist);
        this.dimension = nbt.getInteger("Dimension");
        this.sleeping = nbt.getBoolean("Sleeping");
        this.sleepTimer = nbt.getShort("SleepTimer");
        if (this.sleeping) {
            this.playerLocation = new ChunkCoordinates(
                MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)
            );
            this.wakeUpPlayer(true, true, false);
        }

        if (nbt.hasKey("SpawnX") && nbt.hasKey("SpawnY") && nbt.hasKey("SpawnZ")) {
            this.spawnChunk = new ChunkCoordinates(nbt.getInteger("SpawnX"), nbt.getInteger("SpawnY"), nbt.getInteger("SpawnZ"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        nbt.setInteger("Dimension", this.dimension);
        nbt.setBoolean("Sleeping", this.sleeping);
        nbt.setShort("SleepTimer", (short)this.sleepTimer);
        if (this.spawnChunk != null) {
            nbt.setInteger("SpawnX", this.spawnChunk.posX);
            nbt.setInteger("SpawnY", this.spawnChunk.posY);
            nbt.setInteger("SpawnZ", this.spawnChunk.posZ);
        }
    }

    public void displayGUIChest(IInventory inventory) {
    }

    public void displayWorkbenchGUI(int x, int y, int z) {
    }

    public void onItemPickup(Entity item, int count) {
    }

    @Override
    public float getEyeHeight() {
        return 0.12F;
    }

    protected void resetHeight() {
        this.yOffset = 1.62F;
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        this.age = 0;
        if (this.health <= 0) {
            return false;
        }

        if (this.isSleeping() && !this.worldObj.singleplayerWorld) {
            this.wakeUpPlayer(true, true, false);
        }

        if (source instanceof EntityMob || source instanceof EntityArrow) {
            if (this.worldObj.difficultySetting == 0) {
                amount = 0;
            }

            if (this.worldObj.difficultySetting == 1) {
                amount = amount / 3 + 1;
            }

            if (this.worldObj.difficultySetting == 3) {
                amount = amount * 3 / 2;
            }
        }

        if (amount == 0) {
            return false;
        }

        Entity entity = source;
        if (entity instanceof EntityArrow && ((EntityArrow)entity).owner != null) {
            entity = ((EntityArrow)entity).owner;
        }

        if (entity instanceof EntityLiving) {
            this.wolvesAttack((EntityLiving)entity, false);
        }

        this.addStat(StatList.DAMAGE_TAKEN, amount);
        return super.attackEntityFrom(source, amount);
    }

    protected boolean isPVPEnabled() {
        return false;
    }

    protected void wolvesAttack(EntityLiving target, boolean wasAttackedByTarget) {
        if (!(target instanceof EntityCreeper) && !(target instanceof EntityGhast)) {
            if (target instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf)target;
                if (entitywolf.isTame() && this.username.equals(entitywolf.getOwner())) {
                    return;
                }
            }

            if (!(target instanceof EntityPlayer) || this.isPVPEnabled()) {
                for (Entity entity : this.worldObj
                    .getEntitiesWithinAABB(
                        EntityWolf.class,
                        AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0, this.posY + 1.0, this.posZ + 1.0)
                            .expand(16.0, 4.0, 16.0)
                    )) {
                    EntityWolf entitywolf1 = (EntityWolf)entity;
                    if (entitywolf1.isTame()
                        && entitywolf1.getEntityToAttack() == null
                        && this.username.equals(entitywolf1.getOwner())
                        && (!wasAttackedByTarget || !entitywolf1.getIsSitting())) {
                        entitywolf1.setIsSitting(false);
                        entitywolf1.setEntityToAttack(target);
                    }
                }
            }
        }
    }

    @Override
    protected void damageEntity(int amount) {
        int i = 25 - this.inventory.getTotalArmorValue();
        int j = amount * i + this.damageRemainder;
        this.inventory.damageArmor(amount);
        amount = j / 25;
        this.damageRemainder = j % 25;
        super.damageEntity(amount);
    }

    public void displayGUIFurnace(TileEntityFurnace furnace) {
    }

    public void displayGUIDispenser(TileEntityDispenser dispenser) {
    }

    public void displayGUIEditSign(TileEntitySign sign) {
    }

    public void useCurrentItemOnEntity(Entity target) {
        if (!target.interact(this)) {
            ItemStack itemstack = this.getCurrentEquippedItem();
            if (itemstack != null && target instanceof EntityLiving) {
                itemstack.useItemOnEntity((EntityLiving)target);
                if (itemstack.stackSize <= 0) {
                    itemstack.onRemoved(this);
                    this.destroyCurrentEquippedItem();
                }
            }
        }
    }

    public ItemStack getCurrentEquippedItem() {
        return this.inventory.getCurrentItem();
    }

    public void destroyCurrentEquippedItem() {
        this.inventory.setInventorySlotContents(this.inventory.currentItem, null);
    }

    @Override
    public double getYOffset() {
        return this.yOffset - 0.5F;
    }

    public void swingItem() {
        this.swingProgressInt = -1;
        this.isSwinging = true;
    }

    public void attackTargetEntityWithCurrentItem(Entity target) {
        int i = this.inventory.getDamageVsEntity(target);
        if (i > 0) {
            if (this.motionY < 0.0) {
                i++;
            }

            target.attackEntityFrom(this, i);
            ItemStack itemstack = this.getCurrentEquippedItem();
            if (itemstack != null && target instanceof EntityLiving) {
                itemstack.hitEntity((EntityLiving)target, this);
                if (itemstack.stackSize <= 0) {
                    itemstack.onRemoved(this);
                    this.destroyCurrentEquippedItem();
                }
            }

            if (target instanceof EntityLiving) {
                if (target.isEntityAlive()) {
                    this.wolvesAttack((EntityLiving)target, true);
                }

                this.addStat(StatList.DAMAGE_DEALT, i);
            }
        }
    }

    public void onItemStackChanged(ItemStack item) {
    }

    @Override
    public void setEntityDead() {
        super.setEntityDead();
        this.personalCraftingInventory.onCraftGuiClosed(this);
        if (this.currentCraftingInventory != null) {
            this.currentCraftingInventory.onCraftGuiClosed(this);
        }
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return !this.sleeping && super.isEntityInsideOpaqueBlock();
    }

    public EntityPlayer.EnumStatus goToSleep(int x, int y, int z) {
        if (!this.worldObj.singleplayerWorld) {
            if (this.isSleeping() || !this.isEntityAlive()) {
                return EntityPlayer.EnumStatus.OTHER_PROBLEM;
            }

            if (this.worldObj.worldProvider.unnatural) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
            }

            if (this.worldObj.isDaytime()) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.posX - x) > 3.0 || Math.abs(this.posY - y) > 2.0 || Math.abs(this.posZ - z) > 3.0) {
                return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
            }
        }

        this.setSize(0.2F, 0.2F);
        this.yOffset = 0.2F;
        if (this.worldObj.blockExists(x, y, z)) {
            int i = this.worldObj.getBlockMetadata(x, y, z);
            int j = BlockBed.getFacing(i);
            float f = 0.5F;
            float f1 = 0.5F;
            switch (j) {
                case 0:
                    f1 = 0.9F;
                    break;
                case 2:
                    f1 = 0.1F;
                    break;
                case 1:
                    f = 0.1F;
                    break;
                case 3:
                    f = 0.9F;
            }

            this.setSleepingCameraOffset(j);
            this.setPosition(x + f, y + 0.9375F, z + f1);
        } else {
            this.setPosition(x + 0.5F, y + 0.9375F, z + 0.5F);
        }

        this.sleeping = true;
        this.sleepTimer = 0;
        this.playerLocation = new ChunkCoordinates(x, y, z);
        this.motionX = this.motionZ = this.motionY = 0.0;
        if (!this.worldObj.singleplayerWorld) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.EnumStatus.OK;
    }

    private void setSleepingCameraOffset(int facing) {
        this.sleepingCameraOffsetX = 0.0F;
        this.sleepingCameraOffsetZ = 0.0F;
        switch (facing) {
            case 0:
                this.sleepingCameraOffsetZ = -1.8F;
                break;
            case 1:
                this.sleepingCameraOffsetX = 1.8F;
                break;
            case 2:
                this.sleepingCameraOffsetZ = 1.8F;
                break;
            case 3:
                this.sleepingCameraOffsetX = -1.8F;
        }
    }

    public void wakeUpPlayer(boolean resetSleepTimer, boolean updateAllPlayersSleeping, boolean setSpawnPoint) {
        this.setSize(0.6F, 1.8F);
        this.resetHeight();
        ChunkCoordinates chunkcoordinates = this.playerLocation;
        ChunkCoordinates chunkcoordinates1 = this.playerLocation;
        if (chunkcoordinates != null && this.worldObj.getBlockId(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ) == Block.bed.blockID) {
            BlockBed.updateState(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, false);
            chunkcoordinates1 = BlockBed.getSpawnPoint(this.worldObj, chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, 0);
            if (chunkcoordinates1 == null) {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.posX, chunkcoordinates.posY + 1, chunkcoordinates.posZ);
            }

            this.setPosition(chunkcoordinates1.posX + 0.5F, chunkcoordinates1.posY + this.yOffset + 0.1F, chunkcoordinates1.posZ + 0.5F);
        }

        this.sleeping = false;
        if (!this.worldObj.singleplayerWorld && updateAllPlayersSleeping) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }

        if (resetSleepTimer) {
            this.sleepTimer = 0;
        } else {
            this.sleepTimer = 100;
        }

        if (setSpawnPoint) {
            this.setSpawnChunk(this.playerLocation);
        }
    }

    private boolean isInBed() {
        return this.worldObj.getBlockId(this.playerLocation.posX, this.playerLocation.posY, this.playerLocation.posZ) == Block.bed.blockID;
    }

    public static ChunkCoordinates loadSpawnPoint(World world, ChunkCoordinates pos) {
        IChunkProvider ichunkprovider = world.getChunkSource();
        ichunkprovider.loadChunk(pos.posX - 3 >> 4, pos.posZ - 3 >> 4);
        ichunkprovider.loadChunk(pos.posX + 3 >> 4, pos.posZ - 3 >> 4);
        ichunkprovider.loadChunk(pos.posX - 3 >> 4, pos.posZ + 3 >> 4);
        ichunkprovider.loadChunk(pos.posX + 3 >> 4, pos.posZ + 3 >> 4);
        return world.getBlockId(pos.posX, pos.posY, pos.posZ) != Block.bed.blockID ? null : BlockBed.getSpawnPoint(world, pos.posX, pos.posY, pos.posZ, 0);
    }

    @Override
    public boolean isSleeping() {
        return this.sleeping;
    }

    public boolean isPlayerFullyAsleep() {
        return this.sleeping && this.sleepTimer >= 100;
    }

    public void addMessage(String message) {
    }

    public ChunkCoordinates getSpawnChunk() {
        return this.spawnChunk;
    }

    public void setSpawnChunk(ChunkCoordinates var1) {
        if (var1 != null) {
            this.spawnChunk = new ChunkCoordinates(var1);
        } else {
            this.spawnChunk = null;
        }
    }

    public void incrementStat(StatBase stat) {
        this.addStat(stat, 1);
    }

    public void addStat(StatBase stat, int amount) {
    }

    @Override
    protected void jump() {
        super.jump();
        this.addStat(StatList.JUMPS, 1);
    }

    @Override
    public void moveEntityWithHeading(float sideways, float forwards) {
        double d0 = this.posX;
        double d1 = this.posY;
        double d2 = this.posZ;
        super.moveEntityWithHeading(sideways, forwards);
        this.tickNonRidingMovementRelatedStats(this.posX - d0, this.posY - d1, this.posZ - d2);
    }

    private void tickNonRidingMovementRelatedStats(double x, double y, double z) {
        if (this.ridingEntity == null) {
            if (this.isInsideOfMaterial(Material.water)) {
                int i = Math.round(MathHelper.sqrt_double(x * x + y * y + z * z) * 100.0F);
                if (i > 0) {
                    this.addStat(StatList.CM_DIVEN, i);
                }
            } else if (this.isInWater()) {
                int j = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (j > 0) {
                    this.addStat(StatList.CM_SWUM, j);
                }
            } else if (this.isOnLadder()) {
                if (y > 0.0) {
                    this.addStat(StatList.CM_CLIMB, (int)Math.round(y * 100.0));
                }
            } else if (this.onGround) {
                int k = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (k > 0) {
                    this.addStat(StatList.CM_WALKED, k);
                }
            } else {
                int l = Math.round(MathHelper.sqrt_double(x * x + z * z) * 100.0F);
                if (l > 25) {
                    this.addStat(StatList.CM_FLOWN, l);
                }
            }
        }
    }

    private void tickRidingRelatedStats(double x, double y, double z) {
        if (this.ridingEntity != null) {
            int i = Math.round(MathHelper.sqrt_double(x * x + y * y + z * z) * 100.0F);
            if (i > 0) {
                if (this.ridingEntity instanceof EntityMinecart) {
                    this.addStat(StatList.CM_MINECART, i);
                    if (this.minecartTravelStartPos == null) {
                        this.minecartTravelStartPos = new ChunkCoordinates(
                            MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)
                        );
                    } else if (this.minecartTravelStartPos
                            .getSqDistanceTo(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))
                        >= 1000.0) {
                        this.addStat(AchievementList.TRAVEL_KILOMETER_BY_MINECART, 1);
                    }
                } else if (this.ridingEntity instanceof EntityBoat) {
                    this.addStat(StatList.CM_SAILED, i);
                } else if (this.ridingEntity instanceof EntityPig) {
                    this.addStat(StatList.CM_PIG, i);
                }
            }
        }
    }

    @Override
    protected void fall(float distance) {
        if (distance >= 2.0F) {
            this.addStat(StatList.CM_FALLEN, (int)Math.round(distance * 100.0));
        }

        super.fall(distance);
    }

    @Override
    public void onKill(EntityLiving victim) {
        if (victim instanceof EntityMob) {
            this.incrementStat(AchievementList.KILL_ENEMY);
        }
    }

    @Override
    public void setInPortal() {
        if (this.timeUntilPortal > 0) {
            this.timeUntilPortal = 10;
        } else {
            this.inPortal = true;
        }
    }

    public enum EnumStatus {
        OK,
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW,
        TOO_FAR_AWAY,
        OTHER_PROBLEM;
    }
}
