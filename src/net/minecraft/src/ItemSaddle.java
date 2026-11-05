package net.minecraft.src;

public class ItemSaddle extends Item {
    public ItemSaddle(int var1) {
        super(var1);
        this.maxStackSize = 1;
    }

    @Override
    public void saddleEntity(ItemStack stack, EntityLiving entity) {
        if (entity instanceof EntityPig) {
            EntityPig entitypig = (EntityPig)entity;
            if (!entitypig.getSaddled()) {
                entitypig.setSaddled(true);
                stack.stackSize--;
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLiving target, EntityLiving attacker) {
        this.saddleEntity(stack, target);
        return true;
    }
}
