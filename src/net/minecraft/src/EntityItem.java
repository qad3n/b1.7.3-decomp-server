package net.minecraft.src;


public class EntityItem
extends Entity {
    public ItemStack item;
    private int renderTicks;
    public int age = 0;
    public int delayBeforeCanPickup;
    private int health = 5;
    public float bobOffset = (float)(Math.random() * Math.PI * 2.0);

    public EntityItem(World world, double x, double y, double z, ItemStack item) {
        super(world);
        this.setSize(0.25f, 0.25f);
        this.yOffset = this.height / 2.0f;
        this.setPosition(x, y, z);
        this.item = item;
        this.rotationYaw = (float)(Math.random() * 360.0);
        this.motionX = (float)(Math.random() * (double)0.2f - (double)0.1f);
        this.motionY = 0.2f;
        this.motionZ = (float)(Math.random() * (double)0.2f - (double)0.1f);
    }

    protected boolean makesSteps() {
        return false;
    }

    public EntityItem(World world) {
        super(world);
        this.setSize(0.25f, 0.25f);
        this.yOffset = this.height / 2.0f;
    }

    protected void entityInit() {
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.delayBeforeCanPickup > 0) {
            --this.delayBeforeCanPickup;
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= (double)0.04f;
        if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava) {
            this.motionY = 0.2f;
            this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
            this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f;
            this.worldObj.playSoundAtEntity(this, "random.fizz", 0.4f, 2.0f + this.rand.nextFloat() * 0.4f);
        }
        this.pushAwayFrom(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0, this.posZ);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float f = 0.98f;
        if (this.onGround) {
            f = 0.58800006f;
            int n = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
            if (n > 0) {
                f = Block.blocksList[n].slipperiness * 0.98f;
            }
        }
        this.motionX *= (double)f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)f;
        if (this.onGround) {
            this.motionY *= -0.5;
        }
        ++this.renderTicks;
        ++this.age;
        if (this.age >= 6000) {
            this.setEntityDead();
        }
    }

    public boolean handleWaterMovement() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    protected void dealFireDamage(int amount) {
        this.attackEntityFrom(null, amount);
    }

    public boolean attackEntityFrom(Entity source, int amount) {
        this.setBeenAttacked();
        this.health -= amount;
        if (this.health <= 0) {
            this.setEntityDead();
        }
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setShort("Health", (byte)this.health);
        nbt.setShort("Age", (short)this.age);
        nbt.setCompoundTag("Item", this.item.writeToNBT(new NBTTagCompound()));
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.health = nbt.getShort("Health") & 0xFF;
        this.age = nbt.getShort("Age");
        NBTTagCompound nBTTagCompound = nbt.getCompoundTag("Item");
        this.item = new ItemStack(nBTTagCompound);
    }

    public void onCollideWithPlayer(EntityPlayer player) {
        if (this.worldObj.singleplayerWorld) {
            return;
        }
        int n = this.item.stackSize;
        if (this.delayBeforeCanPickup == 0 && player.inventory.addItemStackToInventory(this.item)) {
            if (this.item.itemID == Block.wood.blockID) {
                player.incrementStat(AchievementList.GET_LOG);
            }
            if (this.item.itemID == Item.leather.shiftedIndex) {
                player.incrementStat(AchievementList.KILL_COW);
            }
            this.worldObj.playSoundAtEntity(this, "random.pop", 0.2f, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.onItemPickup(this, n);
            if (this.item.stackSize <= 0) {
                this.setEntityDead();
            }
        }
    }
}

