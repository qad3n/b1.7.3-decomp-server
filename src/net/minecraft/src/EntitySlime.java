package net.minecraft.src;

public class EntitySlime extends EntityLiving implements IMob {
    public float stretch;
    public float lastStretch;
    private int ticksTillJump = 0;

    public EntitySlime(World var1) {
        super(var1);
        this.texture = "/mob/slime.png";
        int i = 1 << this.rand.nextInt(3);
        this.yOffset = 0.0F;
        this.ticksTillJump = this.rand.nextInt(20) + 10;
        this.setSlimeSize(i);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)1));
    }

    public void setSlimeSize(int size) {
        this.dataWatcher.updateObject(16, new Byte((byte)size));
        this.setSize(0.6F * size, 0.6F * size);
        this.health = size * size;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public int getSize() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Size", this.getSize() - 1);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSlimeSize(nbt.getInteger("Size") + 1);
    }

    @Override
    public void onUpdate() {
        this.lastStretch = this.stretch;
        boolean flag = this.onGround;
        super.onUpdate();
        if (this.onGround && !flag) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; j++) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * i * 0.5F * f1;
                this.worldObj.spawnParticle("slime", this.posX + f2, this.boundingBox.minY, this.posZ + f3, 0.0, 0.0, 0.0);
            }

            if (i > 2) {
                this.worldObj
                    .playSoundAtEntity(this, "mob.slime", this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.stretch = -0.5F;
        }

        this.stretch *= 0.6F;
    }

    @Override
    protected void updatePlayerActionState() {
        this.checkDespawn();
        EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0);
        if (entityplayer != null) {
            this.faceEntity(entityplayer, 10.0F, 20.0F);
        }

        if (this.onGround && this.ticksTillJump-- <= 0) {
            this.ticksTillJump = this.rand.nextInt(20) + 10;
            if (entityplayer != null) {
                this.ticksTillJump /= 3;
            }

            this.isJumping = true;
            if (this.getSize() > 1) {
                this.worldObj
                    .playSoundAtEntity(this, "mob.slime", this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.stretch = 1.0F;
            this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
            this.moveForward = 1 * this.getSize();
        } else {
            this.isJumping = false;
            if (this.onGround) {
                this.moveStrafing = this.moveForward = 0.0F;
            }
        }
    }

    @Override
    public void setEntityDead() {
        int i = this.getSize();
        if (!this.worldObj.singleplayerWorld && i > 1 && this.health == 0) {
            for (int j = 0; j < 4; j++) {
                float f = (j % 2 - 0.5F) * i / 4.0F;
                float f1 = (j / 2 - 0.5F) * i / 4.0F;
                EntitySlime entityslime = new EntitySlime(this.worldObj);
                entityslime.setSlimeSize(i / 2);
                entityslime.setLocationAndAngles(this.posX + f, this.posY + 0.5, this.posZ + f1, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.entityJoinedWorld(entityslime);
            }
        }

        super.setEntityDead();
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        int i = this.getSize();
        if (i > 1 && this.canEntityBeSeen(player) && this.getDistanceToEntity(player) < 0.6 * i && player.attackEntityFrom(this, i)) {
            this.worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }
    }

    @Override
    protected String getHurtSound() {
        return "mob.slime";
    }

    @Override
    protected String getDeathSound() {
        return "mob.slime";
    }

    @Override
protected int getDropItemId() {
    if (this.getSize() == 1) {
        return Item.slimeBall.shiftedIndex;
    }
    return 0;
}

    @Override
    public boolean getCanSpawnHere() {
        Chunk chunk = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
        return (this.getSize() == 1 || this.worldObj.difficultySetting > 0)
            && this.rand.nextInt(10) == 0
            && chunk.getRandomForSlime(987234911L).nextInt(10) == 0
            && this.posY < 16.0;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }
}
