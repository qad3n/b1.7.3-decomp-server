package net.minecraft.src;

import java.util.Random;

public class BlockIce
extends BlockBreakable {
    public BlockIce(int id, int sprite) {
        super(id, sprite, Material.ice, false);
        this.slipperiness = 0.98f;
        this.setTickOnLoad(true);
    }

    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.harvestBlock(world, player, x, y, z, metadata);
        Material material = world.getBlockMaterial(x, y - 1, z);
        if (material.getIsSolid() || material.getIsLiquid()) {
            world.setBlockWithNotify(x, y, z, Block.waterMoving.blockID);
        }
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11 - Block.lightOpacity[this.blockID]) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, Block.waterStill.blockID);
        }
    }

    public int getMobilityFlag() {
        return 0;
    }
}

