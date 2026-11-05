package net.minecraft.src;


public interface IBlockAccess {
    public int getBlockId(int var1, int var2, int var3);

    public TileEntity getBlockTileEntity(int var1, int var2, int var3);

    public int getBlockMetadata(int var1, int var2, int var3);

    public Material getBlockMaterial(int var1, int var2, int var3);

    public boolean isBlockNormalCube(int var1, int var2, int var3);
}

