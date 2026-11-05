package net.minecraft.src;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class World implements IBlockAccess {
    public boolean scheduledUpdatesAreImmediate = false;
    private List<MetadataChunkBlock> lightUpdates = new ArrayList<>();
    public List<Entity> loadedEntityList = new ArrayList<>();
    private List<Entity> unloadedEntityList = new ArrayList<>();
    private TreeSet<NextTickListEntry> scheduledTickTreeSet = new TreeSet<>();
    private Set<NextTickListEntry> scheduledTickSet = new HashSet<>();
    public List<TileEntity> loadedTileEntityList = new ArrayList<>();
    private List<TileEntity> pendingBlockEntities = new ArrayList<>();
    public List<EntityPlayer> playerEntities = new ArrayList<>();
    public List<Entity> lightningEntities = new ArrayList<>();
    private long cloudColor = 16777215L;
    public int skylightSubtracted = 0;
    protected int distHashCounter = new Random().nextInt();
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRain;
    protected float rain;
    protected float prevThunder;
    protected float thunder;
    protected int thunderCooldown = 0;
    public int lightningCooldown = 0;
    public boolean editingBlocks = false;
    private long lockTimestamp = System.currentTimeMillis();
    protected int autosavePeriod = 40;
    public int difficultySetting;
    public Random rand = new Random();
    public boolean isNewWorld = false;
    public final WorldProvider worldProvider;
    protected List<IWorldAccess> worldAccesses = new ArrayList<>();
    protected IChunkProvider chunkProvider;
    protected final ISaveHandler worldFile;
    protected WorldInfo worldInfo;
    public boolean worldChunkLoadOverride;
    private boolean allPlayersSleeping;
    public MapStorage savedDataStorage;
    private ArrayList<AxisAlignedBB> collisions = new ArrayList<>();
    private boolean isTickingBlockEntities;
    private int doLightUpdatesDepth = 0;
    private boolean spawnHostileMobs = true;
    private boolean spawnPeacefulMobs = true;
    static int updateLightDepth = 0;
    private Set<ChunkCoordIntPair> activeChunkSet = new HashSet<>();
    private int ambientTickCountdown = this.rand.nextInt(12000);
    private List<Entity> entitiesInArea = new ArrayList<>();
    public boolean singleplayerWorld = false;

    public WorldChunkManager getWorldChunkManager() {
        return this.worldProvider.worldChunkMgr;
    }

    public World(ISaveHandler storage, String name, long seed, WorldProvider dimension) {
        this.worldFile = storage;
        this.savedDataStorage = new MapStorage(storage);
        this.worldInfo = storage.loadData();
        this.isNewWorld = this.worldInfo == null;
        if (dimension != null) {
            this.worldProvider = dimension;
        } else if (this.worldInfo != null && this.worldInfo.getDimension() == -1) {
            this.worldProvider = WorldProvider.fromId(-1);
        } else {
            this.worldProvider = WorldProvider.fromId(0);
        }

        boolean flag = false;
        if (this.worldInfo == null) {
            this.worldInfo = new WorldInfo(seed, name);
            flag = true;
        } else {
            this.worldInfo.setLevelName(name);
        }

        this.worldProvider.registerWorld(this);
        this.chunkProvider = this.getChunkProvider();
        if (flag) {
            this.generateSpawnPoint();
        }

        this.calculateInitialSkylight();
        this.initWeather();
    }

    protected IChunkProvider getChunkProvider() {
        IChunkLoader ichunkloader = this.worldFile.getChunkStorage(this.worldProvider);
        return new ChunkProvider(this, ichunkloader, this.worldProvider.getChunkProvider());
    }

    protected void generateSpawnPoint() {
        this.worldChunkLoadOverride = true;
        int i = 0;
        byte b0 = 64;

        int j;
        for (j = 0; !this.worldProvider.canCoordinateBeSpawn(i, j); j += this.rand.nextInt(64) - this.rand.nextInt(64)) {
            i += this.rand.nextInt(64) - this.rand.nextInt(64);
        }

        this.worldInfo.setSpawnPosition(i, b0, j);
        this.worldChunkLoadOverride = false;
    }

    public int getFirstUncoveredBlock(int x, int z) {
        int i = 63;

        while (!this.isAirBlock(x, i + 1, z)) {
            i++;
        }

        return this.getBlockId(x, i, z);
    }

    public void saveWorld(final boolean saveEntities, final IProgressUpdate progressListener) {
        if (!this.chunkProvider.shouldSave()) {
            return;
        }
        if (progressListener != null) {
            progressListener.progressStartNoAbort("Saving level");
        }
        this.saveLevel();
        if (progressListener != null) {
            progressListener.displayLoadingString("Saving chunks");
        }
        this.chunkProvider.saveChunks(saveEntities, progressListener);
    }

    private void saveLevel() {
        this.checkSessionLock();
        this.worldFile.saveData(this.worldInfo, this.playerEntities);
        this.savedDataStorage.save();
    }

    public int getBlockId(final int x, final int y, final int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 0;
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            return 0;
        }
        return this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockID(x & 0xF, y, z & 0xF);
    }

    public boolean isAirBlock(int x, int y, int z) {
        return this.getBlockId(x, y, z) == 0;
    }

    public boolean blockExists(int x, int y, int z) {
        return y >= 0 && y < 128 ? this.chunkExists(x >> 4, z >> 4) : false;
    }

    public boolean doChunksNearChunkExist(int x, int y, int z, int range) {
        return this.checkChunksExist(x - range, y - range, z - range, x + range, y + range, z + range);
    }

    public boolean checkChunksExist(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (maxY < 0 || minY >= 128) {
            return false;
        }
        minX >>= 4;
        minY >>= 4;
        minZ >>= 4;
        maxX >>= 4;
        maxY >>= 4;
        maxZ >>= 4;
        for (int i = minX; i <= maxX; ++i) {
            for (int j = minZ; j <= maxZ; ++j) {
                if (!this.chunkExists(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean chunkExists(int chunkX, int chunkZ) {
        return this.chunkProvider.chunkExists(chunkX, chunkZ);
    }

    public Chunk getChunkFromBlockCoords(int x, int z) {
        return this.getChunkFromChunkCoords(x >> 4, z >> 4);
    }

    public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
        return this.chunkProvider.provideChunk(chunkX, chunkZ);
    }

    public boolean setBlockAndMetadata(int x, int y, int z, int block, int metadata) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (y >= 128) {
            return false;
        }

        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        return chunk.setBlockIDWithMetadata(x & 15, y, z & 15, block, metadata);
    }

    public boolean setBlock(int x, int y, int z, int block) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (y >= 128) {
            return false;
        }

        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        return chunk.setBlockID(x & 15, y, z & 15, block);
    }

    public Material getBlockMaterial(final int x, final int y, final int z) {
        final int blockId = this.getBlockId(x, y, z);
        if (blockId == 0) {
            return Material.air;
        }
        return Block.blocksList[blockId].blockMaterial;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 0;
        }

        if (y < 0) {
            return 0;
        }

        if (y >= 128) {
            return 0;
        }

        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        x &= 15;
        z &= 15;
        return chunk.getBlockMetadata(x, y, z);
    }

    public void setBlockMetadataWithNotify(int x, int y, int z, int metadata) {
        if (this.setBlockMetadata(x, y, z, metadata)) {
            int i = this.getBlockId(x, y, z);
            if (Block.requiresSelfNotify[i & 0xFF]) {
                this.notifyBlockChange(x, y, z, i);
            } else {
                this.notifyBlocksOfNeighborChange(x, y, z, i);
            }
        }
    }

    public boolean setBlockMetadata(int x, int y, int z, int metadata) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (y >= 128) {
            return false;
        }

        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        x &= 15;
        z &= 15;
        chunk.setBlockMetadata(x, y, z, metadata);
        return true;
    }

    public boolean setBlockWithNotify(int x, int y, int z, int block) {
        if (this.setBlock(x, y, z, block)) {
            this.notifyBlockChange(x, y, z, block);
            return true;
        } else {
            return false;
        }
    }

    public boolean setBlockAndMetadataWithNotify(int x, int y, int z, int block, int metadata) {
        if (this.setBlockAndMetadata(x, y, z, block, metadata)) {
            this.notifyBlockChange(x, y, z, block);
            return true;
        } else {
            return false;
        }
    }

    public void markBlockNeedsUpdate(int x, int y, int z) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).markBlockNeedsUpdate(x, y, z);
        }
    }

    protected void notifyBlockChange(int x, int y, int z, int block) {
        this.markBlockNeedsUpdate(x, y, z);
        this.notifyBlocksOfNeighborChange(x, y, z, block);
    }

    public void markBlocksDirtyVertical(int x, int z, int newHeight, int oldHeight) {
        if (newHeight > oldHeight) {
            int i = oldHeight;
            oldHeight = newHeight;
            newHeight = i;
        }

        this.markBlocksDirty(x, newHeight, z, x, oldHeight, z);
    }

    public void markBlockAsNeedsUpdate(int x, int y, int z) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).markBlockRangeNeedsUpdate(x, y, z, x, y, z);
        }
    }

    public void markBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).markBlockRangeNeedsUpdate(minX, minY, minZ, maxX, maxY, maxZ);
        }
    }

    public void notifyBlocksOfNeighborChange(int x, int y, int z, int block) {
        this.notifyBlockOfNeighborChange(x - 1, y, z, block);
        this.notifyBlockOfNeighborChange(x + 1, y, z, block);
        this.notifyBlockOfNeighborChange(x, y - 1, z, block);
        this.notifyBlockOfNeighborChange(x, y + 1, z, block);
        this.notifyBlockOfNeighborChange(x, y, z - 1, block);
        this.notifyBlockOfNeighborChange(x, y, z + 1, block);
    }

    private void notifyBlockOfNeighborChange(final int x, final int y, final int z, final int neighborBlock) {
        if (this.editingBlocks || this.singleplayerWorld) {
            return;
        }
        final Block block = Block.blocksList[this.getBlockId(x, y, z)];
        if (block != null) {
            block.onNeighborBlockChange(this, x, y, z, neighborBlock);
        }
    }

    public boolean canBlockSeeTheSky(int x, int y, int z) {
        return this.getChunkFromChunkCoords(x >> 4, z >> 4).canBlockSeeTheSky(x & 15, y, z & 15);
    }

    public int getBlockLightValueNoChecks(int x, int y, int z) {
        if (y < 0) {
            return 0;
        }

        if (y >= 128) {
            y = 127;
        }

        return this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockLightValue(x & 15, y, z & 15, 0);
    }

    public int getBlockLightValue(int x, int y, int z) {
        return this.getBlockLightValue_do(x, y, z, true);
    }

    public int getBlockLightValue_do(int x, int y, int z, final boolean useNeighborLight) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return 15;
        }
        if (useNeighborLight) {
            final int blockId = this.getBlockId(x, y, z);
            if (blockId == Block.stairSingle.blockID || blockId == Block.tilledField.blockID || blockId == Block.stairCompactCobblestone.blockID || blockId == Block.stairCompactPlanks.blockID) {
                int blockLightValue_do = this.getBlockLightValue_do(x, y + 1, z, false);
                final int blockLightValue_do2 = this.getBlockLightValue_do(x + 1, y, z, false);
                final int blockLightValue_do3 = this.getBlockLightValue_do(x - 1, y, z, false);
                final int blockLightValue_do4 = this.getBlockLightValue_do(x, y, z + 1, false);
                final int blockLightValue_do5 = this.getBlockLightValue_do(x, y, z - 1, false);
                if (blockLightValue_do2 > blockLightValue_do) {
                    blockLightValue_do = blockLightValue_do2;
                }
                if (blockLightValue_do3 > blockLightValue_do) {
                    blockLightValue_do = blockLightValue_do3;
                }
                if (blockLightValue_do4 > blockLightValue_do) {
                    blockLightValue_do = blockLightValue_do4;
                }
                if (blockLightValue_do5 > blockLightValue_do) {
                    blockLightValue_do = blockLightValue_do5;
                }
                return blockLightValue_do;
            }
        }
        if (y < 0) {
            return 0;
        }
        if (y >= 128) {
            y = 127;
        }
        final Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        x &= 0xF;
        z &= 0xF;
        return chunkFromChunkCoords.getBlockLightValue(x, y, z, this.skylightSubtracted);
    }

    public boolean canExistingBlockSeeTheSky(int x, int y, int z) {
        if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (y >= 128) {
            return true;
        }

        if (!this.chunkExists(x >> 4, z >> 4)) {
            return false;
        }

        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        x &= 15;
        z &= 15;
        return chunk.canBlockSeeTheSky(x, y, z);
    }

    public int getHeightValue(int x, int z) {
        if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
            if (!this.chunkExists(x >> 4, z >> 4)) {
                return 0;
            }

            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            return chunk.getHeightValue(x & 15, z & 15);
        } else {
            return 0;
        }
    }

    public void neighborLightPropagationChanged(final EnumSkyBlock type, final int x, final int y, final int z, int light) {
        if (this.worldProvider.noSky && type == EnumSkyBlock.Sky) {
            return;
        }
        if (!this.blockExists(x, y, z)) {
            return;
        }
        if (type == EnumSkyBlock.Sky) {
            if (this.canExistingBlockSeeTheSky(x, y, z)) {
                light = 15;
            }
        } else if (type == EnumSkyBlock.Block) {
            final int blockId = this.getBlockId(x, y, z);
            if (Block.lightValue[blockId] > light) {
                light = Block.lightValue[blockId];
            }
        }
        if (this.getSavedLightValue(type, x, y, z) != light) {
            this.scheduleLightingUpdate(type, x, y, z, x, y, z);
        }
    }

    public int getSavedLightValue(EnumSkyBlock type, int x, int y, int z) {
        if (y < 0) {
            y = 0;
        }

        if (y >= 128) {
            y = 127;
        }

        if (y >= 0 && y < 128 && x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
            int i = x >> 4;
            int j = z >> 4;
            if (!this.chunkExists(i, j)) {
                return 0;
            }

            Chunk chunk = this.getChunkFromChunkCoords(i, j);
            return chunk.getSavedLightValue(type, x & 15, y, z & 15);
        } else {
            return type.defaultValue;
        }
    }

    public void setLightValue(EnumSkyBlock type, int x, int y, int z, int light) {
        if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
            if (y >= 0) {
                if (y < 128) {
                    if (this.chunkExists(x >> 4, z >> 4)) {
                        Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                        chunk.setLightValue(type, x & 15, y, z & 15, light);

                        for (int i = 0; i < this.worldAccesses.size(); i++) {
                            this.worldAccesses.get(i).markBlockNeedsUpdate(x, y, z);
                        }
                    }
                }
            }
        }
    }

    public float getLightBrightness(int x, int y, int z) {
        return this.worldProvider.lightBrightnessTable[this.getBlockLightValue(x, y, z)];
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    public MovingObjectPosition rayTraceBlocks(Vec3D from, Vec3D to) {
        return this.rayTrace(from, to, false, false);
    }

    public MovingObjectPosition rayTraceBlocks_do(Vec3D from, Vec3D to, boolean allowLiquids) {
        return this.rayTrace(from, to, allowLiquids, false);
    }

    public MovingObjectPosition rayTrace(Vec3D from, Vec3D to, boolean allowLiquids, boolean ignoreBlocksWithoutCollision) {
        if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord)) {
            return null;
        }

        if (!Double.isNaN(to.xCoord) && !Double.isNaN(to.yCoord) && !Double.isNaN(to.zCoord)) {
            int i = MathHelper.floor_double(to.xCoord);
            int j = MathHelper.floor_double(to.yCoord);
            int k = MathHelper.floor_double(to.zCoord);
            int l = MathHelper.floor_double(from.xCoord);
            int i1 = MathHelper.floor_double(from.yCoord);
            int j1 = MathHelper.floor_double(from.zCoord);
            int k1 = this.getBlockId(l, i1, j1);
            int l1 = this.getBlockMetadata(l, i1, j1);
            Block block = Block.blocksList[k1];
            if ((!ignoreBlocksWithoutCollision || block == null || block.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null)
                && k1 > 0
                && block.canCollideCheck(l1, allowLiquids)) {
                MovingObjectPosition movingobjectposition = block.collisionRayTrace(this, l, i1, j1, from, to);
                if (movingobjectposition != null) {
                    return movingobjectposition;
                }
            }

            k1 = 200;

            while (k1-- >= 0) {
                if (Double.isNaN(from.xCoord) || Double.isNaN(from.yCoord) || Double.isNaN(from.zCoord)) {
                    return null;
                }

                if (l == i && i1 == j && j1 == k) {
                    return null;
                }

                boolean flag = true;
                boolean flag1 = true;
                boolean flag2 = true;
                double d0 = 999.0;
                double d1 = 999.0;
                double d2 = 999.0;
                if (i > l) {
                    d0 = l + 1.0;
                } else if (i < l) {
                    d0 = l + 0.0;
                } else {
                    flag = false;
                }

                if (j > i1) {
                    d1 = i1 + 1.0;
                } else if (j < i1) {
                    d1 = i1 + 0.0;
                } else {
                    flag1 = false;
                }

                if (k > j1) {
                    d2 = j1 + 1.0;
                } else if (k < j1) {
                    d2 = j1 + 0.0;
                } else {
                    flag2 = false;
                }

                double d3 = 999.0;
                double d4 = 999.0;
                double d5 = 999.0;
                double d6 = to.xCoord - from.xCoord;
                double d7 = to.yCoord - from.yCoord;
                double d8 = to.zCoord - from.zCoord;
                if (flag) {
                    d3 = (d0 - from.xCoord) / d6;
                }

                if (flag1) {
                    d4 = (d1 - from.yCoord) / d7;
                }

                if (flag2) {
                    d5 = (d2 - from.zCoord) / d8;
                }

                byte b0 = 0;
                if (d3 < d4 && d3 < d5) {
                    if (i > l) {
                        b0 = 4;
                    } else {
                        b0 = 5;
                    }

                    from.xCoord = d0;
                    from.yCoord += d7 * d3;
                    from.zCoord += d8 * d3;
                } else if (d4 < d5) {
                    if (j > i1) {
                        b0 = 0;
                    } else {
                        b0 = 1;
                    }

                    from.xCoord += d6 * d4;
                    from.yCoord = d1;
                    from.zCoord += d8 * d4;
                } else {
                    if (k > j1) {
                        b0 = 2;
                    } else {
                        b0 = 3;
                    }

                    from.xCoord += d6 * d5;
                    from.yCoord += d7 * d5;
                    from.zCoord = d2;
                }

                Vec3D vec3d = Vec3D.createVector(from.xCoord, from.yCoord, from.zCoord);
                l = (int)(vec3d.xCoord = MathHelper.floor_double(from.xCoord));
                if (b0 == 5) {
                    l--;
                    vec3d.xCoord++;
                }

                i1 = (int)(vec3d.yCoord = MathHelper.floor_double(from.yCoord));
                if (b0 == 1) {
                    i1--;
                    vec3d.yCoord++;
                }

                j1 = (int)(vec3d.zCoord = MathHelper.floor_double(from.zCoord));
                if (b0 == 3) {
                    j1--;
                    vec3d.zCoord++;
                }

                int i2 = this.getBlockId(l, i1, j1);
                int j2 = this.getBlockMetadata(l, i1, j1);
                Block block1 = Block.blocksList[i2];
                if ((!ignoreBlocksWithoutCollision || block1 == null || block1.getCollisionBoundingBoxFromPool(this, l, i1, j1) != null)
                    && i2 > 0
                    && block1.canCollideCheck(j2, allowLiquids)) {
                    MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(this, l, i1, j1, from, to);
                    if (movingobjectposition1 != null) {
                        return movingobjectposition1;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void playSoundAtEntity(Entity source, String sound, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playSound(sound, source.posX, source.posY - source.yOffset, source.posZ, volume, pitch);
        }
    }

    public void playSoundEffect(double x, double y, double z, String sound, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playSound(sound, x, y, z, volume, pitch);
        }
    }

    public void playRecord(String record, int x, int y, int z) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).playRecord(record, x, y, z);
        }
    }

    public void spawnParticle(String type, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).spawnParticle(type, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    public boolean addLightningBolt(Entity entity) {
        this.lightningEntities.add(entity);
        return true;
    }

    public boolean entityJoinedWorld(Entity entity) {
        int i = MathHelper.floor_double(entity.posX / 16.0);
        int j = MathHelper.floor_double(entity.posZ / 16.0);
        boolean flag = false;
        if (entity instanceof EntityPlayer) {
            flag = true;
        }

        if (!flag && !this.chunkExists(i, j)) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entity;
            this.playerEntities.add(entityplayer);
            this.updateAllPlayersSleepingFlag();
        }

        this.getChunkFromChunkCoords(i, j).addEntity(entity);
        this.loadedEntityList.add(entity);
        this.obtainEntitySkin(entity);
        return true;
    }

    protected void obtainEntitySkin(Entity entity) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).obtainEntitySkin(entity);
        }
    }

    protected void releaseEntitySkin(Entity entity) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).releaseEntitySkin(entity);
        }
    }

    public void removePlayerForLogoff(Entity entity) {
        if (entity.riddenByEntity != null) {
            entity.riddenByEntity.mountEntity(null);
        }

        if (entity.ridingEntity != null) {
            entity.mountEntity(null);
        }

        entity.setEntityDead();
        if (entity instanceof EntityPlayer) {
            this.playerEntities.remove((EntityPlayer)entity);
            this.updateAllPlayersSleepingFlag();
        }
    }

    public void removePlayer(Entity entity) {
        entity.setEntityDead();
        if (entity instanceof EntityPlayer) {
            this.playerEntities.remove((EntityPlayer)entity);
            this.updateAllPlayersSleepingFlag();
        }

        int i = entity.chunkCoordX;
        int j = entity.chunkCoordZ;
        if (entity.addedToChunk && this.chunkExists(i, j)) {
            this.getChunkFromChunkCoords(i, j).removeEntity(entity);
        }

        this.loadedEntityList.remove(entity);
        this.releaseEntitySkin(entity);
    }

    public void addWorldAccess(IWorldAccess listener) {
        this.worldAccesses.add(listener);
    }

    public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entity, AxisAlignedBB shape) {
        this.collisions.clear();
        int i = MathHelper.floor_double(shape.minX);
        int j = MathHelper.floor_double(shape.maxX + 1.0);
        int k = MathHelper.floor_double(shape.minY);
        int l = MathHelper.floor_double(shape.maxY + 1.0);
        int i1 = MathHelper.floor_double(shape.minZ);
        int j1 = MathHelper.floor_double(shape.maxZ + 1.0);

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = i1; l1 < j1; l1++) {
                if (this.blockExists(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; i2++) {
                        Block block = Block.blocksList[this.getBlockId(k1, i2, l1)];
                        if (block != null) {
                            block.getCollidingBoundingBoxes(this, k1, i2, l1, shape, this.collisions);
                        }
                    }
                }
            }
        }

        double d0 = 0.25;
        List list = this.getEntitiesWithinAABBExcludingEntity(entity, shape.expand(d0, d0, d0));

        for (int j2 = 0; j2 < list.size(); j2++) {
            AxisAlignedBB axisalignedbb = ((Entity)list.get(j2)).getBoundingBox();
            if (axisalignedbb != null && axisalignedbb.intersectsWith(shape)) {
                this.collisions.add(axisalignedbb);
            }

            axisalignedbb = entity.getCollisionAgainstShape((Entity)list.get(j2));
            if (axisalignedbb != null && axisalignedbb.intersectsWith(shape)) {
                this.collisions.add(axisalignedbb);
            }
        }

        return this.collisions;
    }

    public int calculateSkylightSubtracted(float tickDelta) {
        float f = this.getCelestialAngle(tickDelta);
        float f1 = 1.0F - (MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        f1 = 1.0F - f1;
        f1 = (float)(f1 * (1.0 - this.getRain(tickDelta) * 5.0F / 16.0));
        f1 = (float)(f1 * (1.0 - this.getThunder(tickDelta) * 5.0F / 16.0));
        f1 = 1.0F - f1;
        return (int)(f1 * 11.0F);
    }

    public float getCelestialAngle(float tickDelta) {
        return this.worldProvider.calculateCelestialAngle(this.worldInfo.getWorldTime(), tickDelta);
    }

    public int getTopSolidOrLiquidBlock(int x, int z) {
        Chunk chunk = this.getChunkFromBlockCoords(x, z);
        int i = 127;
        x &= 15;
        z &= 15;

        while (i > 0) {
            int j = chunk.getBlockID(x, i, z);
            Material material = j == 0 ? Material.air : Block.blocksList[j].blockMaterial;
            if (material.getIsSolid() || material.getIsLiquid()) {
                return i + 1;
            }

            i--;
        }

        return -1;
    }

    public int findTopSolidBlock(int x, int z) {
        Chunk chunk = this.getChunkFromBlockCoords(x, z);
        int i = 127;
        x &= 15;
        z &= 15;

        while (i > 0) {
            int j = chunk.getBlockID(x, i, z);
            if (j != 0 && Block.blocksList[j].blockMaterial.getIsSolid()) {
                return i + 1;
            }

            i--;
        }

        return -1;
    }

    public void scheduleUpdateTick(int x, int y, int z, int block, int delay) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(x, y, z, block);
        byte b0 = 8;
        if (this.scheduledUpdatesAreImmediate) {
            if (this.checkChunksExist(
                nextticklistentry.xCoord - b0,
                nextticklistentry.yCoord - b0,
                nextticklistentry.zCoord - b0,
                nextticklistentry.xCoord + b0,
                nextticklistentry.yCoord + b0,
                nextticklistentry.zCoord + b0
            )) {
                int i = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);
                if (i == nextticklistentry.blockID && i > 0) {
                    Block.blocksList[i].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                }
            }
        } else {
            if (this.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
                if (block > 0) {
                    nextticklistentry.setScheduledTime(delay + this.worldInfo.getWorldTime());
                }

                if (!this.scheduledTickSet.contains(nextticklistentry)) {
                    this.scheduledTickSet.add(nextticklistentry);
                    this.scheduledTickTreeSet.add(nextticklistentry);
                }
            }
        }
    }

    public void updateEntities() {
        for (int i = 0; i < this.lightningEntities.size(); i++) {
            Entity entity = this.lightningEntities.get(i);
            entity.onUpdate();
            if (entity.isDead) {
                this.lightningEntities.remove(i--);
            }
        }

        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int l = 0; l < this.unloadedEntityList.size(); l++) {
            Entity entity1 = this.unloadedEntityList.get(l);
            int j = entity1.chunkCoordX;
            int k = entity1.chunkCoordZ;
            if (entity1.addedToChunk && this.chunkExists(j, k)) {
                this.getChunkFromChunkCoords(j, k).removeEntity(entity1);
            }
        }

        for (int i1 = 0; i1 < this.unloadedEntityList.size(); i1++) {
            this.releaseEntitySkin(this.unloadedEntityList.get(i1));
        }

        this.unloadedEntityList.clear();

        for (int j1 = 0; j1 < this.loadedEntityList.size(); j1++) {
            Entity entity2 = this.loadedEntityList.get(j1);
            if (entity2.ridingEntity != null) {
                if (!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) {
                    continue;
                }

                entity2.ridingEntity.riddenByEntity = null;
                entity2.ridingEntity = null;
            }

            if (!entity2.isDead) {
                this.updateEntity(entity2);
            }

            if (entity2.isDead) {
                int k1 = entity2.chunkCoordX;
                int l1 = entity2.chunkCoordZ;
                if (entity2.addedToChunk && this.chunkExists(k1, l1)) {
                    this.getChunkFromChunkCoords(k1, l1).removeEntity(entity2);
                }

                this.loadedEntityList.remove(j1--);
                this.releaseEntitySkin(entity2);
            }
        }

        this.isTickingBlockEntities = true;
        Iterator iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity)iterator.next();
            if (!tileentity.isInvalid()) {
                tileentity.updateEntity();
            }

            if (tileentity.isInvalid()) {
                iterator.remove();
                Chunk chunk = this.getChunkFromChunkCoords(tileentity.xCoord >> 4, tileentity.zCoord >> 4);
                if (chunk != null) {
                    chunk.removeChunkBlockTileEntity(tileentity.xCoord & 15, tileentity.yCoord, tileentity.zCoord & 15);
                }
            }
        }

        this.isTickingBlockEntities = false;
        if (!this.pendingBlockEntities.isEmpty()) {
            for (TileEntity tileentity1 : this.pendingBlockEntities) {
                if (!tileentity1.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileentity1)) {
                        this.loadedTileEntityList.add(tileentity1);
                    }

                    Chunk chunk1 = this.getChunkFromChunkCoords(tileentity1.xCoord >> 4, tileentity1.zCoord >> 4);
                    if (chunk1 != null) {
                        chunk1.setChunkBlockTileEntity(tileentity1.xCoord & 15, tileentity1.yCoord, tileentity1.zCoord & 15, tileentity1);
                    }

                    this.markBlockNeedsUpdate(tileentity1.xCoord, tileentity1.yCoord, tileentity1.zCoord);
                }
            }

            this.pendingBlockEntities.clear();
        }
    }

    public void loadBlockEntities(Collection<TileEntity> blockEntities) {
        if (this.isTickingBlockEntities) {
            this.pendingBlockEntities.addAll(blockEntities);
        } else {
            this.loadedTileEntityList.addAll(blockEntities);
        }
    }

    public void updateEntity(Entity entity) {
        this.updateEntityWithOptionalForce(entity, true);
    }

    public void updateEntityWithOptionalForce(Entity entity, boolean requireLoaded) {
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posZ);
        byte b0 = 32;
        if (!requireLoaded || this.checkChunksExist(i - b0, 0, j - b0, i + b0, 128, j + b0)) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            if (requireLoaded && entity.addedToChunk) {
                if (entity.ridingEntity != null) {
                    entity.updateRidden();
                } else {
                    entity.onUpdate();
                }
            }

            if (Double.isNaN(entity.posX) || Double.isInfinite(entity.posX)) {
                entity.posX = entity.lastTickPosX;
            }

            if (Double.isNaN(entity.posY) || Double.isInfinite(entity.posY)) {
                entity.posY = entity.lastTickPosY;
            }

            if (Double.isNaN(entity.posZ) || Double.isInfinite(entity.posZ)) {
                entity.posZ = entity.lastTickPosZ;
            }

            if (Double.isNaN(entity.rotationPitch) || Double.isInfinite(entity.rotationPitch)) {
                entity.rotationPitch = entity.prevRotationPitch;
            }

            if (Double.isNaN(entity.rotationYaw) || Double.isInfinite(entity.rotationYaw)) {
                entity.rotationYaw = entity.prevRotationYaw;
            }

            int k = MathHelper.floor_double(entity.posX / 16.0);
            int l = MathHelper.floor_double(entity.posY / 16.0);
            int i1 = MathHelper.floor_double(entity.posZ / 16.0);
            if (!entity.addedToChunk || entity.chunkCoordX != k || entity.chunkCoordY != l || entity.chunkCoordZ != i1) {
                if (entity.addedToChunk && this.chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
                    this.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ).removeEntityAtIndex(entity, entity.chunkCoordY);
                }

                if (this.chunkExists(k, i1)) {
                    entity.addedToChunk = true;
                    this.getChunkFromChunkCoords(k, i1).addEntity(entity);
                } else {
                    entity.addedToChunk = false;
                }
            }

            if (requireLoaded && entity.addedToChunk && entity.riddenByEntity != null) {
                if (!entity.riddenByEntity.isDead && entity.riddenByEntity.ridingEntity == entity) {
                    this.updateEntity(entity.riddenByEntity);
                } else {
                    entity.riddenByEntity.ridingEntity = null;
                    entity.riddenByEntity = null;
                }
            }
        }
    }

    public boolean checkIfAABBIsClear(AxisAlignedBB bounds) {
        List list = this.getEntitiesWithinAABBExcludingEntity(null, bounds);

        for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (!entity.isDead && entity.preventEntitySpawning) {
                return false;
            }
        }

        return true;
    }

    public boolean containsNonAir(AxisAlignedBB bounds) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);
        if (bounds.minX < 0.0) {
            i--;
        }

        if (bounds.minY < 0.0) {
            k--;
        }

        if (bounds.minZ < 0.0) {
            i1--;
        }

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
                    if (block != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean getIsAnyLiquid(AxisAlignedBB bounds) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);
        if (bounds.minX < 0.0) {
            i--;
        }

        if (bounds.minY < 0.0) {
            k--;
        }

        if (bounds.minZ < 0.0) {
            i1--;
        }

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
                    if (block != null && block.blockMaterial.getIsLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isBoundingBoxBurning(AxisAlignedBB bounds) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);
        if (this.checkChunksExist(i, k, i1, j, l, j1)) {
            for (int k1 = i; k1 < j; k1++) {
                for (int l1 = k; l1 < l; l1++) {
                    for (int i2 = i1; i2 < j1; i2++) {
                        int j2 = this.getBlockId(k1, l1, i2);
                        if (j2 == Block.fire.blockID || j2 == Block.lavaMoving.blockID || j2 == Block.lavaStill.blockID) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB bounds, Material material, Entity entity) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);
        if (!this.checkChunksExist(i, k, i1, j, l, j1)) {
            return false;
        }

        boolean flag = false;
        Vec3D vec3d = Vec3D.createVector(0.0, 0.0, 0.0);

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
                    if (block != null && block.blockMaterial == material) {
                        double d0 = l1 + 1 - BlockFluid.setFluidHeight(this.getBlockMetadata(k1, l1, i2));
                        if (l >= d0) {
                            flag = true;
                            block.velocityToAddToEntity(this, k1, l1, i2, entity, vec3d);
                        }
                    }
                }
            }
        }

        if (vec3d.lengthVector() > 0.0) {
            vec3d = vec3d.normalize();
            double d1 = 0.014;
            entity.motionX = entity.motionX + vec3d.xCoord * d1;
            entity.motionY = entity.motionY + vec3d.yCoord * d1;
            entity.motionZ = entity.motionZ + vec3d.zCoord * d1;
        }

        return flag;
    }

    public boolean isMaterialInBB(AxisAlignedBB bounds, Material material) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
                    if (block != null && block.blockMaterial == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isAABBInMaterial(AxisAlignedBB bounds, Material material) {
        int i = MathHelper.floor_double(bounds.minX);
        int j = MathHelper.floor_double(bounds.maxX + 1.0);
        int k = MathHelper.floor_double(bounds.minY);
        int l = MathHelper.floor_double(bounds.maxY + 1.0);
        int i1 = MathHelper.floor_double(bounds.minZ);
        int j1 = MathHelper.floor_double(bounds.maxZ + 1.0);

        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    Block block = Block.blocksList[this.getBlockId(k1, l1, i2)];
                    if (block != null && block.blockMaterial == material) {
                        int j2 = this.getBlockMetadata(k1, l1, i2);
                        double d0 = l1 + 1;
                        if (j2 < 8) {
                            d0 = l1 + 1 - j2 / 8.0;
                        }

                        if (d0 >= bounds.minY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion createExplosion(Entity source, double x, double y, double z, float power) {
        return this.newExplosion(source, x, y, z, power, false);
    }

    public Explosion newExplosion(Entity source, double x, double y, double z, float power, boolean createFire) {
        Explosion explosion = new Explosion(this, source, x, y, z, power);
        explosion.isFlaming = createFire;
        explosion.doExplosion();
        explosion.doEffects(true);
        return explosion;
    }

    public float getBlockDensity(Vec3D pos, AxisAlignedBB bounds) {
        double d0 = 1.0 / ((bounds.maxX - bounds.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((bounds.maxY - bounds.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((bounds.maxZ - bounds.minZ) * 2.0 + 1.0);
        int i = 0;
        int j = 0;

        for (float f = 0.0F; f <= 1.0F; f = (float)(f + d0)) {
            for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
                    double d3 = bounds.minX + (bounds.maxX - bounds.minX) * f;
                    double d4 = bounds.minY + (bounds.maxY - bounds.minY) * f1;
                    double d5 = bounds.minZ + (bounds.maxZ - bounds.minZ) * f2;
                    if (this.rayTraceBlocks(Vec3D.createVector(d3, d4, d5), pos) == null) {
                        i++;
                    }

                    j++;
                }
            }
        }

        return (float)i / j;
    }

    public void extinguishFire(EntityPlayer player, int x, int y, int z, int face) {
        if (face == 0) {
            y--;
        }

        if (face == 1) {
            y++;
        }

        if (face == 2) {
            z--;
        }

        if (face == 3) {
            z++;
        }

        if (face == 4) {
            x--;
        }

        if (face == 5) {
            x++;
        }

        if (this.getBlockId(x, y, z) == Block.fire.blockID) {
            this.doEvent(player, 1004, x, y, z, 0);
            this.setBlockWithNotify(x, y, z, 0);
        }
    }

    public TileEntity getBlockTileEntity(final int x, final int y, final int z) {
        final Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        if (chunkFromChunkCoords != null) {
            return chunkFromChunkCoords.getChunkBlockTileEntity(x & 0xF, y, z & 0xF);
        }
        return null;
    }

    public void setBlockTileEntity(int x, int y, int z, TileEntity blockEntity) {
        if (!blockEntity.isInvalid()) {
            if (this.isTickingBlockEntities) {
                blockEntity.xCoord = x;
                blockEntity.yCoord = y;
                blockEntity.zCoord = z;
                this.pendingBlockEntities.add(blockEntity);
            } else {
                this.loadedTileEntityList.add(blockEntity);
                Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (chunk != null) {
                    chunk.setChunkBlockTileEntity(x & 15, y, z & 15, blockEntity);
                }
            }
        }
    }

    public void removeBlockTileEntity(int x, int y, int z) {
        TileEntity tileentity = this.getBlockTileEntity(x, y, z);
        if (tileentity != null && this.isTickingBlockEntities) {
            tileentity.invalidate();
        } else {
            if (tileentity != null) {
                this.loadedTileEntityList.remove(tileentity);
            }

            Chunk chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            if (chunk != null) {
                chunk.removeChunkBlockTileEntity(x & 15, y, z & 15);
            }
        }
    }

    public boolean isBlockOpaqueCube(int x, int y, int z) {
        Block block = Block.blocksList[this.getBlockId(x, y, z)];
        return block == null ? false : block.isOpaqueCube();
    }

    @Override
    public boolean isBlockNormalCube(int x, int y, int z) {
        Block block = Block.blocksList[this.getBlockId(x, y, z)];
        return block == null ? false : block.blockMaterial.getIsOpaque() && block.isACube();
    }

    public boolean doLightUpdates() {
        if (this.doLightUpdatesDepth >= 50) {
            return false;
        }

        this.doLightUpdatesDepth++;

        try {
            int i = 500;

            while (this.lightUpdates.size() > 0) {
                if (--i <= 0) {
                    return true;
                }

                this.lightUpdates.remove(this.lightUpdates.size() - 1).run(this);
            }

            return false;
        } finally {
            this.doLightUpdatesDepth--;
        }
    }

    public void scheduleLightingUpdate(EnumSkyBlock type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.updateLight(type, minX, minY, minZ, maxX, maxY, maxZ, true);
    }

    public void updateLight(EnumSkyBlock type, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean expand) {
        if (!this.worldProvider.noSky || type != EnumSkyBlock.Sky) {
            updateLightDepth++;

            try {
                if (updateLightDepth != 50) {
                    int i = (maxX + minX) / 2;
                    int j = (maxZ + minZ) / 2;
                    if (this.blockExists(i, 64, j)) {
                        if (!this.getChunkFromBlockCoords(i, j).isEmpty()) {
                            int k = this.lightUpdates.size();
                            if (expand) {
                                int l = 5;
                                if (l > k) {
                                    l = k;
                                }

                                for (int i1 = 0; i1 < l; i1++) {
                                    MetadataChunkBlock metadatachunkblock = this.lightUpdates.get(this.lightUpdates.size() - i1 - 1);
                                    if (metadatachunkblock.type == type && metadatachunkblock.expand(minX, minY, minZ, maxX, maxY, maxZ)) {
                                        return;
                                    }
                                }
                            }

                            this.lightUpdates.add(new MetadataChunkBlock(type, minX, minY, minZ, maxX, maxY, maxZ));
                            int j1 = 1000000;
                            if (this.lightUpdates.size() > 1000000) {
                                System.out.println("More than " + j1 + " updates, aborting lighting updates");
                                this.lightUpdates.clear();
                            }
                        }
                    }
                }
            } finally {
                updateLightDepth--;
            }
        }
    }

    public void calculateInitialSkylight() {
        int i = this.calculateSkylightSubtracted(1.0F);
        if (i != this.skylightSubtracted) {
            this.skylightSubtracted = i;
        }
    }

    public void setAllowedSpawnTypes(boolean spawnAnimals, boolean spawnMonsters) {
        this.spawnHostileMobs = spawnAnimals;
        this.spawnPeacefulMobs = spawnMonsters;
    }

    public void tick() {
        this.updateWeather();
        if (this.isAllPlayersFullyAsleep()) {
            boolean flag = false;
            if (this.spawnHostileMobs && this.difficultySetting >= 1) {
                flag = SpawnerAnimals.performSleepSpawning(this, this.playerEntities);
            }

            if (!flag) {
                long i = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(i - i % 24000L);
                this.wakeUpAllPlayers();
            }
        }

        SpawnerAnimals.performSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs);
        this.chunkProvider.tick();
        int j = this.calculateSkylightSubtracted(1.0F);
        if (j != this.skylightSubtracted) {
            this.skylightSubtracted = j;

            for (int k = 0; k < this.worldAccesses.size(); k++) {
                this.worldAccesses.get(k).updateAllRenderers();
            }
        }

        long l = this.worldInfo.getWorldTime() + 1L;
        if (l % this.autosavePeriod == 0L) {
            this.saveWorld(false, null);
        }

        this.worldInfo.setWorldTime(l);
        this.TickUpdates(false);
        this.doRandomUpdateTicks();
    }

    private void initWeather() {
        if (this.worldInfo.getIsRaining()) {
            this.rain = 1.0F;
            if (this.worldInfo.getIsThundering()) {
                this.thunder = 1.0F;
            }
        }
    }

    protected void updateWeather() {
        if (!this.worldProvider.noSky) {
            if (this.thunderCooldown > 0) {
                this.thunderCooldown--;
            }

            int i = this.worldInfo.getThunderTime();
            if (i <= 0) {
                if (this.worldInfo.getIsThundering()) {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                } else {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setThunderTime(--i);
                if (i <= 0) {
                    this.worldInfo.setIsThundering(!this.worldInfo.getIsThundering());
                }
            }

            int j = this.worldInfo.getRainTime();
            if (j <= 0) {
                if (this.worldInfo.getIsRaining()) {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                } else {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setRainTime(--j);
                if (j <= 0) {
                    this.worldInfo.setIsRaining(!this.worldInfo.getIsRaining());
                }
            }

            this.prevRain = this.rain;
            if (this.worldInfo.getIsRaining()) {
                this.rain = (float)(this.rain + 0.01);
            } else {
                this.rain = (float)(this.rain - 0.01);
            }

            if (this.rain < 0.0F) {
                this.rain = 0.0F;
            }

            if (this.rain > 1.0F) {
                this.rain = 1.0F;
            }

            this.prevThunder = this.thunder;
            if (this.worldInfo.getIsThundering()) {
                this.thunder = (float)(this.thunder + 0.01);
            } else {
                this.thunder = (float)(this.thunder - 0.01);
            }

            if (this.thunder < 0.0F) {
                this.thunder = 0.0F;
            }

            if (this.thunder > 1.0F) {
                this.thunder = 1.0F;
            }
        }
    }

    private void clearWeather() {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setIsRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setIsThundering(false);
    }

    protected void doRandomUpdateTicks() {
        this.activeChunkSet.clear();

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityplayer = this.playerEntities.get(i);
            int j = MathHelper.floor_double(entityplayer.posX / 16.0);
            int k = MathHelper.floor_double(entityplayer.posZ / 16.0);
            byte b0 = 9;

            for (int l = -b0; l <= b0; l++) {
                for (int i1 = -b0; i1 <= b0; i1++) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(l + j, i1 + k));
                }
            }
        }

        if (this.ambientTickCountdown > 0) {
            this.ambientTickCountdown--;
        }

        for (ChunkCoordIntPair chunkcoordintpair : this.activeChunkSet) {
            int i2 = chunkcoordintpair.chunkXPos * 16;
            int j2 = chunkcoordintpair.chunkZPos * 16;
            Chunk chunk = this.getChunkFromChunkCoords(chunkcoordintpair.chunkXPos, chunkcoordintpair.chunkZPos);
            if (this.ambientTickCountdown == 0) {
                this.distHashCounter = this.distHashCounter * 3 + 1013904223;
                int k2 = this.distHashCounter >> 2;
                int k3 = k2 & 15;
                int j1 = k2 >> 8 & 15;
                int k1 = k2 >> 16 & 127;
                int l1 = chunk.getBlockID(k3, k1, j1);
                k3 += i2;
                j1 += j2;
                if (l1 == 0
                    && this.getBlockLightValueNoChecks(k3, k1, j1) <= this.rand.nextInt(8)
                    && this.getSavedLightValue(EnumSkyBlock.Sky, k3, k1, j1) <= 0) {
                    EntityPlayer entityplayer1 = this.getClosestPlayer(k3 + 0.5, k1 + 0.5, j1 + 0.5, 8.0);
                    if (entityplayer1 != null && entityplayer1.getDistanceSq(k3 + 0.5, k1 + 0.5, j1 + 0.5) > 4.0) {
                        this.playSoundEffect(k3 + 0.5, k1 + 0.5, j1 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                        this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                    }
                }
            }

            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.distHashCounter = this.distHashCounter * 3 + 1013904223;
                int l2 = this.distHashCounter >> 2;
                int l3 = i2 + (l2 & 15);
                int k4 = j2 + (l2 >> 8 & 15);
                int j5 = this.getTopSolidOrLiquidBlock(l3, k4);
                if (this.canLightningStrikeAt(l3, j5, k4)) {
                    this.addLightningBolt(new EntityLightningBolt(this, l3, j5, k4));
                    this.thunderCooldown = 2;
                }
            }

            if (this.rand.nextInt(16) == 0) {
                this.distHashCounter = this.distHashCounter * 3 + 1013904223;
                int i3 = this.distHashCounter >> 2;
                int i4 = i3 & 15;
                int l4 = i3 >> 8 & 15;
                int k5 = this.getTopSolidOrLiquidBlock(i4 + i2, l4 + j2);
                if (this.getWorldChunkManager().getBiomeGenAt(i4 + i2, l4 + j2).getEnableSnow()
                    && k5 >= 0
                    && k5 < 128
                    && chunk.getSavedLightValue(EnumSkyBlock.Block, i4, k5, l4) < 10) {
                    int i6 = chunk.getBlockID(i4, k5 - 1, l4);
                    int k6 = chunk.getBlockID(i4, k5, l4);
                    if (this.isRaining()
                        && k6 == 0
                        && Block.snow.canPlaceBlockAt(this, i4 + i2, k5, l4 + j2)
                        && i6 != 0
                        && i6 != Block.ice.blockID
                        && Block.blocksList[i6].blockMaterial.getIsSolid()) {
                        this.setBlockWithNotify(i4 + i2, k5, l4 + j2, Block.snow.blockID);
                    }

                    if (i6 == Block.waterStill.blockID && chunk.getBlockMetadata(i4, k5 - 1, l4) == 0) {
                        this.setBlockWithNotify(i4 + i2, k5 - 1, l4 + j2, Block.ice.blockID);
                    }
                }
            }

            for (int j3 = 0; j3 < 80; j3++) {
                this.distHashCounter = this.distHashCounter * 3 + 1013904223;
                int j4 = this.distHashCounter >> 2;
                int i5 = j4 & 15;
                int l5 = j4 >> 8 & 15;
                int j6 = j4 >> 16 & 127;
                int l6 = chunk.blocks[i5 << 11 | l5 << 7 | j6] & 255;
                if (Block.tickOnLoad[l6]) {
                    Block.blocksList[l6].updateTick(this, i5 + i2, j6, l5 + j2, this.rand);
                }
            }
        }
    }

    public boolean TickUpdates(boolean flush) {
        int i = this.scheduledTickTreeSet.size();
        if (i != this.scheduledTickSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }

        if (i > 1000) {
            i = 1000;
        }

        for (int j = 0; j < i; j++) {
            NextTickListEntry nextticklistentry = this.scheduledTickTreeSet.first();
            if (!flush && nextticklistentry.scheduledTime > this.worldInfo.getWorldTime()) {
                break;
            }

            this.scheduledTickTreeSet.remove(nextticklistentry);
            this.scheduledTickSet.remove(nextticklistentry);
            byte b0 = 8;
            if (this.checkChunksExist(
                nextticklistentry.xCoord - b0,
                nextticklistentry.yCoord - b0,
                nextticklistentry.zCoord - b0,
                nextticklistentry.xCoord + b0,
                nextticklistentry.yCoord + b0,
                nextticklistentry.zCoord + b0
            )) {
                int k = this.getBlockId(nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord);
                if (k == nextticklistentry.blockID && k > 0) {
                    Block.blocksList[k].updateTick(this, nextticklistentry.xCoord, nextticklistentry.yCoord, nextticklistentry.zCoord, this.rand);
                }
            }
        }

        return this.scheduledTickTreeSet.size() != 0;
    }

    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity exclude, AxisAlignedBB bounds) {
        this.entitiesInArea.clear();
        int i = MathHelper.floor_double((bounds.minX - 2.0) / 16.0);
        int j = MathHelper.floor_double((bounds.maxX + 2.0) / 16.0);
        int k = MathHelper.floor_double((bounds.minZ - 2.0) / 16.0);
        int l = MathHelper.floor_double((bounds.maxZ + 2.0) / 16.0);

        for (int i1 = i; i1 <= j; i1++) {
            for (int j1 = k; j1 <= l; j1++) {
                if (this.chunkExists(i1, j1)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesWithinAABBForEntity(exclude, bounds, this.entitiesInArea);
                }
            }
        }

        return this.entitiesInArea;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
        int i = MathHelper.floor_double((bounds.minX - 2.0) / 16.0);
        int j = MathHelper.floor_double((bounds.maxX + 2.0) / 16.0);
        int k = MathHelper.floor_double((bounds.minZ - 2.0) / 16.0);
        int l = MathHelper.floor_double((bounds.maxZ + 2.0) / 16.0);
        ArrayList arraylist = new ArrayList();

        for (int i1 = i; i1 <= j; i1++) {
            for (int j1 = k; j1 <= l; j1++) {
                if (this.chunkExists(i1, j1)) {
                    this.getChunkFromChunkCoords(i1, j1).getEntitiesOfTypeWithinAAAB(type, bounds, arraylist);
                }
            }
        }

        return arraylist;
    }

    public void updateTileEntityChunkAndDoNothing(int x, int y, int z, TileEntity blockEntity) {
        if (this.blockExists(x, y, z)) {
            this.getChunkFromBlockCoords(x, z).setChunkModified();
        }

        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).doNothingWithTileEntity(x, y, z, blockEntity);
        }
    }

    public int countEntities(Class<?> type) {
        int i = 0;

        for (int j = 0; j < this.loadedEntityList.size(); j++) {
            Entity entity = this.loadedEntityList.get(j);
            if (type.isAssignableFrom(entity.getClass())) {
                i++;
            }
        }

        return i;
    }

    public void addLoadedEntities(List<Entity> entities) {
        this.loadedEntityList.addAll(entities);

        for (int i = 0; i < entities.size(); i++) {
            this.obtainEntitySkin((Entity)entities.get(i));
        }
    }

    public void addUnloadedEntities(List<Entity> entities) {
        this.unloadedEntityList.addAll(entities);
    }

    public boolean canBlockBePlacedAt(int block, int x, int y, int z, boolean skipCollisionChecks, int face) {
        int i = this.getBlockId(x, y, z);
        Block blockx = Block.blocksList[i];
        Block block1x = Block.blocksList[block];
        AxisAlignedBB axisalignedbb = block1x.getCollisionBoundingBoxFromPool(this, x, y, z);
        if (skipCollisionChecks) {
            axisalignedbb = null;
        }

        if (axisalignedbb != null && !this.checkIfAABBIsClear(axisalignedbb)) {
            return false;
        }

        if (blockx == Block.waterMoving
            || blockx == Block.waterStill
            || blockx == Block.lavaMoving
            || blockx == Block.lavaStill
            || blockx == Block.fire
            || blockx == Block.snow) {
            blockx = null;
        }

        return block > 0 && blockx == null && block1x.canPlaceBlockOnSide(this, x, y, z, face);
    }

    public PathEntity getPathToEntity(Entity entity, Entity target, float range) {
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);
        int l = (int)(range + 16.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2);
        return new Pathfinder(chunkcache).createEntityPathTo(entity, target, range);
    }

    public PathEntity getEntityPathToXYZ(Entity entity, int x, int y, int z, float range) {
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);
        int l = (int)(range + 8.0F);
        int i1 = i - l;
        int j1 = j - l;
        int k1 = k - l;
        int l1 = i + l;
        int i2 = j + l;
        int j2 = k + l;
        ChunkCache chunkcache = new ChunkCache(this, i1, j1, k1, l1, i2, j2);
        return new Pathfinder(chunkcache).createEntityPathTo(entity, x, y, z, range);
    }

    public boolean isBlockProvidingPowerTo(int x, int y, int z, int dir) {
        int i = this.getBlockId(x, y, z);
        return i == 0 ? false : Block.blocksList[i].isIndirectlyPoweringTo(this, x, y, z, dir);
    }

    public boolean isBlockGettingPowered(int x, int y, int z) {
        if (this.isBlockProvidingPowerTo(x, y - 1, z, 0)) {
            return true;
        } else if (this.isBlockProvidingPowerTo(x, y + 1, z, 1)) {
            return true;
        } else if (this.isBlockProvidingPowerTo(x, y, z - 1, 2)) {
            return true;
        } else if (this.isBlockProvidingPowerTo(x, y, z + 1, 3)) {
            return true;
        } else {
            return this.isBlockProvidingPowerTo(x - 1, y, z, 4) ? true : this.isBlockProvidingPowerTo(x + 1, y, z, 5);
        }
    }

    public boolean isBlockIndirectlyProvidingPowerTo(int x, int y, int z, int dir) {
        if (this.isBlockNormalCube(x, y, z)) {
            return this.isBlockGettingPowered(x, y, z);
        }

        int i = this.getBlockId(x, y, z);
        return i == 0 ? false : Block.blocksList[i].isPoweringTo(this, x, y, z, dir);
    }

    public boolean isBlockIndirectlyGettingPowered(int x, int y, int z) {
        if (this.isBlockIndirectlyProvidingPowerTo(x, y - 1, z, 0)) {
            return true;
        } else if (this.isBlockIndirectlyProvidingPowerTo(x, y + 1, z, 1)) {
            return true;
        } else if (this.isBlockIndirectlyProvidingPowerTo(x, y, z - 1, 2)) {
            return true;
        } else if (this.isBlockIndirectlyProvidingPowerTo(x, y, z + 1, 3)) {
            return true;
        } else {
            return this.isBlockIndirectlyProvidingPowerTo(x - 1, y, z, 4) ? true : this.isBlockIndirectlyProvidingPowerTo(x + 1, y, z, 5);
        }
    }

    public EntityPlayer getClosestPlayerToEntity(Entity entity, double range) {
        return this.getClosestPlayer(entity.posX, entity.posY, entity.posZ, range);
    }

    public EntityPlayer getClosestPlayer(double x, double y, double z, double range) {
        double d0 = -1.0;
        EntityPlayer entityplayer = null;

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityplayer1 = this.playerEntities.get(i);
            double d1 = entityplayer1.getDistanceSq(x, y, z);
            if ((range < 0.0 || d1 < range * range) && (d0 == -1.0 || d1 < d0)) {
                d0 = d1;
                entityplayer = entityplayer1;
            }
        }

        return entityplayer;
    }

    public EntityPlayer getPlayerEntityByName(String name) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            if (name.equals(this.playerEntities.get(i).username)) {
                return this.playerEntities.get(i);
            }
        }

        return null;
    }

    public byte[] getChunkData(int minX, int minY, int minZ, int widthX, int height, int widthZ) {
        byte[] abyte = new byte[widthX * height * widthZ * 5 / 2];
        int i = minX >> 4;
        int j = minZ >> 4;
        int k = minX + widthX - 1 >> 4;
        int l = minZ + widthZ - 1 >> 4;
        int i1 = 0;
        int j1 = minY;
        int k1 = minY + height;
        if (j1 < 0) {
            j1 = 0;
        }

        if (k1 > 128) {
            k1 = 128;
        }

        for (int l1 = i; l1 <= k; l1++) {
            int i2 = minX - l1 * 16;
            int j2 = minX + widthX - l1 * 16;
            if (i2 < 0) {
                i2 = 0;
            }

            if (j2 > 16) {
                j2 = 16;
            }

            for (int k2 = j; k2 <= l; k2++) {
                int l2 = minZ - k2 * 16;
                int i3 = minZ + widthZ - k2 * 16;
                if (l2 < 0) {
                    l2 = 0;
                }

                if (i3 > 16) {
                    i3 = 16;
                }

                i1 = this.getChunkFromChunkCoords(l1, k2).getChunkData(abyte, i2, j1, l2, j2, k1, i3, i1);
            }
        }

        return abyte;
    }

    public void checkSessionLock() {
        this.worldFile.checkSessionLock();
    }

    public void setWorldTime(long time) {
        this.worldInfo.setWorldTime(time);
    }

    public void changeTime(long time) {
        long i = time - this.worldInfo.getWorldTime();

        for (NextTickListEntry nextticklistentry : this.scheduledTickSet) {
            nextticklistentry.scheduledTime += i;
        }

        this.setWorldTime(time);
    }

    public long getRandomSeed() {
        return this.worldInfo.getRandomSeed();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    public ChunkCoordinates getSpawnPoint() {
        return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
    }

    public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    public void sendTrackedEntityStatusUpdatePacket(Entity entity, byte event) {
    }

    public IChunkProvider getChunkSource() {
        return this.chunkProvider;
    }

    public void playNoteAt(int x, int y, int z, int type, int data) {
        int i = this.getBlockId(x, y, z);
        if (i > 0) {
            Block.blocksList[i].playBlock(this, x, y, z, type, data);
        }
    }

    public ISaveHandler getWorldFile() {
        return this.worldFile;
    }

    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public void updateAllPlayersSleepingFlag() {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();

        for (EntityPlayer entityplayer : this.playerEntities) {
            if (!entityplayer.isSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeUpAllPlayers() {
        this.allPlayersSleeping = false;

        for (EntityPlayer entityplayer : this.playerEntities) {
            if (entityplayer.isSleeping()) {
                entityplayer.wakeUpPlayer(false, false, true);
            }
        }

        this.clearWeather();
    }

    public boolean isAllPlayersFullyAsleep() {
        if (this.allPlayersSleeping && !this.singleplayerWorld) {
            for (EntityPlayer entityplayer : this.playerEntities) {
                if (!entityplayer.isPlayerFullyAsleep()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public float getThunder(float tickDelta) {
        return (this.prevThunder + (this.thunder - this.prevThunder) * tickDelta) * this.getRain(tickDelta);
    }

    public float getRain(float tickDelta) {
        return this.prevRain + (this.rain - this.prevRain) * tickDelta;
    }

    public boolean isThundering() {
        return this.getThunder(1.0F) > 0.9;
    }

    public boolean isRaining() {
        return this.getRain(1.0F) > 0.2;
    }

    public boolean canLightningStrikeAt(int x, int y, int z) {
        if (!this.isRaining()) {
            return false;
        }

        if (!this.canBlockSeeTheSky(x, y, z)) {
            return false;
        }

        if (this.getTopSolidOrLiquidBlock(x, z) > y) {
            return false;
        }

        BiomeGenBase biomegenbase = this.getWorldChunkManager().getBiomeGenAt(x, z);
        return biomegenbase.getEnableSnow() ? false : biomegenbase.canSpawnLightningBolt();
    }

    public void setSavedData(String id, MapDataBase data) {
        this.savedDataStorage.set(id, data);
    }

    public <T extends MapDataBase> T loadSavedData(Class<T> type, String id) {
        return this.savedDataStorage.load(type, id);
    }

    public int getSavedDataCount(String id) {
        return this.savedDataStorage.getNextCount(id);
    }

    public void doEvent(int type, int x, int y, int z, int data) {
        this.doEvent(null, type, x, y, z, data);
    }

    public void doEvent(EntityPlayer source, int type, int x, int y, int z, int data) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            this.worldAccesses.get(i).doEvent(source, type, x, y, z, data);
        }
    }
}
