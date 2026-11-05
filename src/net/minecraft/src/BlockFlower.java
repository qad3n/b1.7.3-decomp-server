package net.minecraft.src;

import java.util.Random;

public class BlockFlower
extends Block {
    protected BlockFlower(int id, int sprite) {
        super(id, Material.plants);
        this.blockIndexInTexture = sprite;
        this.setTickOnLoad(true);
        float f = 0.2f;
        this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f * 3.0f, 0.5f + f);
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }

    protected boolean canThisPlantGrowOnThisBlockID(int block) {
        return block == Block.grass.blockID || block == Block.dirt.blockID || block == Block.tilledField.blockID;
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        this.canSurviveOrBreak(world, x, y, z);
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        this.canSurviveOrBreak(world, x, y, z);
    }

    protected final void canSurviveOrBreak(World world, int x, int y, int z) {
        if (!this.canBlockStay(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    public boolean canBlockStay(World world, int x, int y, int z) {
        return (world.getBlockLightValueNoChecks(x, y, z) >= 8 || world.canBlockSeeTheSky(x, y, z)) && this.canThisPlantGrowOnThisBlockID(world.getBlockId(x, y - 1, z));
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }
}

