package net.minecraft.src;

import java.util.List;

public class EntityPigZombie extends EntityZombie {
    private int angerLevel = 0;
    private int randomSoundDelay = 0;
    private static final ItemStack defaultHeldItem = new ItemStack(Item.swordGold, 1);

    public EntityPigZombie(World var1) {
        super(var1);
        this.texture = "/mob/pigzombie.png";
        this.moveSpeed = 0.5F;
        this.attackStrength = 5;
        this.isImmuneToFire = true;
    }

    @Override
    public void onUpdate() {
        this.moveSpeed = this.playerToAttack != null ? 0.95F : 0.5F;
        if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
            this.worldObj
                .playSoundAtEntity(
                    this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F
                );
        }

        super.onUpdate();
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting > 0
            && this.worldObj.checkIfAABBIsClear(this.boundingBox)
            && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.worldObj.getIsAnyLiquid(this.boundingBox);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setShort("Anger", (short)this.angerLevel);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.angerLevel = nbt.getShort("Anger");
    }

    protected Entity findPlayerToAttack() {
        if (this.angerLevel == 0) {
            return null;
        }
        return super.findPlayerToAttack();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        if (source instanceof EntityPlayer) {
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0, 32.0, 32.0));

            for (int i = 0; i < list.size(); i++) {
                Entity entity = (Entity)list.get(i);
                if (entity instanceof EntityPigZombie) {
                    EntityPigZombie entitypigzombie = (EntityPigZombie)entity;
                    entitypigzombie.becomeAngryAt(source);
                }
            }

            this.becomeAngryAt(source);
        }

        return super.attackEntityFrom(source, amount);
    }

    private void becomeAngryAt(Entity target) {
        this.playerToAttack = target;
        this.angerLevel = 400 + this.rand.nextInt(400);
        this.randomSoundDelay = this.rand.nextInt(40);
    }

    @Override
    protected String getLivingSound() {
        return "mob.zombiepig.zpig";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombiepig.zpighurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombiepig.zpigdeath";
    }

    @Override
    protected int getDropItemId() {
        return Item.porkCooked.shiftedIndex;
    }
}
