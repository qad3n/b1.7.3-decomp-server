package net.minecraft.src;

import java.util.Random;

public class ChunkProviderGenerate implements IChunkProvider {
    private Random rand;
    private NoiseGeneratorOctaves minLimitPerlinNoise;
    private NoiseGeneratorOctaves maxLimitPerlinNoise;
    private NoiseGeneratorOctaves perlinNoise1;
    private NoiseGeneratorOctaves perlinNoise2;
    private NoiseGeneratorOctaves perlinNoise3;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private World worldObj;
    private double[] heightMap;
    private double[] sandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    private double[] stoneNoise = new double[256];
    private MapGenBase cave = new MapGenCaves();
    private BiomeGenBase[] biomesForGeneration;
    double[] perlinNoiseBuffer;
    double[] minLimitPerlinNoiseBuffer;
    double[] maxLimitPerlinNoiseBuffer;
    double[] scaleNoiseBuffer;
    double[] depthNoiseBuffer;
    int[][] waterDepths = new int[32][32];
    private double[] generatedTemperatures;

    public ChunkProviderGenerate(World world, long seed) {
        this.worldObj = world;
        this.rand = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.rand, 8);
        this.perlinNoise2 = new NoiseGeneratorOctaves(this.rand, 4);
        this.perlinNoise3 = new NoiseGeneratorOctaves(this.rand, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    public void generateTerrain(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomes, double[] temperatures) {
        byte b0 = 4;
        byte b1 = 64;
        int i = b0 + 1;
        byte b2 = 17;
        int j = b0 + 1;
        this.heightMap = this.generateHeightMap(this.heightMap, chunkX * b0, 0, chunkZ * b0, i, b2, j);

        for (int k = 0; k < b0; k++) {
            for (int l = 0; l < b0; l++) {
                for (int i1 = 0; i1 < 16; i1++) {
                    double d0 = 0.125;
                    double d1 = this.heightMap[((k + 0) * j + l + 0) * b2 + i1 + 0];
                    double d2 = this.heightMap[((k + 0) * j + l + 1) * b2 + i1 + 0];
                    double d3 = this.heightMap[((k + 1) * j + l + 0) * b2 + i1 + 0];
                    double d4 = this.heightMap[((k + 1) * j + l + 1) * b2 + i1 + 0];
                    double d5 = (this.heightMap[((k + 0) * j + l + 0) * b2 + i1 + 1] - d1) * d0;
                    double d6 = (this.heightMap[((k + 0) * j + l + 1) * b2 + i1 + 1] - d2) * d0;
                    double d7 = (this.heightMap[((k + 1) * j + l + 0) * b2 + i1 + 1] - d3) * d0;
                    double d8 = (this.heightMap[((k + 1) * j + l + 1) * b2 + i1 + 1] - d4) * d0;

                    for (int j1 = 0; j1 < 8; j1++) {
                        double d9 = 0.25;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int k1 = 0; k1 < 4; k1++) {
                            int l1 = k1 + k * 4 << 11 | 0 + l * 4 << 7 | i1 * 8 + j1;
                            short short1 = 128;
                            double d14 = 0.25;
                            double d15 = d10;
                            double d16 = (d11 - d10) * d14;

                            for (int i2 = 0; i2 < 4; i2++) {
                                double d17 = temperatures[(k * 4 + k1) * 16 + l * 4 + i2];
                                int j2 = 0;
                                if (i1 * 8 + j1 < b1) {
                                    if (d17 < 0.5 && i1 * 8 + j1 >= b1 - 1) {
                                        j2 = Block.ice.blockID;
                                    } else {
                                        j2 = Block.waterStill.blockID;
                                    }
                                }

                                if (d15 > 0.0) {
                                    j2 = Block.stone.blockID;
                                }

                                blocks[l1] = (byte)j2;
                                l1 += short1;
                                d15 += d16;
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, BiomeGenBase[] biomes) {
        byte b0 = 64;
        double d0 = 0.03125;
        this.sandNoise = this.perlinNoise2.generateNoiseOctaves(this.sandNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0, d0, 1.0);
        this.gravelNoise = this.perlinNoise2.generateNoiseOctaves(this.gravelNoise, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, d0, 1.0, d0);
        this.stoneNoise = this.perlinNoise3.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0 * 2.0, d0 * 2.0, d0 * 2.0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                BiomeGenBase biomegenbase = biomes[i + j * 16];
                boolean flag = this.sandNoise[i + j * 16] + this.rand.nextDouble() * 0.2 > 0.0;
                boolean flag1 = this.gravelNoise[i + j * 16] + this.rand.nextDouble() * 0.2 > 3.0;
                int k = (int)(this.stoneNoise[i + j * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
                int l = -1;
                byte b1 = biomegenbase.topBlock;
                byte b2 = biomegenbase.fillerBlock;

                for (int i1 = 127; i1 >= 0; i1--) {
                    int j1 = (j * 16 + i) * 128 + i1;
                    if (i1 <= 0 + this.rand.nextInt(5)) {
                        blocks[j1] = (byte)Block.bedrock.blockID;
                    } else {
                        byte b3 = blocks[j1];
                        if (b3 == 0) {
                            l = -1;
                        } else if (b3 == Block.stone.blockID) {
                            if (l == -1) {
                                if (k <= 0) {
                                    b1 = 0;
                                    b2 = (byte)Block.stone.blockID;
                                } else if (i1 >= b0 - 4 && i1 <= b0 + 1) {
                                    b1 = biomegenbase.topBlock;
                                    b2 = biomegenbase.fillerBlock;
                                    if (flag1) {
                                        b1 = 0;
                                    }

                                    if (flag1) {
                                        b2 = (byte)Block.gravel.blockID;
                                    }

                                    if (flag) {
                                        b1 = (byte)Block.sand.blockID;
                                    }

                                    if (flag) {
                                        b2 = (byte)Block.sand.blockID;
                                    }
                                }

                                if (i1 < b0 && b1 == 0) {
                                    b1 = (byte)Block.waterStill.blockID;
                                }

                                l = k;
                                if (i1 >= b0 - 1) {
                                    blocks[j1] = b1;
                                } else {
                                    blocks[j1] = b2;
                                }
                            } else if (l > 0) {
                                l--;
                                blocks[j1] = b2;
                                if (l == 0 && b2 == Block.sand.blockID) {
                                    l = this.rand.nextInt(4);
                                    b2 = (byte)Block.sandStone.blockID;
                                }
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
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        byte[] abyte = new byte[32768];
        Chunk chunk = new Chunk(this.worldObj, abyte, chunkX, chunkZ);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        double[] adouble = this.worldObj.getWorldChunkManager().temperature;
        this.generateTerrain(chunkX, chunkZ, abyte, this.biomesForGeneration, adouble);
        this.replaceBlocksForBiome(chunkX, chunkZ, abyte, this.biomesForGeneration);
        this.cave.place(this, this.worldObj, chunkX, chunkZ, abyte);
        chunk.populateHeightMap();
        return chunk;
    }

    private double[] generateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        if (heightMap == null) {
            heightMap = new double[sizeX * sizeY * sizeZ];
        }

        double d0 = 684.412;
        double d1 = 684.412;
        double[] adouble = this.worldObj.getWorldChunkManager().temperature;
        double[] adouble1 = this.worldObj.getWorldChunkManager().humidity;
        this.scaleNoiseBuffer = this.scaleNoise.getRegion(this.scaleNoiseBuffer, x, z, sizeX, sizeZ, 1.121, 1.121, 0.5);
        this.depthNoiseBuffer = this.depthNoise.getRegion(this.depthNoiseBuffer, x, z, sizeX, sizeZ, 200.0, 200.0, 0.5);
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
                if (d6 < 0.0) {
                    d6 /= 2.0;
                    if (d6 < -1.0) {
                        d6 = -1.0;
                    }

                    d6 /= 1.4;
                    d6 /= 2.0;
                    d5 = 0.0;
                } else {
                    if (d6 > 1.0) {
                        d6 = 1.0;
                    }

                    d6 /= 8.0;
                }

                if (d5 < 0.0) {
                    d5 = 0.0;
                }

                d5 += 0.5;
                d6 = d6 * sizeY / 16.0;
                double d7 = sizeY / 2.0 + d6 * 4.0;
                j++;

                for (int l1 = 0; l1 < sizeY; l1++) {
                    double d8 = 0.0;
                    double d9 = (l1 - d7) * 12.0 / d5;
                    if (d9 < 0.0) {
                        d9 *= 4.0;
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

                    d8 -= d9;
                    if (l1 > sizeY - 4) {
                        double d13 = (l1 - (sizeY - 4)) / 3.0F;
                        d8 = d8 * (1.0 - d13) + -10.0 * d13;
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
        BiomeGenBase biomegenbase = this.worldObj.getWorldChunkManager().getBiomeGenAt(i + 16, j + 16);
        this.rand.setSeed(this.worldObj.getRandomSeed());
        long k = this.rand.nextLong() / 2L * 2L + 1L;
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed(chunkX * k + chunkZ * l ^ this.worldObj.getRandomSeed());
        double d0 = 0.25;
        if (this.rand.nextInt(4) == 0) {
            int i1 = i + this.rand.nextInt(16) + 8;
            int j1 = this.rand.nextInt(128);
            int k1 = j + this.rand.nextInt(16) + 8;
            new WorldGenLakes(Block.waterStill.blockID).generate(this.worldObj, this.rand, i1, j1, k1);
        }

        if (this.rand.nextInt(8) == 0) {
            int j3 = i + this.rand.nextInt(16) + 8;
            int j6 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            int j9 = j + this.rand.nextInt(16) + 8;
            if (j6 < 64 || this.rand.nextInt(10) == 0) {
                new WorldGenLakes(Block.lavaStill.blockID).generate(this.worldObj, this.rand, j3, j6, j9);
            }
        }

        for (int k3 = 0; k3 < 8; k3++) {
            int k6 = i + this.rand.nextInt(16) + 8;
            int k9 = this.rand.nextInt(128);
            int l1 = j + this.rand.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.worldObj, this.rand, k6, k9, l1);
        }

        for (int l3 = 0; l3 < 10; l3++) {
            int l6 = i + this.rand.nextInt(16);
            int l9 = this.rand.nextInt(128);
            int j12 = j + this.rand.nextInt(16);
            new WorldGenClay(32).generate(this.worldObj, this.rand, l6, l9, j12);
        }

        for (int i4 = 0; i4 < 20; i4++) {
            int i7 = i + this.rand.nextInt(16);
            int i10 = this.rand.nextInt(128);
            int k12 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.dirt.blockID, 32).generate(this.worldObj, this.rand, i7, i10, k12);
        }

        for (int j4 = 0; j4 < 10; j4++) {
            int j7 = i + this.rand.nextInt(16);
            int j10 = this.rand.nextInt(128);
            int l12 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.gravel.blockID, 32).generate(this.worldObj, this.rand, j7, j10, l12);
        }

        for (int k4 = 0; k4 < 20; k4++) {
            int k7 = i + this.rand.nextInt(16);
            int k10 = this.rand.nextInt(128);
            int i13 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreCoal.blockID, 16).generate(this.worldObj, this.rand, k7, k10, i13);
        }

        for (int l4 = 0; l4 < 20; l4++) {
            int l7 = i + this.rand.nextInt(16);
            int l10 = this.rand.nextInt(64);
            int j13 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreIron.blockID, 8).generate(this.worldObj, this.rand, l7, l10, j13);
        }

        for (int i5 = 0; i5 < 2; i5++) {
            int i8 = i + this.rand.nextInt(16);
            int i11 = this.rand.nextInt(32);
            int k13 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreGold.blockID, 8).generate(this.worldObj, this.rand, i8, i11, k13);
        }

        for (int j5 = 0; j5 < 8; j5++) {
            int j8 = i + this.rand.nextInt(16);
            int j11 = this.rand.nextInt(16);
            int l13 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreRedstone.blockID, 7).generate(this.worldObj, this.rand, j8, j11, l13);
        }

        for (int k5 = 0; k5 < 1; k5++) {
            int k8 = i + this.rand.nextInt(16);
            int k11 = this.rand.nextInt(16);
            int i14 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreDiamond.blockID, 7).generate(this.worldObj, this.rand, k8, k11, i14);
        }

        for (int l5 = 0; l5 < 1; l5++) {
            int l8 = i + this.rand.nextInt(16);
            int l11 = this.rand.nextInt(16) + this.rand.nextInt(16);
            int j14 = j + this.rand.nextInt(16);
            new WorldGenMinable(Block.oreLapis.blockID, 6).generate(this.worldObj, this.rand, l8, l11, j14);
        }

        d0 = 0.5;
        int i6 = (int)((this.mobSpawnerNoise.getValue(i * d0, j * d0) / 8.0 + this.rand.nextDouble() * 4.0 + 4.0) / 3.0);
        int i9 = 0;
        if (this.rand.nextInt(10) == 0) {
            i9++;
        }

        if (biomegenbase == BiomeGenBase.forest) {
            i9 += i6 + 5;
        }

        if (biomegenbase == BiomeGenBase.rainforest) {
            i9 += i6 + 5;
        }

        if (biomegenbase == BiomeGenBase.seasonalForest) {
            i9 += i6 + 2;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            i9 += i6 + 5;
        }

        if (biomegenbase == BiomeGenBase.desert) {
            i9 -= 20;
        }

        if (biomegenbase == BiomeGenBase.tundra) {
            i9 -= 20;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            i9 -= 20;
        }

        for (int i12 = 0; i12 < i9; i12++) {
            int k14 = i + this.rand.nextInt(16) + 8;
            int i2 = j + this.rand.nextInt(16) + 8;
            WorldGenerator worldgenerator = biomegenbase.getRandomWorldGenForTrees(this.rand);
            worldgenerator.prepare(1.0, 1.0, 1.0);
            worldgenerator.generate(this.worldObj, this.rand, k14, this.worldObj.getHeightValue(k14, i2), i2);
        }

        byte b0 = 0;
        if (biomegenbase == BiomeGenBase.forest) {
            b0 = 2;
        }

        if (biomegenbase == BiomeGenBase.seasonalForest) {
            b0 = 4;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            b0 = 2;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            b0 = 3;
        }

        for (int l14 = 0; l14 < b0; l14++) {
            int i15 = i + this.rand.nextInt(16) + 8;
            int i17 = this.rand.nextInt(128);
            int j2 = j + this.rand.nextInt(16) + 8;
            new WorldGenFlowers(Block.plantYellow.blockID).generate(this.worldObj, this.rand, i15, i17, j2);
        }

        byte b1 = 0;
        if (biomegenbase == BiomeGenBase.forest) {
            b1 = 2;
        }

        if (biomegenbase == BiomeGenBase.rainforest) {
            b1 = 10;
        }

        if (biomegenbase == BiomeGenBase.seasonalForest) {
            b1 = 2;
        }

        if (biomegenbase == BiomeGenBase.taiga) {
            b1 = 1;
        }

        if (biomegenbase == BiomeGenBase.plains) {
            b1 = 10;
        }

        for (int j15 = 0; j15 < b1; j15++) {
            byte b3 = 1;
            if (biomegenbase == BiomeGenBase.rainforest && this.rand.nextInt(3) != 0) {
                b3 = 2;
            }

            int l19 = i + this.rand.nextInt(16) + 8;
            int k2 = this.rand.nextInt(128);
            int l2 = j + this.rand.nextInt(16) + 8;
            new WorldGenTallGrass(Block.tallGrass.blockID, b3).generate(this.worldObj, this.rand, l19, k2, l2);
        }

        b1 = 0;
        if (biomegenbase == BiomeGenBase.desert) {
            b1 = 2;
        }

        for (int k15 = 0; k15 < b1; k15++) {
            int j17 = i + this.rand.nextInt(16) + 8;
            int i20 = this.rand.nextInt(128);
            int k22 = j + this.rand.nextInt(16) + 8;
            new WorldGenDeadBush(Block.deadBush.blockID).generate(this.worldObj, this.rand, j17, i20, k22);
        }

        if (this.rand.nextInt(2) == 0) {
            int l15 = i + this.rand.nextInt(16) + 8;
            int k17 = this.rand.nextInt(128);
            int j20 = j + this.rand.nextInt(16) + 8;
            new WorldGenFlowers(Block.plantRed.blockID).generate(this.worldObj, this.rand, l15, k17, j20);
        }

        if (this.rand.nextInt(4) == 0) {
            int i16 = i + this.rand.nextInt(16) + 8;
            int l17 = this.rand.nextInt(128);
            int k20 = j + this.rand.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomBrown.blockID).generate(this.worldObj, this.rand, i16, l17, k20);
        }

        if (this.rand.nextInt(8) == 0) {
            int j16 = i + this.rand.nextInt(16) + 8;
            int i18 = this.rand.nextInt(128);
            int l20 = j + this.rand.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomRed.blockID).generate(this.worldObj, this.rand, j16, i18, l20);
        }

        for (int k16 = 0; k16 < 10; k16++) {
            int j18 = i + this.rand.nextInt(16) + 8;
            int i21 = this.rand.nextInt(128);
            int l22 = j + this.rand.nextInt(16) + 8;
            new WorldGenReed().generate(this.worldObj, this.rand, j18, i21, l22);
        }

        if (this.rand.nextInt(32) == 0) {
            int l16 = i + this.rand.nextInt(16) + 8;
            int k18 = this.rand.nextInt(128);
            int j21 = j + this.rand.nextInt(16) + 8;
            new WorldGenPumpkin().generate(this.worldObj, this.rand, l16, k18, j21);
        }

        int b2 = 0;
        if (biomegenbase == BiomeGenBase.desert) {
            b2 += 10;
        }

        for (int l18 = 0; l18 < b2; l18++) {
            int k21 = i + this.rand.nextInt(16) + 8;
            int i23 = this.rand.nextInt(128);
            int i24 = j + this.rand.nextInt(16) + 8;
            new WorldGenCactus().generate(this.worldObj, this.rand, k21, i23, i24);
        }

        for (int i19 = 0; i19 < 50; i19++) {
            int l21 = i + this.rand.nextInt(16) + 8;
            int j23 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            int j24 = j + this.rand.nextInt(16) + 8;
            new WorldGenLiquids(Block.waterMoving.blockID).generate(this.worldObj, this.rand, l21, j23, j24);
        }

        for (int j19 = 0; j19 < 20; j19++) {
            int i22 = i + this.rand.nextInt(16) + 8;
            int k23 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(112) + 8) + 8);
            int k24 = j + this.rand.nextInt(16) + 8;
            new WorldGenLiquids(Block.lavaMoving.blockID).generate(this.worldObj, this.rand, i22, k23, k24);
        }

        this.generatedTemperatures = this.worldObj.getWorldChunkManager().getTemperatures(this.generatedTemperatures, i + 8, j + 8, 16, 16);

        for (int k19 = i + 8; k19 < i + 8 + 16; k19++) {
            for (int j22 = j + 8; j22 < j + 8 + 16; j22++) {
                int l23 = k19 - (i + 8);
                int l24 = j22 - (j + 8);
                int i3 = this.worldObj.getTopSolidOrLiquidBlock(k19, j22);
                double d1 = this.generatedTemperatures[l23 * 16 + l24] - (i3 - 64) / 64.0 * 0.3;
                if (d1 < 0.5
                    && i3 > 0
                    && i3 < 128
                    && this.worldObj.isAirBlock(k19, i3, j22)
                    && this.worldObj.getBlockMaterial(k19, i3 - 1, j22).getIsSolid()
                    && this.worldObj.getBlockMaterial(k19, i3 - 1, j22) != Material.ice) {
                    this.worldObj.setBlockWithNotify(k19, i3, j22, Block.snow.blockID);
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
