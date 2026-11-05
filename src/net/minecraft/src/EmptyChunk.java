package net.minecraft.src;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EmptyChunk
extends Chunk {
    public EmptyChunk(World world, int chunkX, int chunkZ) {
        super(world, chunkX, chunkZ);
        this.neverSave = true;
    }

    public EmptyChunk(World world, byte[] byArray, int n, int n2) {
        super(world, byArray, n, n2);
        this.neverSave = true;
    }

    @Override
    public boolean isAtLocation(int chunkX, int chunkZ) {
        return chunkX == this.xPosition && chunkZ == this.zPosition;
    }

    @Override
    public int getHeightValue(int localX, int localZ) {
        return 0;
    }

    @Override
    public void populateLight() {
    }

    @Override
    public void populateHeightMap() {
    }

    @Override
    public void populateBlockLight() {
    }

    @Override
    public int getBlockID(int localX, int y, int localZ) {
        return 0;
    }

    @Override
    public boolean setBlockIDWithMetadata(int localX, int y, int localZ, int block, int metadata) {
        return true;
    }

    @Override
    public boolean setBlockID(int localX, int y, int localZ, int block) {
        return true;
    }

    @Override
    public int getBlockMetadata(int localX, int y, int localZ) {
        return 0;
    }

    @Override
    public void setBlockMetadata(int localX, int y, int localZ, int metadata) {
    }

    @Override
    public int getSavedLightValue(EnumSkyBlock type, int localX, int y, int localZ) {
        return 0;
    }

    @Override
    public void setLightValue(EnumSkyBlock type, int localX, int y, int localZ, int light) {
    }

    @Override
    public int getBlockLightValue(int localX, int y, int localZ, int ambientDarkness) {
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
    }

    @Override
    public void removeEntity(Entity entity) {
    }

    @Override
    public void removeEntityAtIndex(Entity entity, int chunkY) {
    }

    @Override
    public boolean canBlockSeeTheSky(int localX, int y, int localZ) {
        return false;
    }

    @Override
    public TileEntity getChunkBlockTileEntity(int localX, int y, int localZ) {
        return null;
    }

    @Override
    public void addTileEntity(TileEntity blockEntity) {
    }

    @Override
    public void setChunkBlockTileEntity(int localX, int y, int localZ, TileEntity blockEntity) {
    }

    @Override
    public void removeChunkBlockTileEntity(int localX, int y, int localZ) {
    }

    @Override
    public void onChunkLoad() {
    }

    @Override
    public void onChunkUnload() {
    }

    @Override
    public void setChunkModified() {
    }

    @Override
    public void getEntitiesWithinAABBForEntity(Entity exclude, AxisAlignedBB bounds, List<Entity> entities) {
    }

    @Override
    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> type, AxisAlignedBB bounds, List<T> entities) {
    }

    @Override
    public boolean needsSaving(boolean saveEntities) {
        return false;
    }

    @Override
    public int getChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int size) {
        int n = maxX - minX;
        int n2 = maxY - minY;
        int n3 = maxZ - minZ;
        int n4 = n * n2 * n3;
        int n5 = n4 + n4 / 2 * 3;
        Arrays.fill(data, size, size + n5, (byte)0);
        return n5;
    }

    @Override
    public Random getRandomForSlime(long seed) {
        return new Random(this.worldObj.getRandomSeed() + (long)(this.xPosition * this.xPosition * 4987142) + (long)(this.xPosition * 5947611) + (long)(this.zPosition * this.zPosition) * 4392871L + (long)(this.zPosition * 389711) ^ seed);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}

