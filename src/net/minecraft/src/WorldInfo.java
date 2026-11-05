package net.minecraft.src;

import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WorldInfo {
    private long randomSeed;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long worldTime;
    private long lastTimePlayed;
    private long sizeOnDisk;
    private NBTTagCompound playerData;
    private int dimension;
    private String levelName;
    private int saveVersion;
    private boolean isRaining;
    private int rainTime;
    private boolean isThundering;
    private int thunderTime;

    public WorldInfo(NBTTagCompound nbt) {
        this.randomSeed = nbt.getLong("RandomSeed");
        this.spawnX = nbt.getInteger("SpawnX");
        this.spawnY = nbt.getInteger("SpawnY");
        this.spawnZ = nbt.getInteger("SpawnZ");
        this.worldTime = nbt.getLong("Time");
        this.lastTimePlayed = nbt.getLong("LastPlayed");
        this.sizeOnDisk = nbt.getLong("SizeOnDisk");
        this.levelName = nbt.getString("LevelName");
        this.saveVersion = nbt.getInteger("version");
        this.rainTime = nbt.getInteger("rainTime");
        this.isRaining = nbt.getBoolean("raining");
        this.thunderTime = nbt.getInteger("thunderTime");
        this.isThundering = nbt.getBoolean("thundering");
        if (nbt.hasKey("Player")) {
            this.playerData = nbt.getCompoundTag("Player");
            this.dimension = this.playerData.getInteger("Dimension");
        }
    }

    public WorldInfo(long seed, String worldName) {
        this.randomSeed = seed;
        this.levelName = worldName;
    }

    public WorldInfo(WorldInfo data) {
        this.randomSeed = data.randomSeed;
        this.spawnX = data.spawnX;
        this.spawnY = data.spawnY;
        this.spawnZ = data.spawnZ;
        this.worldTime = data.worldTime;
        this.lastTimePlayed = data.lastTimePlayed;
        this.sizeOnDisk = data.sizeOnDisk;
        this.playerData = data.playerData;
        this.dimension = data.dimension;
        this.levelName = data.levelName;
        this.saveVersion = data.saveVersion;
        this.rainTime = data.rainTime;
        this.isRaining = data.isRaining;
        this.thunderTime = data.thunderTime;
        this.isThundering = data.isThundering;
    }

    public NBTTagCompound toNbt() {
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        this.saveNBTTag(nBTTagCompound, this.playerData);
        return nBTTagCompound;
    }

    public NBTTagCompound toNbt(List<EntityPlayer> players) {
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        EntityPlayer entityPlayer = null;
        NBTTagCompound nBTTagCompound2 = null;
        if (players.size() > 0) {
            entityPlayer = players.get(0);
        }
        if (entityPlayer != null) {
            nBTTagCompound2 = new NBTTagCompound();
            entityPlayer.writeToNBT(nBTTagCompound2);
        }
        this.saveNBTTag(nBTTagCompound, nBTTagCompound2);
        return nBTTagCompound;
    }

    private void saveNBTTag(NBTTagCompound nbt, NBTTagCompound playerData) {
        nbt.setLong("RandomSeed", this.randomSeed);
        nbt.setInteger("SpawnX", this.spawnX);
        nbt.setInteger("SpawnY", this.spawnY);
        nbt.setInteger("SpawnZ", this.spawnZ);
        nbt.setLong("Time", this.worldTime);
        nbt.setLong("SizeOnDisk", this.sizeOnDisk);
        nbt.setLong("LastPlayed", System.currentTimeMillis());
        nbt.setString("LevelName", this.levelName);
        nbt.setInteger("version", this.saveVersion);
        nbt.setInteger("rainTime", this.rainTime);
        nbt.setBoolean("raining", this.isRaining);
        nbt.setInteger("thunderTime", this.thunderTime);
        nbt.setBoolean("thundering", this.isThundering);
        if (playerData != null) {
            nbt.setCompoundTag("Player", playerData);
        }
    }

    public long getRandomSeed() {
        return this.randomSeed;
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getWorldTime() {
        return this.worldTime;
    }

    public long getSizeOnDisk() {
        return this.sizeOnDisk;
    }

    public int getDimension() {
        return this.dimension;
    }

    public void setWorldTime(long time) {
        this.worldTime = time;
    }

    public void setSizeOnDisk(long size) {
        this.sizeOnDisk = size;
    }

    public void setSpawnPosition(int x, int y, int z) {
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;
    }

    public void setLevelName(String name) {
        this.levelName = name;
    }

    public int getVersion() {
        return this.saveVersion;
    }

    public void setVersion(int version) {
        this.saveVersion = version;
    }

    public boolean getIsThundering() {
        return this.isThundering;
    }

    public void setIsThundering(boolean thundering) {
        this.isThundering = thundering;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public boolean getIsRaining() {
        return this.isRaining;
    }

    public void setIsRaining(boolean raining) {
        this.isRaining = raining;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }
}

