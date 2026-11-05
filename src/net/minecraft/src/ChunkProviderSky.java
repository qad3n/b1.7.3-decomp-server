package net.minecraft.src;

import java.util.Random;

public class ChunkProviderSky implements IChunkProvider {
    private Random random;
    private NoiseGeneratorOctaves minLimitPerlinNoise;
    private NoiseGeneratorOctaves maxLimitPerlinNoise;
    private NoiseGeneratorOctaves perlinNoise1;
    private NoiseGeneratorOctaves perlinNoise2;
    private NoiseGeneratorOctaves perlinNoise3;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    public NoiseGeneratorOctaves forestNoise;
    private World world;
    private double[] heightMap;
    private double[] sandBuffer = new double[256];
    private double[] gravelBuffer = new double[256];
    private double[] depthBuffer = new double[256];
    private MapGenBase cave = new MapGenCaves();
    private BiomeGenBase[] biomes;
    double[] perlinNoiseBuffer;
    double[] minLimitPerlinNoiseBuffer;
    double[] maxLimitPerlinNoiseBuffer;
    double[] scaleNoiseBuffer;
    double[] depthNoiseBuffer;
    int[][] waterDepths = new int[32][32];
    private double[] temperatures;

    public ChunkProviderSky(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.random, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.random, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.random, 8);
        this.perlinNoise2 = new NoiseGeneratorOctaves(this.random, 4);
        this.perlinNoise3 = new NoiseGeneratorOctaves(this.random, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.random, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.random, 16);
        this.forestNoise = new NoiseGeneratorOctaves(this.random, 8);
    }

    public void buildTerrain(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomes, double[] temperatures) {
        int n = 2;
        int n2 = n + 1;
        int n3 = 33;
        int n4 = n + 1;
        this.heightMap = this.generateHeightMap(this.heightMap, chunkX * n, 0, chunkZ * n, n2, n3, n4);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < 32; ++k) {
                    double d = 0.25;
                    double d2 = this.heightMap[((i + 0) * n4 + (j + 0)) * n3 + (k + 0)];
                    double d3 = this.heightMap[((i + 0) * n4 + (j + 1)) * n3 + (k + 0)];
                    double d4 = this.heightMap[((i + 1) * n4 + (j + 0)) * n3 + (k + 0)];
                    double d5 = this.heightMap[((i + 1) * n4 + (j + 1)) * n3 + (k + 0)];
                    double d6 = (this.heightMap[((i + 0) * n4 + (j + 0)) * n3 + (k + 1)] - d2) * d;
                    double d7 = (this.heightMap[((i + 0) * n4 + (j + 1)) * n3 + (k + 1)] - d3) * d;
                    double d8 = (this.heightMap[((i + 1) * n4 + (j + 0)) * n3 + (k + 1)] - d4) * d;
                    double d9 = (this.heightMap[((i + 1) * n4 + (j + 1)) * n3 + (k + 1)] - d5) * d;
                    for (int i2 = 0; i2 < 4; ++i2) {
                        double d10 = 0.125;
                        double d11 = d2;
                        double d12 = d3;
                        double d13 = (d4 - d2) * d10;
                        double d14 = (d5 - d3) * d10;
                        for (int i3 = 0; i3 < 8; ++i3) {
                            int n5 = i3 + i * 8 << 11 | 0 + j * 8 << 7 | k * 4 + i2;
                            int n6 = 128;
                            double d15 = 0.125;
                            double d16 = d11;
                            double d17 = (d12 - d11) * d15;
                            for (int i4 = 0; i4 < 8; ++i4) {
                                int n7 = 0;
                                if (d16 > 0.0) {
                                    n7 = Block.stone.blockID;
                                }
                                blocks[n5] = (byte) n7;
                                n5 += n6;
                                d16 += d17;
                            }
                            d11 += d13;
                            d12 += d14;
                        }
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                        d5 += d9;
                    }
                }
            }
        }
    }

    public void buildSurfaces(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomes) {
        double d0 = 0.03125;
        this.sandBuffer = this.perlinNoise2.generateNoiseOctaves(this.sandBuffer, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0, d0, 1.0);
        this.gravelBuffer = this.perlinNoise2.generateNoiseOctaves(this.gravelBuffer, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, d0, 1.0, d0);
        this.depthBuffer = this.perlinNoise3.generateNoiseOctaves(this.depthBuffer, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0 * 2.0, d0 * 2.0, d0 * 2.0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BiomeGenBase biomegenbase = biomes[i + j * 16];
                int k = (int)(this.depthBuffer[i + j * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
                int l = -1;
                byte b0 = biomegenbase.topBlock;
                byte b1 = biomegenbase.fillerBlock;

                for (int i1 = 127; i1 >= 0; i1--) {
                    int j1 = (j * 16 + i) * 128 + i1;
                    byte b2 = blocks[j1];
                    if (b2 == 0) {
                        l = -1;
                    } else if (b2 == Block.stone.blockID) {
                        if (l == -1) {
                            if (k <= 0) {
                                b0 = 0;
                                b1 = (byte)Block.stone.blockID;
                            }

                            l = k;
                            if (i1 >= 0) {
                                blocks[j1] = b0;
                            } else {
                                blocks[j1] = b1;
                            }
                        } else if (l > 0) {
                            l--;
                            blocks[j1] = b1;
                            if (l == 0 && b1 == Block.sand.blockID) {
                                l = this.random.nextInt(4);
                                b1 = (byte)Block.sandStone.blockID;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk loadChunk(int chunkX, int chunkZ) {
        return this.provideChunk(chunkX, chunkZ);
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.random.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        byte[] abyte = new byte[32768];
        Chunk chunk = new Chunk(this.world, abyte, chunkX, chunkZ);
        this.biomes = this.world.getWorldChunkManager().loadBlockGeneratorData(this.biomes, chunkX * 16, chunkZ * 16, 16, 16);
        double[] adouble = this.world.getWorldChunkManager().temperature;
        this.buildTerrain(chunkX, chunkZ, abyte, this.biomes, adouble);
        this.buildSurfaces(chunkX, chunkZ, abyte, this.biomes);
        this.cave.place(this, this.world, chunkX, chunkZ, abyte);
        chunk.populateHeightMap();
        return chunk;
    }

    private double[] generateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        if (heightMap == null) {
            heightMap = new double[sizeX * sizeY * sizeZ];
        }

        double d0 = 684.412;
        double d1 = 684.412;
        double[] adouble = this.world.getWorldChunkManager().temperature;
        double[] adouble1 = this.world.getWorldChunkManager().humidity;
        this.scaleNoiseBuffer = this.scaleNoise.getRegion(this.scaleNoiseBuffer, x, z, sizeX, sizeZ, 1.121, 1.121, 0.5);
        this.depthNoiseBuffer = this.depthNoise.getRegion(this.depthNoiseBuffer, x, z, sizeX, sizeZ, 200.0, 200.0, 0.5);
        d0 *= 2.0;
        this.perlinNoiseBuffer = this.perlinNoise1.generateNoiseOctaves(this.perlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0 / 80.0, d1 / 160.0, d0 / 80.0);
        this.minLimitPerlinNoiseBuffer = this.minLimitPerlinNoise
            .generateNoiseOctaves(this.minLimitPerlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0, d1, d0);
        this.maxLimitPerlinNoiseBuffer = this.maxLimitPerlinNoise
            .generateNoiseOctaves(this.maxLimitPerlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0, d1, d0);
        int i = 0;
        int j = 0;
        int k = 16 / sizeX;

        for (int l = 0; l < sizeX; l++) {
            int i1 = l * k + k / 2;

            for (int j1 = 0; j1 < sizeZ; j1++) {
                int k1 = j1 * k + k / 2;
                double d2 = adouble[i1 * 16 + k1];
                double d3 = adouble1[i1 * 16 + k1] * d2;
                double d4 = 1.0 - d3;
                d4 *= d4;
                d4 *= d4;
                d4 = 1.0 - d4;
                double d5 = (this.scaleNoiseBuffer[j] + 256.0) / 512.0;
                d5 *= d4;
                if (d5 > 1.0) {
                    d5 = 1.0;
                }

                double d6 = this.depthNoiseBuffer[j] / 8000.0;
                if (d6 < 0.0) {
                    d6 = -d6 * 0.3;
                }

                d6 = d6 * 3.0 - 2.0;
                if (d6 > 1.0) {
                    d6 = 1.0;
                }

                d6 /= 8.0;
                d6 = 0.0;
                if (d5 < 0.0) {
                    d5 = 0.0;
                }

                d5 += 0.5;
                d6 = d6 * sizeY / 16.0;
                j++;
                double d7 = sizeY / 2.0;

                for (int l1 = 0; l1 < sizeY; l1++) {
                    double d8 = 0.0;
                    double d9 = (l1 - d7) * 8.0 / d5;
                    if (d9 < 0.0) {
                        d9 *= -1.0;
                    }

                    double d10 = this.minLimitPerlinNoiseBuffer[i] / 512.0;
                    double d11 = this.maxLimitPerlinNoiseBuffer[i] / 512.0;
                    double d12 = (this.perlinNoiseBuffer[i] / 10.0 + 1.0) / 2.0;
                    if (d12 < 0.0) {
                        d8 = d10;
                    } else if (d12 > 1.0) {
                        d8 = d11;
                    } else {
                        d8 = d10 + (d11 - d10) * d12;
                    }

                    d8 -= 8.0;
                    byte b0 = 32;
                    if (l1 > sizeY - b0) {
                        double d13 = (l1 - (sizeY - b0)) / (b0 - 1.0F);
                        d8 = d8 * (1.0 - d13) + -30.0 * d13;
                    }

                    b0 = 8;
                    if (l1 < b0) {
                        double d14 = (b0 - l1) / (b0 - 1.0F);
                        d8 = d8 * (1.0 - d14) + -30.0 * d14;
                    }

                    heightMap[i] = d8;
                    i++;
                }
            }
        }

        return heightMap;
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return true;
    }

    @Override
    public void populate(IChunkProvider source, int chunkX, int chunkZ) {
        BlockSand.fallInstantly = true;
        int i = chunkX * 16;
        int j = chunkZ * 16;
        BiomeGenBase biomegenbase = this.world.getWorldChunkManager().getBiomeGenAt(i + 16, j + 16);
        this.random.setSeed(this.world.getRandomSeed());
        long k = this.random.nextLong() / 2L * 2L + 1L;
        long l = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed(chunkX * k + chunkZ * l ^ this.world.getRandomSeed());
        double d0 = 0.25;
        if (this.random.nextInt(4) == 0) {
            int i1 = i + this.random.nextInt(16) + 8;
            int j1 = this.random.nextInt(128);
            int k1 = j + this.random.nextInt(16) + 8;
            new WorldGenLakes(Block.waterStill.blockID).generate(this.world, this.random, i1, j1, k1);
        }

        if (this.random.nextInt(8) == 0) {
            int l2 = i + this.random.nextInt(16) + 8;
            int l5 = this.random.nextInt(this.random.nextInt(120) + 8);
            int l8 = j + this.random.nextInt(16) + 8;
            if (l5 < 64 || this.random.nextInt(10) == 0) {
                new WorldGenLakes(Block.lavaStill.blockID).generate(this.world, this.random, l2, l5, l8);
            }
        }

        for (int i3 = 0; i3 < 8; i3++) {
            int i6 = i + this.random.nextInt(16) + 8;
            int i9 = this.random.nextInt(128);
            int l1 = j + this.random.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.world, this.random, i6, i9, l1);
        }

        for (int j3 = 0; j3 < 10; j3++) {
            int j6 = i + this.random.nextInt(16);
            int j9 = this.random.nextInt(128);
            int j13 = j + this.random.nextInt(16);
            new WorldGenClay(32).generate(this.world, this.random, j6, j9, j13);
        }

        for (int k3 = 0; k3 < 20; k3++) {
            int k6 = i + this.random.nextInt(16);
            int k9 = this.random.nextInt(128);
            int k13 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.dirt.blockID, 32).generate(this.world, this.random, k6, k9, k13);
        }

        for (int l3 = 0; l3 < 10; l3++) {
            int l6 = i + this.random.nextInt(16);
            int l9 = this.random.nextInt(128);
            int l13 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.gravel.blockID, 32).generate(this.world, this.random, l6, l9, l13);
        }

        for (int i4 = 0; i4 < 20; i4++) {
            int i7 = i + this.random.nextInt(16);
            int i10 = this.random.nextInt(128);
            int i14 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreCoal.blockID, 16).generate(this.world, this.random, i7, i10, i14);
        }

        for (int j4 = 0; j4 < 20; j4++) {
            int j7 = i + this.random.nextInt(16);
            int j10 = this.random.nextInt(64);
            int j14 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreIron.blockID, 8).generate(this.world, this.random, j7, j10, j14);
        }

        for (int k4 = 0; k4 < 2; k4++) {
            int k7 = i + this.random.nextInt(16);
            int k10 = this.random.nextInt(32);
            int k14 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreGold.blockID, 8).generate(this.world, this.random, k7, k10, k14);
        }

        for (int l4 = 0; l4 < 8; l4++) {
            int l7 = i + this.random.nextInt(16);
            int l10 = this.random.nextInt(16);
            int l14 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreRedstone.blockID, 7).generate(this.world, this.random, l7, l10, l14);
        }

        for (int i5 = 0; i5 < 1; i5++) {
            int i8 = i + this.random.nextInt(16);
            int i11 = this.random.nextInt(16);
            int i15 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreDiamond.blockID, 7).generate(this.world, this.random, i8, i11, i15);
        }

        for (int j5 = 0; j5 < 1; j5++) {
            int j8 = i + this.random.nextInt(16);
            int j11 = this.random.nextInt(16) + this.random.nextInt(16);
            int j15 = j + this.random.nextInt(16);
            new WorldGenMinable(Block.oreLapis.blockID, 6).generate(this.world, this.random, j8, j11, j15);
        }

        d0 = 0.5;
        int k5 = (int)((this.forestNoise.getValue(i * d0, j * d0) / 8.0 + this.random.nextDouble() * 4.0 + 4.0) / 3.0);
        int k8 = 0;
        if (this.random.nextInt(10) == 0) {
            k8++;
        }

        if (biomegenbase == BiomeGenBase.forest) {
            k8 += k5 + 5;
        }

        if (biomegenbase == BiomeGenBase.rainforest) {
            k8 += k5 + 5;
        }

        if (biomegenbase == BiomeGenBase.seasonalForest) {
            k8 += k5 + 2;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            k8 += k5 + 5;
        }

        if (biomegenbase == BiomeGenBase.desert) {
            k8 -= 20;
        }

        if (biomegenbase == BiomeGenBase.tundra) {
            k8 -= 20;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            k8 -= 20;
        }

        for (int k11 = 0; k11 < k8; k11++) {
            int k15 = i + this.random.nextInt(16) + 8;
            int i2 = j + this.random.nextInt(16) + 8;
            WorldGenerator worldgenerator = biomegenbase.getRandomWorldGenForTrees(this.random);
            worldgenerator.prepare(1.0, 1.0, 1.0);
            worldgenerator.generate(this.world, this.random, k15, this.world.getHeightValue(k15, i2), i2);
        }

        for (int l11 = 0; l11 < 2; l11++) {
            int l15 = i + this.random.nextInt(16) + 8;
            int j18 = this.random.nextInt(128);
            int l20 = j + this.random.nextInt(16) + 8;
            new WorldGenFlowers(Block.plantYellow.blockID).generate(this.world, this.random, l15, j18, l20);
        }

        if (this.random.nextInt(2) == 0) {
            int i12 = i + this.random.nextInt(16) + 8;
            int i16 = this.random.nextInt(128);
            int k18 = j + this.random.nextInt(16) + 8;
            new WorldGenFlowers(Block.plantRed.blockID).generate(this.world, this.random, i12, i16, k18);
        }

        if (this.random.nextInt(4) == 0) {
            int j12 = i + this.random.nextInt(16) + 8;
            int j16 = this.random.nextInt(128);
            int l18 = j + this.random.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomBrown.blockID).generate(this.world, this.random, j12, j16, l18);
        }

        if (this.random.nextInt(8) == 0) {
            int k12 = i + this.random.nextInt(16) + 8;
            int k16 = this.random.nextInt(128);
            int i19 = j + this.random.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomRed.blockID).generate(this.world, this.random, k12, k16, i19);
        }

        for (int l12 = 0; l12 < 10; l12++) {
            int l16 = i + this.random.nextInt(16) + 8;
            int j19 = this.random.nextInt(128);
            int i21 = j + this.random.nextInt(16) + 8;
            new WorldGenReed().generate(this.world, this.random, l16, j19, i21);
        }

        if (this.random.nextInt(32) == 0) {
            int i13 = i + this.random.nextInt(16) + 8;
            int i17 = this.random.nextInt(128);
            int k19 = j + this.random.nextInt(16) + 8;
            new WorldGenPumpkin().generate(this.world, this.random, i13, i17, k19);
        }

        int b0 = 0;
        if (biomegenbase == BiomeGenBase.desert) {
            b0 += 10;
        }

        for (int j17 = 0; j17 < b0; j17++) {
            int l19 = i + this.random.nextInt(16) + 8;
            int j21 = this.random.nextInt(128);
            int j2 = j + this.random.nextInt(16) + 8;
            new WorldGenCactus().generate(this.world, this.random, l19, j21, j2);
        }

        for (int k17 = 0; k17 < 50; k17++) {
            int i20 = i + this.random.nextInt(16) + 8;
            int k21 = this.random.nextInt(this.random.nextInt(120) + 8);
            int j22 = j + this.random.nextInt(16) + 8;
            new WorldGenLiquids(Block.waterMoving.blockID).generate(this.world, this.random, i20, k21, j22);
        }

        for (int l17 = 0; l17 < 20; l17++) {
            int j20 = i + this.random.nextInt(16) + 8;
            int l21 = this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8);
            int k22 = j + this.random.nextInt(16) + 8;
            new WorldGenLiquids(Block.lavaMoving.blockID).generate(this.world, this.random, j20, l21, k22);
        }

        this.temperatures = this.world.getWorldChunkManager().getTemperatures(this.temperatures, i + 8, j + 8, 16, 16);

        for (int i18 = i + 8; i18 < i + 8 + 16; i18++) {
            for (int k20 = j + 8; k20 < j + 8 + 16; k20++) {
                int i22 = i18 - (i + 8);
                int l22 = k20 - (j + 8);
                int k2 = this.world.getTopSolidOrLiquidBlock(i18, k20);
                double d1 = this.temperatures[i22 * 16 + l22] - (k2 - 64) / 64.0 * 0.3;
                if (d1 < 0.5
                    && k2 > 0
                    && k2 < 128
                    && this.world.isAirBlock(i18, k2, k20)
                    && this.world.getBlockMaterial(i18, k2 - 1, k20).getIsSolid()
                    && this.world.getBlockMaterial(i18, k2 - 1, k20) != Material.ice) {
                    this.world.setBlockWithNotify(i18, k2, k20, Block.snow.blockID);
                }
            }
        }

        BlockSand.fallInstantly = false;
    }

    @Override
    public boolean saveChunks(boolean saveEntities, IProgressUpdate listener) {
        return true;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
