package net.minecraft.src;


public class ItemShears
extends Item {
    public ItemShears(int n) {
        super(n);
        this.setMaxStackSize(1);
        this.setMaxDamage(238);
    }

    public boolean mineBlock(ItemStack stack, int block, int x, int y, int z, EntityLiving entity) {
        if (block == Block.leaves.blockID || block == Block.web.blockID) {
            stack.damageItem(1, entity);
        }
        return super.mineBlock(stack, block, x, y, z, entity);
    }

    public boolean canHarvestBlock(Block block) {
        return block.blockID == Block.web.blockID;
    }

    public float getStrVsBlock(ItemStack stack, Block block) {
        if (block.blockID == Block.web.blockID || block.blockID == Block.leaves.blockID) {
            return 15.0f;
        }
        if (block.blockID == Block.cloth.blockID) {
            return 5.0f;
        }
        return super.getStrVsBlock(stack, block);
    }
}

