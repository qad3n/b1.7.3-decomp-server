package net.minecraft.src;


public class WorldProviderHell
extends WorldProvider {
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.hell, 1.0, 0.0);
        this.unnatural = true;
        this.isHellWorld = true;
        this.noSky = true;
        this.worldType = -1;
    }

    protected void generateLightBrightnessTable() {
        float f = 0.1f;
        for (int i = 0; i <= 15; ++i) {
            float f2 = 1.0f - (float)i / 15.0f;
            this.lightBrightnessTable[i] = (1.0f - f2) / (f2 * 3.0f + 1.0f) * (1.0f - f) + f;
        }
    }

    public IChunkProvider getChunkProvider() {
        return new ChunkProviderHell(this.worldObj, this.worldObj.getRandomSeed());
    }

    public boolean canCoordinateBeSpawn(int x, int z) {
        int n = this.worldObj.getFirstUncoveredBlock(x, z);
        if (n == Block.bedrock.blockID) {
            return false;
        }
        if (n == 0) {
            return false;
        }
        return Block.opaqueCubeLookup[n];
    }

    public float calculateCelestialAngle(long time, float tickDelta) {
        return 0.5f;
    }

    public boolean hasSpawnPoint() {
        return false;
    }
}

