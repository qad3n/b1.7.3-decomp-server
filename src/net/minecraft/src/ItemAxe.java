package net.minecraft.src;


public class ItemAxe
extends ItemTool {
    private static Block[] blocksEffectiveAgainst = new Block[]{Block.planks, Block.bookShelf, Block.wood, Block.chest};

    protected ItemAxe(int id, Item.EnumToolMaterial tier) {
        super(id, 3, tier, blocksEffectiveAgainst);
    }
}

