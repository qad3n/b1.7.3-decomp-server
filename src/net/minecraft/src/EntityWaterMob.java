package net.minecraft.src;


public class EntityWaterMob
extends EntityCreature
implements IAnimals {
    public EntityWaterMob(World world) {
        super(world);
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    public boolean getCanSpawnHere() {
        return this.worldObj.checkIfAABBIsClear(this.boundingBox);
    }

    public int getTalkInterval() {
        return 120;
    }
}

