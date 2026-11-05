package net.minecraft.src;

import java.util.List;

public class EntityLightningBolt extends EntityWeatherEffect {
    private int stage;
    public long seed = 0L;
    private int fireAttempts;

    public EntityLightningBolt(World world, double x, double y, double z) {
        super(world);
        this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
        this.stage = 2;
        this.seed = this.rand.nextLong();
        this.fireAttempts = this.rand.nextInt(3) + 1;
        if (world.difficultySetting >= 2
            && world.doChunksNearChunkExist(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 10)) {
            int i = MathHelper.floor_double(x);
            int j = MathHelper.floor_double(y);
            int k = MathHelper.floor_double(z);
            if (world.getBlockId(i, j, k) == 0 && Block.fire.canPlaceBlockAt(world, i, j, k)) {
                world.setBlockWithNotify(i, j, k, Block.fire.blockID);
            }

            for (int i1 = 0; i1 < 4; i1++) {
                j = MathHelper.floor_double(x) + this.rand.nextInt(3) - 1;
                k = MathHelper.floor_double(y) + this.rand.nextInt(3) - 1;
                int l = MathHelper.floor_double(z) + this.rand.nextInt(3) - 1;
                if (world.getBlockId(j, k, l) == 0 && Block.fire.canPlaceBlockAt(world, j, k, l)) {
                    world.setBlockWithNotify(j, k, l, Block.fire.blockID);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.stage == 2) {
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        this.stage--;
        if (this.stage < 0) {
            if (this.fireAttempts == 0) {
                this.setEntityDead();
            } else if (this.stage < -this.rand.nextInt(10)) {
                this.fireAttempts--;
                this.stage = 1;
                this.seed = this.rand.nextLong();
                if (this.worldObj
                    .doChunksNearChunkExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10)) {
                    int i = MathHelper.floor_double(this.posX);
                    int j = MathHelper.floor_double(this.posY);
                    int k = MathHelper.floor_double(this.posZ);
                    if (this.worldObj.getBlockId(i, j, k) == 0 && Block.fire.canPlaceBlockAt(this.worldObj, i, j, k)) {
                        this.worldObj.setBlockWithNotify(i, j, k, Block.fire.blockID);
                    }
                }
            }
        }

        if (this.stage >= 0) {
            double d0 = 3.0;
            List list = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(
                    this,
                    AxisAlignedBB.getBoundingBoxFromPool(this.posX - d0, this.posY - d0, this.posZ - d0, this.posX + d0, this.posY + 6.0 + d0, this.posZ + d0)
                );

            for (int l = 0; l < list.size(); l++) {
                Entity entity = (Entity)list.get(l);
                entity.onStruckByLightning(this);
            }

            this.worldObj.lightningCooldown = 2;
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
    }
}
