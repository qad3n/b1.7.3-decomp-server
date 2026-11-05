// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

public abstract class EntityAnimal extends EntityCreature implements IAnimals
{
    public EntityAnimal(final World world) {
        super(world);
    }
    
    @Override
    protected float getBlockPathWeight(final int x, final int y, final int z) {
        if (this.worldObj.getBlockId(x, y - 1, z) == Block.grass.blockID) {
            return 10.0f;
        }
        return this.worldObj.getLightBrightness(x, y, z) - 0.5f;
    }
    
    @Override
    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }
    
    @Override
    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }
    
    @Override
    public boolean getCanSpawnHere() {
        final int floor_double = MathHelper.floor_double(this.posX);
        final int floor_double2 = MathHelper.floor_double(this.boundingBox.minY);
        final int floor_double3 = MathHelper.floor_double(this.posZ);
        return this.worldObj.getBlockId(floor_double, floor_double2 - 1, floor_double3) == Block.grass.blockID && this.worldObj.getBlockLightValueNoChecks(floor_double, floor_double2, floor_double3) > 8 && super.getCanSpawnHere();
    }
    
    @Override
    public int getTalkInterval() {
        return 120;
    }
}
