package net.minecraft.src;

import java.util.Random;

public class EntitySheep extends EntityAnimal {
    public static final float[][] DYE_COLORS = new float[][]{
        {1.0F, 1.0F, 1.0F},
        {0.95F, 0.7F, 0.2F},
        {0.9F, 0.5F, 0.85F},
        {0.6F, 0.7F, 0.95F},
        {0.9F, 0.9F, 0.2F},
        {0.5F, 0.8F, 0.1F},
        {0.95F, 0.7F, 0.8F},
        {0.3F, 0.3F, 0.3F},
        {0.6F, 0.6F, 0.6F},
        {0.3F, 0.6F, 0.7F},
        {0.7F, 0.4F, 0.9F},
        {0.2F, 0.4F, 0.8F},
        {0.5F, 0.4F, 0.3F},
        {0.4F, 0.5F, 0.2F},
        {0.8F, 0.3F, 0.3F},
        {0.1F, 0.1F, 0.1F}
    };

    public EntitySheep(World world) {
        super(world);
        this.texture = "/mob/sheep.png";
        this.setSize(0.9F, 1.3F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, new Byte((byte)0));
    }

    @Override
    public boolean attackEntityFrom(Entity source, int amount) {
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void dropFewItems() {
        if (!this.isSheared()) {
            this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 0.0F);
        }
    }

    @Override
    protected int getDropItemId() {
        return Block.cloth.blockID;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (itemstack != null && itemstack.itemID == Item.SHEARS.shiftedIndex && !this.isSheared()) {
            if (!this.worldObj.singleplayerWorld) {
                this.setSheared(true);
                int i = 2 + this.rand.nextInt(3);

                for (int j = 0; j < i; j++) {
                    EntityItem entityitem = this.entityDropItem(new ItemStack(Block.cloth.blockID, 1, this.getFleeceColor()), 1.0F);
                    entityitem.motionY = entityitem.motionY + this.rand.nextFloat() * 0.05F;
                    entityitem.motionX = entityitem.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    entityitem.motionZ = entityitem.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                }
            }

            itemstack.damageItem(1, player);
        }

        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Sheared", this.isSheared());
        nbt.setByte("Color", (byte)this.getFleeceColor());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
        this.setFleeceColor(nbt.getByte("Color"));
    }

    @Override
    protected String getLivingSound() {
        return "mob.sheep";
    }

    @Override
    protected String getHurtSound() {
        return "mob.sheep";
    }

    @Override
    protected String getDeathSound() {
        return "mob.sheep";
    }

    public int getFleeceColor() {
        return this.dataWatcher.getWatchableObjectByte(16) & 15;
    }

    public void setFleeceColor(int id) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        this.dataWatcher.updateObject(16, (byte)(b0 & 240 | id & 15));
    }

    public boolean isSheared() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
    }

    public void setSheared(boolean sheared) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        if (sheared) {
            this.dataWatcher.updateObject(16, (byte)(b0 | 16));
        } else {
            this.dataWatcher.updateObject(16, (byte)(b0 & -17));
        }
    }

    public static int pickColor(final Random random) {
        final int nextInt = random.nextInt(100);
        if (nextInt < 5) {
            return 15;
        }
        if (nextInt < 10) {
            return 7;
        }
        if (nextInt < 15) {
            return 8;
        }
        if (nextInt < 18) {
            return 12;
        }
        if (random.nextInt(500) == 0) {
            return 6;
        }
        return 0;
    }
}
