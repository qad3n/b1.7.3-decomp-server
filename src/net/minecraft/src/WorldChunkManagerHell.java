package net.minecraft.src;

import java.util.Arrays;

public class WorldChunkManagerHell
extends WorldChunkManager {
    private BiomeGenBase biome;
    private double hellTemperature;
    private double downfall;

    public WorldChunkManagerHell(BiomeGenBase biome, double temperature, double downfall) {
        this.biome = biome;
        this.hellTemperature = temperature;
        this.downfall = downfall;
    }

    public BiomeGenBase getBiome(ChunkCoordIntPair pos) {
        return this.biome;
    }

    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return this.biome;
    }

    public BiomeGenBase[] getBiomes(int x, int z, int sizeX, int sizeZ) {
        this.biomesBuffer = this.loadBlockGeneratorData(this.biomesBuffer, x, z, sizeX, sizeZ);
        return this.biomesBuffer;
    }

    public double[] getTemperatures(double[] temperatures, int x, int z, int sizeX, int sizeZ) {
        if (temperatures == null || temperatures.length < sizeX * sizeZ) {
            temperatures = new double[sizeX * sizeZ];
        }
        Arrays.fill(temperatures, 0, sizeX * sizeZ, this.hellTemperature);
        return temperatures;
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int sizeX, int sizeZ) {
        if (biomes == null || biomes.length < sizeX * sizeZ) {
            biomes = new BiomeGenBase[sizeX * sizeZ];
        }
        if (this.temperature == null || this.temperature.length < sizeX * sizeZ) {
            this.temperature = new double[sizeX * sizeZ];
            this.humidity = new double[sizeX * sizeZ];
        }
        Arrays.fill(biomes, 0, sizeX * sizeZ, this.biome);
        Arrays.fill(this.humidity, 0, sizeX * sizeZ, this.downfall);
        Arrays.fill(this.temperature, 0, sizeX * sizeZ, this.hellTemperature);
        return biomes;
    }
}

