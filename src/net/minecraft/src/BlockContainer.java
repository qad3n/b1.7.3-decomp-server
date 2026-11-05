package net.minecraft.src;


public abstract class BlockContainer
extends Block {
    protected BlockContainer(int n, Material material) {
        super(n, material);
        BlockContainer.isBlockContainer[n] = true;
    }

    protected BlockContainer(int n, int n2, Material material) {
        super(n, n2, material);
        BlockContainer.isBlockContainer[n] = true;
    }

    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.setBlockTileEntity(x, y, z, this.getBlockEntity());
    }

    public void onBlockRemoval(World world, int x, int y, int z) {
        super.onBlockRemoval(world, x, y, z);
        world.removeBlockTileEntity(x, y, z);
    }

    protected abstract TileEntity getBlockEntity();
}

