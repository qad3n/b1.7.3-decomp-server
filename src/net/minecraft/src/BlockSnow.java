package net.minecraft.src;

import java.util.Random;

public class BlockSnow
extends Block {
    protected BlockSnow(int id, int sprite) {
        super(id, sprite, Material.snow);
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
        this.setTickOnLoad(true);
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z) & 7;
        if (n >= 3) {
            return AxisAlignedBB.getBoundingBoxFromPool((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (float)y + 0.5f, (double)z + this.maxZ);
        }
        return null;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int n = world.getBlockMetadata(x, y, z) & 7;
        float f = (float)(2 * (1 + n)) / 16.0f;
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, f, 1.0f);
    }

    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int n = world.getBlockId(x, y - 1, z);
        if (n == 0 || !Block.blocksList[n].isOpaqueCube()) {
            return false;
        }
        return world.getBlockMaterial(x, y - 1, z).getIsSolid();
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlock) {
        this.canBePlacedOrBreak(world, x, y, z);
    }

    private boolean canBePlacedOrBreak(World world, int x, int y, int z) {
        if (!this.canPlaceBlockAt(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            return false;
        }
        return true;
    }

    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        int n = Item.snowball.shiftedIndex;
        float f = 0.7f;
        double d = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        double d3 = (double)(world.rand.nextFloat() * f) + (double)(1.0f - f) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double)x + d, (double)y + d2, (double)z + d3, new ItemStack(n, 1, 0));
        entityItem.delayBeforeCanPickup = 10;
        world.entityJoinedWorld(entityItem);
        world.setBlockWithNotify(x, y, z, 0);
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
    }

    public int idDropped(int metadata, Random random) {
        return Item.snowball.shiftedIndex;
    }

    public int quantityDropped(Random random) {
        return 0;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        if (world.getSavedLightValue(EnumSkyBlock.Block, x, y, z) > 11) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
        }
    }
}

