package net.minecraft.src;


public class ItemSpade
extends ItemTool {
    private static Block[] blocksEffectiveAgainst = new Block[]{Block.grass, Block.dirt, Block.sand, Block.gravel, Block.snow, Block.blockSnow, Block.blockClay, Block.tilledField};

    public ItemSpade(int id, Item.EnumToolMaterial tier) {
        super(id, 1, tier, blocksEffectiveAgainst);
    }

    public boolean canHarvestBlock(Block block) {
        if (block == Block.snow) {
            return true;
        }
        return block == Block.blockSnow;
    }
}

