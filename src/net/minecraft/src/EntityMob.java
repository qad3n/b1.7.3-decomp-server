package net.minecraft.src;


public class EntityMob
extends EntityCreature
implements IMob {
    protected int attackStrength = 2;

    public EntityMob(World world) {
        super(world);
        this.health = 20;
    }

    public void onLivingUpdate() {
        float f = this.getEntityBrightness(1.0f);
        if (f > 0.5f) {
            this.age += 2;
        }
        super.onLivingUpdate();
    }

    public void onUpdate() {
        super.onUpdate();
        if (!this.worldObj.singleplayerWorld && this.worldObj.difficultySetting == 0) {
            this.setEntityDead();
        }
    }

    protected Entity findPlayerToAttack() {
        EntityPlayer entityPlayer = this.worldObj.getClosestPlayerToEntity(this, 16.0);
        if (entityPlayer != null && this.canEntityBeSeen(entityPlayer)) {
            return entityPlayer;
        }
        return null;
    }

    public boolean attackEntityFrom(Entity source, int amount) {
        if (super.attackEntityFrom(source, amount)) {
            if (this.riddenByEntity == source || this.ridingEntity == source) {
                return true;
            }
            if (source != this) {
                this.playerToAttack = source;
            }
            return true;
        }
        return false;
    }

    protected void attackEntity(Entity target, float distance) {
        if (this.attackTime <= 0 && distance < 2.0f && target.boundingBox.maxY > this.boundingBox.minY && target.boundingBox.minY < this.boundingBox.maxY) {
            this.attackTime = 20;
            target.attackEntityFrom(this, this.attackStrength);
        }
    }

    protected float getBlockPathWeight(int x, int y, int z) {
        return 0.5f - this.worldObj.getLightBrightness(x, y, z);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    @Override
public boolean getCanSpawnHere() {
    int i = MathHelper.floor_double(this.posX);
    int j = MathHelper.floor_double(this.boundingBox.minY);
    int k = MathHelper.floor_double(this.posZ);
    if (this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i, j, k) > this.rand.nextInt(32)) {
        return false;
    }
    int l = this.worldObj.getBlockLightValue(i, j, k);
    if (this.worldObj.isThundering()) {
        int i1 = this.worldObj.skylightSubtracted;
        this.worldObj.skylightSubtracted = 10;
        l = this.worldObj.getBlockLightValue(i, j, k);
        this.worldObj.skylightSubtracted = i1;
    }
    return l <= this.rand.nextInt(8) && super.getCanSpawnHere();
}
}

