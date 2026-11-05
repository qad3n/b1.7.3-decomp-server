package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

public class Chunk {
    public static boolean isLit;
    public byte[] blocks;
    public boolean isChunkLoaded;
    public World worldObj;
    public NibbleArray data;
    public NibbleArray skylightMap;
    public NibbleArray blocklightMap;
    public byte[] heightMap;
    public int lowestHeight;
    public final int xPosition;
    public final int zPosition;
    public Map<ChunkPosition, TileEntity> chunkTileEntityMap = new HashMap<>();
    public List<Entity>[] entities = new List[8];
    public boolean isTerrainPopulated = false;
    public boolean isModified = false;
    public boolean neverSave;
    public boolean hasEntities = false;
    public long lastSaveTime = 0L;

    public Chunk(World world, int chunkX, int chunkZ) {
        this.worldObj = world;
        this.xPosition = chunkX;
        this.zPosition = chunkZ;
        this.heightMap = new byte[256];

        for (int i = 0; i < this.entities.length; i++) {
            this.entities[i] = new ArrayList<>();
        }
    }

    public Chunk(World world, byte[] blocks, int chunkX, int chunkZ) {
        this(world, chunkX, chunkZ);
        this.blocks = blocks;
        this.data = new NibbleArray(blocks.length);
        this.skylightMap = new NibbleArray(blocks.length);
        this.blocklightMap = new NibbleArray(blocks.length);
    }

    public boolean isAtLocation(int chunkX, int chunkZ) {
        return chunkX == this.xPosition && chunkZ == this.zPosition;
    }

    public int getHeightValue(int localX, int localZ) {
        return this.heightMap[localZ << 4 | localX] & 0xFF;
    }

    public void populateLight() {
    }

    public void populateHeightMap() {
        int lowestHeight = 127;
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                int n;
                int n2;
                for (n = 127, n2 = (i << 11 | j << 7); n > 0 && Block.lightOpacity[this.blocks[n2 + n - 1] & 0xFF] == 0; --n) {
                }
                this.heightMap[j << 4 | i] = (byte) n;
                if (n < lowestHeight) {
                    lowestHeight = n;
                }
                if (!this.worldObj.worldProvider.noSky) {
                    int value = 15;
                    int y = 127;
                    do {
                        value -= Block.lightOpacity[this.blocks[n2 + y] & 0xFF];
                        if (value > 0) {
                            this.skylightMap.setNibble(i, y, j, value);
                        }
                    } while (--y > 0 && value > 0);
                }
            }
        }
        this.lowestHeight = lowestHeight;
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                this.lightGaps(k, l);
            }
        }
        this.isModified = true;
    }

    public void populateBlockLight() {
    }

    private void lightGaps(int localX, int localZ) {
        int i = this.getHeightValue(localX, localZ);
        int j = this.xPosition * 16 + localX;
        int k = this.zPosition * 16 + localZ;
        this.lightGap(j - 1, k, i);
        this.lightGap(j + 1, k, i);
        this.lightGap(j, k - 1, i);
        this.lightGap(j, k + 1, i);
    }

    private void lightGap(int x, int z, int y) {
        int i = this.worldObj.getHeightValue(x, z);
        if (i > y) {
            this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, x, y, z, x, i, z);
            this.isModified = true;
        } else if (i < y) {
            this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, x, i, z, x, y, z);
            this.isModified = true;
        }
    }

    private void updateHeightMap(int localX, int y, int localZ) {
        int i = this.heightMap[localZ << 4 | localX] & 255;
        int j = i;
        if (y > i) {
            j = y;
        }

        int k = localX << 11 | localZ << 7;

        while (j > 0 && Block.lightOpacity[this.blocks[k + j - 1] & 0xFF] == 0) {
            j--;
        }

        if (j != i) {
            this.worldObj.markBlocksDirtyVertical(localX, localZ, j, i);
            this.heightMap[localZ << 4 | localX] = (byte)j;
            if (j < this.lowestHeight) {
                this.lowestHeight = j;
            } else {
                int l = 127;

                for (int i1 = 0; i1 < 16; i1++) {
                    for (int j1 = 0; j1 < 16; j1++) {
                        if ((this.heightMap[j1 << 4 | i1] & 255) < l) {
                            l = this.heightMap[j1 << 4 | i1] & 255;
                        }
                    }
                }

                this.lowestHeight = l;
            }

            int i2 = this.xPosition * 16 + localX;
            int j2 = this.zPosition * 16 + localZ;
            if (j < i) {
                for (int k2 = j; k2 < i; k2++) {
                    this.skylightMap.setNibble(localX, k2, localZ, 15);
                }
            } else {
                this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, i2, i, j2, i2, j, j2);

                for (int l2 = i; l2 < j; l2++) {
                    this.skylightMap.setNibble(localX, l2, localZ, 0);
                }
            }

            int i3 = 15;
            int k1 = j;

            while (j > 0 && i3 > 0) {
                int l1 = Block.lightOpacity[this.getBlockID(localX, --j, localZ)];
                if (l1 == 0) {
                    l1 = 1;
                }

                i3 -= l1;
                if (i3 < 0) {
                    i3 = 0;
                }

                this.skylightMap.setNibble(localX, j, localZ, i3);
            }

            while (j > 0 && Block.lightOpacity[this.getBlockID(localX, j - 1, localZ)] == 0) {
                j--;
            }

            if (j != k1) {
                this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, i2 - 1, j, j2 - 1, i2 + 1, k1, j2 + 1);
            }

            this.isModified = true;
        }
    }

    public int getBlockID(int localX, int y, int localZ) {
        return this.blocks[localX << 11 | localZ << 7 | y] & 0xFF;
    }

    public boolean setBlockIDWithMetadata(int localX, int y, int localZ, int block, int metadata) {
        byte b0 = (byte)block;
        int i = this.heightMap[localZ << 4 | localX] & 255;
        int j = this.blocks[localX << 11 | localZ << 7 | y] & 255;
        if (j == block && this.data.getNibble(localX, y, localZ) == metadata) {
            return false;
        }

        int k = this.xPosition * 16 + localX;
        int l = this.zPosition * 16 + localZ;
        this.blocks[localX << 11 | localZ << 7 | y] = (byte)(b0 & 0xFF);
        if (j != 0 && !this.worldObj.singleplayerWorld) {
            Block.blocksList[j].onBlockRemoval(this.worldObj, k, y, l);
        }

        this.data.setNibble(localX, y, localZ, metadata);
        if (!this.worldObj.worldProvider.noSky) {
            if (Block.lightOpacity[b0 & 0xFF] != 0) {
                if (y >= i) {
                    this.updateHeightMap(localX, y + 1, localZ);
                }
            } else if (y == i - 1) {
                this.updateHeightMap(localX, y, localZ);
            }

            this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, k, y, l, k, y, l);
        }

        this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, k, y, l, k, y, l);
        this.lightGaps(localX, localZ);
        this.data.setNibble(localX, y, localZ, metadata);
        if (block != 0) {
            Block.blocksList[block].onBlockAdded(this.worldObj, k, y, l);
        }

        this.isModified = true;
        return true;
    }

    public boolean setBlockID(int localX, int y, int localZ, int block) {
        byte b0 = (byte)block;
        int i = this.heightMap[localZ << 4 | localX] & 255;
        int j = this.blocks[localX << 11 | localZ << 7 | y] & 255;
        if (j == block) {
            return false;
        }

        int k = this.xPosition * 16 + localX;
        int l = this.zPosition * 16 + localZ;
        this.blocks[localX << 11 | localZ << 7 | y] = (byte)(b0 & 0xFF);
        if (j != 0) {
            Block.blocksList[j].onBlockRemoval(this.worldObj, k, y, l);
        }

        this.data.setNibble(localX, y, localZ, 0);
        if (Block.lightOpacity[b0 & 0xFF] != 0) {
            if (y >= i) {
                this.updateHeightMap(localX, y + 1, localZ);
            }
        } else if (y == i - 1) {
            this.updateHeightMap(localX, y, localZ);
        }

        this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Sky, k, y, l, k, y, l);
        this.worldObj.scheduleLightingUpdate(EnumSkyBlock.Block, k, y, l, k, y, l);
        this.lightGaps(localX, localZ);
        if (block != 0 && !this.worldObj.singleplayerWorld) {
            Block.blocksList[block].onBlockAdded(this.worldObj, k, y, l);
        }

        this.isModified = true;
        return true;
    }

    public int getBlockMetadata(int localX, int y, int localZ) {
        return this.data.getNibble(localX, y, localZ);
    }

    public void setBlockMetadata(int localX, int y, int localZ, int metadata) {
        this.isModified = true;
        this.data.setNibble(localX, y, localZ, metadata);
    }

    public int getSavedLightValue(final EnumSkyBlock type, final int localX, final int y, final int localZ) {
        if (type == EnumSkyBlock.Sky) {
            return this.skylightMap.getNibble(localX, y, localZ);
        }
        if (type == EnumSkyBlock.Block) {
            return this.blocklightMap.getNibble(localX, y, localZ);
        }
        return 0;
    }

    public void setLightValue(EnumSkyBlock type, int localX, int y, int localZ, int light) {
        this.isModified = true;
        if (type == EnumSkyBlock.Sky) {
            this.skylightMap.setNibble(localX, y, localZ, light);
        } else {
            if (type != EnumSkyBlock.Block) {
                return;
            }

            this.blocklightMap.setNibble(localX, y, localZ, light);
        }
    }

    public int getBlockLightValue(int localX, int y, int localZ, int ambientDarkness) {
        int i = this.skylightMap.getNibble(localX, y, localZ);
        if (i > 0) {
            isLit = true;
        }

        i -= ambientDarkness;
        int j = this.blocklightMap.getNibble(localX, y, localZ);
        if (j > i) {
            i = j;
        }

        return i;
    }

    public void addEntity(Entity entity) {
        this.hasEntities = true;
        int i = MathHelper.floor_double(entity.posX / 16.0);
        int j = MathHelper.floor_double(entity.posZ / 16.0);
        if (i != this.xPosition || j != this.zPosition) {
            System.out.println("Wrong location! " + entity);
            Thread.dumpStack();
        }

        int k = MathHelper.floor_double(entity.posY / 16.0);
        if (k < 0) {
            k = 0;
        }

        if (k >= this.entities.length) {
            k = this.entities.length - 1;
        }

        entity.addedToChunk = true;
        entity.chunkCoordX = this.xPosition;
        entity.chunkCoordY = k;
        entity.chunkCoordZ = this.zPosition;
        this.entities[k].add(entity);
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAtIndex(entity, entity.chunkCoordY);
    }

    public void removeEntityAtIndex(Entity entity, int chunkY) {
        if (chunkY < 0) {
            chunkY = 0;
        }

        if (chunkY >= this.entities.length) {
            chunkY = this.entities.length - 1;
        }

        this.entities[chunkY].remove(entity);
    }

    public boolean canBlockSeeTheSky(int localX, int y, int localZ) {
        return y >= (this.heightMap[localZ << 4 | localX] & 255);
    }

    public TileEntity getChunkBlockTileEntity(int localX, int y, int localZ) {
        ChunkPosition chunkposition = new ChunkPosition(localX, y, localZ);
        TileEntity tileentity = this.chunkTileEntityMap.get(chunkposition);
        if (tileentity == null) {
            int i = this.getBlockID(localX, y, localZ);
            if (!Block.isBlockContainer[i]) {
                return null;
            }

            BlockContainer blockcontainer = (BlockContainer)Block.blocksList[i];
            blockcontainer.onBlockAdded(this.worldObj, this.xPosition * 16 + localX, y, this.zPosition * 16 + localZ);
            tileentity = this.chunkTileEntityMap.get(chunkposition);
        }

        if (tileentity != null && tileentity.isInvalid()) {
            this.chunkTileEntityMap.remove(chunkposition);
            return null;
        } else {
            return tileentity;
        }
    }

    public void addTileEntity(TileEntity blockEntity) {
        int i = blockEntity.xCoord - this.xPosition * 16;
        int j = blockEntity.yCoord;
        int k = blockEntity.zCoord - this.zPosition * 16;
        this.setChunkBlockTileEntity(i, j, k, blockEntity);
        if (this.isChunkLoaded) {
            this.worldObj.loadedTileEntityList.add(blockEntity);
        }
    }

    public void setChunkBlockTileEntity(final int localX, final int y, final int localZ, final TileEntity blockEntity) {
        final ChunkPosition chunkPosition = new ChunkPosition(localX, y, localZ);
        blockEntity.worldObj = this.worldObj;
        blockEntity.xCoord = this.xPosition * 16 + localX;
        blockEntity.yCoord = y;
        blockEntity.zCoord = this.zPosition * 16 + localZ;
        if (this.getBlockID(localX, y, localZ) == 0 || !(Block.blocksList[this.getBlockID(localX, y, localZ)] instanceof BlockContainer)) {
            System.out.println("Attempted to place a tile entity where there was no entity tile!");
            return;
        }
        blockEntity.validate();
        this.chunkTileEntityMap.put(chunkPosition, blockEntity);
    }

    public void removeChunkBlockTileEntity(int localX, int y, int localZ) {
        ChunkPosition chunkposition = new ChunkPosition(localX, y, localZ);
        if (this.isChunkLoaded) {
            TileEntity tileentity = this.chunkTileEntityMap.remove(chunkposition);
            if (tileentity != null) {
                tileentity.invalidate();
            }
        }
    }

    public void onChunkLoad() {
        this.isChunkLoaded = true;
        this.worldObj.loadBlockEntities(this.chunkTileEntityMap.values());

        for (int i = 0; i < this.entities.length; i++) {
            this.worldObj.addLoadedEntities(this.entities[i]);
        }
    }

    public void onChunkUnload() {
        this.isChunkLoaded = false;

        for (TileEntity tileentity : this.chunkTileEntityMap.values()) {
            tileentity.invalidate();
        }

        for (int i = 0; i < this.entities.length; i++) {
            this.worldObj.addUnloadedEntities(this.entities[i]);
        }
    }

    public void setChunkModified() {
        this.isModified = true;
    }

    public void getEntitiesWithinAABBForEntity(Entity exclude, AxisAlignedBB bounds, List<Entity> entities) {
        int i = MathHelper.floor_double((bounds.minY - 2.0) / 16.0);
        int j = MathHelper.floor_double((bounds.maxY + 2.0) / 16.0);
        if (i < 0) {
            i = 0;
        }

        if (j >= this.entities.length) {
            j = this.entities.length - 1;
        }

        for (int k = i; k <= j; k++) {
            List list = this.entities[k];

            for (int l = 0; l < list.size(); l++) {
                Entity entity = (Entity)list.get(l);
                if (entity != exclude && entity.boundingBox.intersectsWith(bounds)) {
                    entities.add(entity);
                }
            }
        }
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAAAB(Class<? extends T> type, AxisAlignedBB bounds, List<T> entities) {
        int i = MathHelper.floor_double((bounds.minY - 2.0) / 16.0);
        int j = MathHelper.floor_double((bounds.maxY + 2.0) / 16.0);
        if (i < 0) {
            i = 0;
        }

        if (j >= this.entities.length) {
            j = this.entities.length - 1;
        }

        for (int k = i; k <= j; k++) {
            List list = this.entities[k];

            for (int l = 0; l < list.size(); l++) {
                Entity entity = (Entity)list.get(l);
                if (type.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(bounds)) {
                    entities.add((T)entity);
                }
            }
        }
    }

    public boolean needsSaving(boolean saveEntities) {
        if (this.neverSave) {
            return false;
        }

        if (saveEntities) {
            if (this.hasEntities && this.worldObj.getWorldTime() != this.lastSaveTime) {
                return true;
            }
        } else if (this.hasEntities && this.worldObj.getWorldTime() >= this.lastSaveTime + 600L) {
            return true;
        }

        return this.isModified;
    }

    public int getChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int size) {
        int i = maxX - minX;
        int j = maxY - minY;
        int k = maxZ - minZ;
        if (i * j * k == this.blocks.length) {
            System.arraycopy(this.blocks, 0, data, size, this.blocks.length);
            size += this.blocks.length;
            System.arraycopy(this.data.data, 0, data, size, this.data.data.length);
            size += this.data.data.length;
            System.arraycopy(this.blocklightMap.data, 0, data, size, this.blocklightMap.data.length);
            size += this.blocklightMap.data.length;
            System.arraycopy(this.skylightMap.data, 0, data, size, this.skylightMap.data.length);
            return size + this.skylightMap.data.length;
        }

        for (int l = minX; l < maxX; l++) {
            for (int i1 = minZ; i1 < maxZ; i1++) {
                int j1 = l << 11 | i1 << 7 | minY;
                int k1 = maxY - minY;
                System.arraycopy(this.blocks, j1, data, size, k1);
                size += k1;
            }
        }

        for (int l1 = minX; l1 < maxX; l1++) {
            for (int k2 = minZ; k2 < maxZ; k2++) {
                int j3 = (l1 << 11 | k2 << 7 | minY) >> 1;
                int i4 = (maxY - minY) / 2;
                System.arraycopy(this.data.data, j3, data, size, i4);
                size += i4;
            }
        }

        for (int i2 = minX; i2 < maxX; i2++) {
            for (int l2 = minZ; l2 < maxZ; l2++) {
                int k3 = (i2 << 11 | l2 << 7 | minY) >> 1;
                int j4 = (maxY - minY) / 2;
                System.arraycopy(this.blocklightMap.data, k3, data, size, j4);
                size += j4;
            }
        }

        for (int j2 = minX; j2 < maxX; j2++) {
            for (int i3 = minZ; i3 < maxZ; i3++) {
                int l3 = (j2 << 11 | i3 << 7 | minY) >> 1;
                int k4 = (maxY - minY) / 2;
                System.arraycopy(this.skylightMap.data, l3, data, size, k4);
                size += k4;
            }
        }

        return size;
    }

    public Random getRandomForSlime(long seed) {
        return new Random(
            this.worldObj.getRandomSeed()
                    + this.xPosition * this.xPosition * 4987142
                    + this.xPosition * 5947611
                    + this.zPosition * this.zPosition * 4392871L
                    + this.zPosition * 389711
                ^ seed
        );
    }

    public boolean isEmpty() {
        return false;
    }

    public void fill() {
        ChunkBlockMap.fill(this.blocks);
    }
}
