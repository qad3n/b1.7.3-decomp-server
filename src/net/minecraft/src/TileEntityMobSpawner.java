package net.minecraft.src;

public class TileEntityMobSpawner extends TileEntity {
    public int delay = -1;
    private String mobID;
    public double yaw;
    public double yaw2 = 0.0;

    public TileEntityMobSpawner() {
        this.mobID = "Pig";
        this.delay = 20;
    }

    public void setMobID(String type) {
        this.mobID = type;
    }

    public boolean anyPlayerInRange() {
        return this.worldObj.getClosestPlayer(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, 16.0) != null;
    }

    @Override
    public void updateEntity() {
        this.yaw2 = this.yaw;
        if (this.anyPlayerInRange()) {
            double d0 = this.xCoord + this.worldObj.rand.nextFloat();
            double d1 = this.yCoord + this.worldObj.rand.nextFloat();
            double d2 = this.zCoord + this.worldObj.rand.nextFloat();
            this.worldObj.spawnParticle("smoke", d0, d1, d2, 0.0, 0.0, 0.0);
            this.worldObj.spawnParticle("flame", d0, d1, d2, 0.0, 0.0, 0.0);

            for (this.yaw = this.yaw + 1000.0F / (this.delay + 200.0F); this.yaw > 360.0; this.yaw2 -= 360.0) {
                this.yaw -= 360.0;
            }

            if (!this.worldObj.singleplayerWorld) {
                if (this.delay == -1) {
                    this.updateDelay();
                }

                if (this.delay > 0) {
                    this.delay--;
                    return;
                }

                byte b0 = 4;

                for (int i = 0; i < b0; i++) {
                    EntityLiving entityliving = (EntityLiving)EntityList.createEntityInWorld(this.mobID, this.worldObj);
                    if (entityliving == null) {
                        return;
                    }

                    int j = this.worldObj
                        .getEntitiesWithinAABB(
                            entityliving.getClass(),
                            AxisAlignedBB.getBoundingBoxFromPool(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1)
                                .expand(8.0, 4.0, 8.0)
                        )
                        .size();
                    if (j >= 6) {
                        this.updateDelay();
                        return;
                    }

                    if (entityliving != null) {
                        double d3 = this.xCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0;
                        double d4 = this.yCoord + this.worldObj.rand.nextInt(3) - 1;
                        double d5 = this.zCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0;
                        entityliving.setLocationAndAngles(d3, d4, d5, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
                        if (entityliving.getCanSpawnHere()) {
                            this.worldObj.entityJoinedWorld(entityliving);

                            for (int k = 0; k < 20; k++) {
                                d0 = this.xCoord + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                                d1 = this.yCoord + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                                d2 = this.zCoord + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                                this.worldObj.spawnParticle("smoke", d0, d1, d2, 0.0, 0.0, 0.0);
                                this.worldObj.spawnParticle("flame", d0, d1, d2, 0.0, 0.0, 0.0);
                            }

                            entityliving.spawnExplosionParticle();
                            this.updateDelay();
                        }
                    }
                }
            }

            super.updateEntity();
        }
    }

    private void updateDelay() {
        this.delay = 200 + this.worldObj.rand.nextInt(600);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.mobID = nbt.getString("EntityId");
        this.delay = nbt.getShort("Delay");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("EntityId", this.mobID);
        nbt.setShort("Delay", (short)this.delay);
    }
}
