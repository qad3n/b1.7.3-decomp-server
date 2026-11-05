package net.minecraft.src;


public class EntityCow
extends EntityAnimal {
    public EntityCow(World world) {
        super(world);
        this.texture = "/mob/cow.png";
        this.setSize(0.9f, 1.3f);
    }

    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
    }

    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
    }

    protected String getLivingSound() {
        return "mob.cow";
    }

    protected String getHurtSound() {
        return "mob.cowhurt";
    }

    protected String getDeathSound() {
        return "mob.cowhurt";
    }

    protected float getSoundVolume() {
        return 0.4f;
    }

    protected int getDropItemId() {
        return Item.leather.shiftedIndex;
    }

    public boolean interact(EntityPlayer player) {
        ItemStack itemStack = player.inventory.getCurrentItem();
        if (itemStack != null && itemStack.itemID == Item.bucketEmpty.shiftedIndex) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Item.bucketMilk));
            return true;
        }
        return false;
    }
}

