package net.minecraft.src;


public abstract class WorldProvider {
    public World worldObj;
    public WorldChunkManager worldChunkMgr;
    public boolean unnatural = false;
    public boolean isHellWorld = false;
    public boolean noSky = false;
    public float[] lightBrightnessTable = new float[16];
    public int worldType = 0;
    private float[] sunriseColor = new float[4];

    public final void registerWorld(World world) {
        this.worldObj = world;
        this.registerWorldChunkManager();
        this.generateLightBrightnessTable();
    }

    protected void generateLightBrightnessTable() {
        float f = 0.05f;
        for (int i = 0; i <= 15; ++i) {
            float f2 = 1.0f - (float)i / 15.0f;
            this.lightBrightnessTable[i] = (1.0f - f2) / (f2 * 3.0f + 1.0f) * (1.0f - f) + f;
        }
    }

    protected void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManager(this.worldObj);
    }

    public IChunkProvider getChunkProvider() {
        return new ChunkProviderGenerate(this.worldObj, this.worldObj.getRandomSeed());
    }

    public boolean canCoordinateBeSpawn(int x, int z) {
        int n = this.worldObj.getFirstUncoveredBlock(x, z);
        return n == Block.sand.blockID;
    }

    public float calculateCelestialAngle(long time, float tickDelta) {
        int n = (int)(time % 24000L);
        float f = ((float)n + tickDelta) / 24000.0f - 0.25f;
        if (f < 0.0f) {
            f += 1.0f;
        }
        if (f > 1.0f) {
            f -= 1.0f;
        }
        float f2 = f;
        f = 1.0f - (float)((Math.cos((double)f * Math.PI) + 1.0) / 2.0);
        f = f2 + (f - f2) / 3.0f;
        return f;
    }

    public boolean hasSpawnPoint() {
        return true;
    }

    public static WorldProvider fromId(int id) {
        if (id == -1) {
            return new WorldProviderHell();
        }
        if (id == 0) {
            return new WorldProviderSurface();
        }
        if (id == 1) {
            return new WorldProviderSky();
        }
        return null;
    }
}

