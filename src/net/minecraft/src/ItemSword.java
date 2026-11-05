package net.minecraft.src;


public class ItemSword
extends Item {
    private int weaponDamage;

    public ItemSword(int id, Item.EnumToolMaterial tier) {
        super(id);
        this.maxStackSize = 1;
        this.setMaxDamage(tier.getMaxUses());
        this.weaponDamage = 4 + tier.getDamageVsEntity() * 2;
    }

    public float getStrVsBlock(ItemStack stack, Block block) {
        if (block.blockID == Block.web.blockID) {
            return 15.0f;
        }
        return 1.5f;
    }

    public boolean hitEntity(ItemStack stack, EntityLiving target, EntityLiving attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    public boolean mineBlock(ItemStack stack, int block, int x, int y, int z, EntityLiving entity) {
        stack.damageItem(2, entity);
        return true;
    }

    public int getDamageVsEntity(Entity target) {
        return this.weaponDamage;
    }

    public boolean canHarvestBlock(Block block) {
        return block.blockID == Block.web.blockID;
    }
}

