package net.minecraft.src;


public class ItemPickaxe
extends ItemTool {
    private static Block[] blocksEffectiveAgainst = new Block[]{Block.cobblestone, Block.stairDouble, Block.stairSingle, Block.stone, Block.sandStone, Block.cobblestoneMossy, Block.oreIron, Block.blockSteel, Block.oreCoal, Block.blockGold, Block.oreGold, Block.oreDiamond, Block.blockDiamond, Block.ice, Block.bloodStone, Block.oreLapis, Block.blockLapis};

    protected ItemPickaxe(int id, Item.EnumToolMaterial tier) {
        super(id, 2, tier, blocksEffectiveAgainst);
    }

    public boolean canHarvestBlock(Block block) {
        if (block == Block.obsidian) {
            return this.toolMaterial.getHarvestLevel() == 3;
        }
        if (block == Block.blockDiamond || block == Block.oreDiamond) {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
        if (block == Block.blockGold || block == Block.oreGold) {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
        if (block == Block.blockSteel || block == Block.oreIron) {
            return this.toolMaterial.getHarvestLevel() >= 1;
        }
        if (block == Block.blockLapis || block == Block.oreLapis) {
            return this.toolMaterial.getHarvestLevel() >= 1;
        }
        if (block == Block.oreRedstone || block == Block.oreRedstoneGlowing) {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
        if (block.blockMaterial == Material.rock) {
            return true;
        }
        return block.blockMaterial == Material.iron;
    }
}

