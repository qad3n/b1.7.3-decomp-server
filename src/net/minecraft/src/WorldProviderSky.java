package net.minecraft.src;


public class WorldProviderSky
extends WorldProvider {
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.SKY, 0.5, 0.0);
        this.worldType = 1;
    }

    public IChunkProvider getChunkProvider() {
        return new ChunkProviderSky(this.worldObj, this.worldObj.getRandomSeed());
    }

    public float calculateCelestialAngle(long time, float tickDelta) {
        return 0.0f;
    }

    public boolean canCoordinateBeSpawn(int x, int z) {
        int n = this.worldObj.getFirstUncoveredBlock(x, z);
        if (n == 0) {
            return false;
        }
        return Block.blocksList[n].blockMaterial.getIsSolid();
    }
}

