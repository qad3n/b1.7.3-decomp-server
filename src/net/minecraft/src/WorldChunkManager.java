package net.minecraft.src;

import java.util.Random;

public class WorldChunkManager {
    private NoiseGeneratorOctaves2 temperatureNoise;
    private NoiseGeneratorOctaves2 downfallNoise;
    private NoiseGeneratorOctaves2 biomeNoise;
    public double[] temperature;
    public double[] humidity;
    public double[] noises;
    public BiomeGenBase[] biomesBuffer;

    protected WorldChunkManager() {
    }

    public WorldChunkManager(World world) {
        this.temperatureNoise = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 9871L), 4);
        this.downfallNoise = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 39811L), 4);
        this.biomeNoise = new NoiseGeneratorOctaves2(new Random(world.getRandomSeed() * 543321L), 2);
    }

    public BiomeGenBase getBiome(ChunkCoordIntPair pos) {
        return this.getBiomeGenAt(pos.chunkXPos << 4, pos.chunkZPos << 4);
    }

    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return this.getBiomes(x, z, 1, 1)[0];
    }

    public BiomeGenBase[] getBiomes(int x, int z, int sizeX, int sizeZ) {
        this.biomesBuffer = this.loadBlockGeneratorData(this.biomesBuffer, x, z, sizeX, sizeZ);
        return this.biomesBuffer;
    }

    public double[] getTemperatures(double[] temperatures, int x, int z, int sizeX, int sizeZ) {
        if (temperatures == null || temperatures.length < sizeX * sizeZ) {
            temperatures = new double[sizeX * sizeZ];
        }

        temperatures = this.temperatureNoise.getRegion(temperatures, x, z, sizeX, sizeZ, 0.025F, 0.025F, 0.25);
        this.noises = this.biomeNoise.getRegion(this.noises, x, z, sizeX, sizeZ, 0.25, 0.25, 0.5882352941176471);
        int i = 0;

        for (int j = 0; j < sizeX; j++) {
            for (int k = 0; k < sizeZ; k++) {
                double d0 = this.noises[i] * 1.1 + 0.5;
                double d1 = 0.01;
                double d2 = 1.0 - d1;
                double d3 = (temperatures[i] * 0.15 + 0.7) * d2 + d0 * d1;
                d3 = 1.0 - (1.0 - d3) * (1.0 - d3);
                if (d3 < 0.0) {
                    d3 = 0.0;
                }

                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                temperatures[i] = d3;
                i++;
            }
        }

        return temperatures;
    }

    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int z, int sizeX, int sizeZ) {
        if (biomes == null || biomes.length < sizeX * sizeZ) {
            biomes = new BiomeGenBase[sizeX * sizeZ];
        }

        this.temperature = this.temperatureNoise.getRegion(this.temperature, x, z, sizeX, sizeX, 0.025F, 0.025F, 0.25);
        this.humidity = this.downfallNoise.getRegion(this.humidity, x, z, sizeX, sizeX, 0.05F, 0.05F, 0.3333333333333333);
        this.noises = this.biomeNoise.getRegion(this.noises, x, z, sizeX, sizeX, 0.25, 0.25, 0.5882352941176471);
        int i = 0;

        for (int j = 0; j < sizeX; j++) {
            for (int k = 0; k < sizeZ; k++) {
                double d0 = this.noises[i] * 1.1 + 0.5;
                double d1 = 0.01;
                double d2 = 1.0 - d1;
                double d3 = (this.temperature[i] * 0.15 + 0.7) * d2 + d0 * d1;
                d1 = 0.002;
                d2 = 1.0 - d1;
                double d4 = (this.humidity[i] * 0.15 + 0.5) * d2 + d0 * d1;
                d3 = 1.0 - (1.0 - d3) * (1.0 - d3);
                if (d3 < 0.0) {
                    d3 = 0.0;
                }

                if (d4 < 0.0) {
                    d4 = 0.0;
                }

                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                if (d4 > 1.0) {
                    d4 = 1.0;
                }

                this.temperature[i] = d3;
                this.humidity[i] = d4;
                biomes[i++] = BiomeGenBase.getBiomeFromLookup(d3, d4);
            }
        }

        return biomes;
    }
}
