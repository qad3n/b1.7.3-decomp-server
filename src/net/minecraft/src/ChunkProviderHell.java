package net.minecraft.src;

import java.util.Random;

public class ChunkProviderHell implements IChunkProvider {
    private Random hellRNG;
    private NoiseGeneratorOctaves minLimitPerlinNoise;
    private NoiseGeneratorOctaves maxLimitPerlinNoise;
    private NoiseGeneratorOctaves perlinNoise1;
    private NoiseGeneratorOctaves perlinNoise2;
    private NoiseGeneratorOctaves perlinNoise3;
    public NoiseGeneratorOctaves scaleNoise;
    public NoiseGeneratorOctaves depthNoise;
    private World world;
    private double[] heightMap;
    private double[] sandBuffer = new double[256];
    private double[] gravelBuffer = new double[256];
    private double[] depthBuffer = new double[256];
    private MapGenBase cave = new MapGenCavesHell();
    double[] perlinNoiseBuffer;
    double[] minLimitPerlinNoiseBuffer;
    double[] maxLimitPerlinNoiseBuffer;
    double[] scaleNoiseBuffer;
    double[] depthNoiseBuffer;

    public ChunkProviderHell(World world, long seed) {
        this.world = world;
        this.hellRNG = new Random(seed);
        this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.hellRNG, 16);
        this.perlinNoise1 = new NoiseGeneratorOctaves(this.hellRNG, 8);
        this.perlinNoise2 = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.perlinNoise3 = new NoiseGeneratorOctaves(this.hellRNG, 4);
        this.scaleNoise = new NoiseGeneratorOctaves(this.hellRNG, 10);
        this.depthNoise = new NoiseGeneratorOctaves(this.hellRNG, 16);
    }

    public void buildTerrain(int chunkX, int chunkZ, byte[] blocks) {
        int n = 4;
        int n2 = 32;
        int n3 = n + 1;
        int n4 = 17;
        int n5 = n + 1;
        this.heightMap = this.generateHeightMap(this.heightMap, chunkX * n, 0, chunkZ * n, n3, n4, n5);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                for (int k = 0; k < 16; ++k) {
                    double d = 0.125;
                    double d2 = this.heightMap[((i + 0) * n5 + (j + 0)) * n4 + (k + 0)];
                    double d3 = this.heightMap[((i + 0) * n5 + (j + 1)) * n4 + (k + 0)];
                    double d4 = this.heightMap[((i + 1) * n5 + (j + 0)) * n4 + (k + 0)];
                    double d5 = this.heightMap[((i + 1) * n5 + (j + 1)) * n4 + (k + 0)];
                    double d6 = (this.heightMap[((i + 0) * n5 + (j + 0)) * n4 + (k + 1)] - d2) * d;
                    double d7 = (this.heightMap[((i + 0) * n5 + (j + 1)) * n4 + (k + 1)] - d3) * d;
                    double d8 = (this.heightMap[((i + 1) * n5 + (j + 0)) * n4 + (k + 1)] - d4) * d;
                    double d9 = (this.heightMap[((i + 1) * n5 + (j + 1)) * n4 + (k + 1)] - d5) * d;
                    for (int i2 = 0; i2 < 8; ++i2) {
                        double d10 = 0.25;
                        double d11 = d2;
                        double d12 = d3;
                        double d13 = (d4 - d2) * d10;
                        double d14 = (d5 - d3) * d10;
                        for (int i3 = 0; i3 < 4; ++i3) {
                            int n6 = i3 + i * 4 << 11 | 0 + j * 4 << 7 | k * 8 + i2;
                            int n7 = 128;
                            double d15 = 0.25;
                            double d16 = d11;
                            double d17 = (d12 - d11) * d15;
                            for (int i4 = 0; i4 < 4; ++i4) {
                                int n8 = 0;
                                if (k * 8 + i2 < n2) {
                                    n8 = Block.lavaStill.blockID;
                                }
                                if (d16 > 0.0) {
                                    n8 = Block.bloodStone.blockID;
                                }
                                blocks[n6] = (byte) n8;
                                n6 += n7;
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

    public void buildSurfaces(int chunkX, int chunkZ, byte[] blocks) {
        byte b0 = 64;
        double d0 = 0.03125;
        this.sandBuffer = this.perlinNoise2.generateNoiseOctaves(this.sandBuffer, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0, d0, 1.0);
        this.gravelBuffer = this.perlinNoise2.generateNoiseOctaves(this.gravelBuffer, chunkX * 16, 109.0134, chunkZ * 16, 16, 1, 16, d0, 1.0, d0);
        this.depthBuffer = this.perlinNoise3.generateNoiseOctaves(this.depthBuffer, chunkX * 16, chunkZ * 16, 0.0, 16, 16, 1, d0 * 2.0, d0 * 2.0, d0 * 2.0);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                boolean flag = this.sandBuffer[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                boolean flag1 = this.gravelBuffer[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
                int k = (int)(this.depthBuffer[i + j * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
                int l = -1;
                byte b1 = (byte)Block.bloodStone.blockID;
                byte b2 = (byte)Block.bloodStone.blockID;

                for (int i1 = 127; i1 >= 0; i1--) {
                    int j1 = (j * 16 + i) * 128 + i1;
                    if (i1 >= 127 - this.hellRNG.nextInt(5)) {
                        blocks[j1] = (byte)Block.bedrock.blockID;
                    } else if (i1 <= 0 + this.hellRNG.nextInt(5)) {
                        blocks[j1] = (byte)Block.bedrock.blockID;
                    } else {
                        byte b3 = blocks[j1];
                        if (b3 == 0) {
                            l = -1;
                        } else if (b3 == Block.bloodStone.blockID) {
                            if (l == -1) {
                                if (k <= 0) {
                                    b1 = 0;
                                    b2 = (byte)Block.bloodStone.blockID;
                                } else if (i1 >= b0 - 4 && i1 <= b0 + 1) {
                                    b1 = (byte)Block.bloodStone.blockID;
                                    b2 = (byte)Block.bloodStone.blockID;
                                    if (flag1) {
                                        b1 = (byte)Block.gravel.blockID;
                                    }

                                    if (flag1) {
                                        b2 = (byte)Block.bloodStone.blockID;
                                    }

                                    if (flag) {
                                        b1 = (byte)Block.slowSand.blockID;
                                    }

                                    if (flag) {
                                        b2 = (byte)Block.slowSand.blockID;
                                    }
                                }

                                if (i1 < b0 && b1 == 0) {
                                    b1 = (byte)Block.lavaStill.blockID;
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

    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.hellRNG.setSeed((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
        byte[] byArray = new byte[32768];
        this.buildTerrain(chunkX, chunkZ, byArray);
        this.buildSurfaces(chunkX, chunkZ, byArray);
        this.cave.place(this, this.world, chunkX, chunkZ, byArray);
        Chunk chunk = new Chunk(this.world, byArray, chunkX, chunkZ);
        return chunk;
    }

    private double[] generateHeightMap(double[] heightMap, int x, int y, int z, int sizeX, int sizeY, int sizeZ) {
        if (heightMap == null) {
            heightMap = new double[sizeX * sizeY * sizeZ];
        }

        double d0 = 684.412;
        double d1 = 2053.236;
        this.scaleNoiseBuffer = this.scaleNoise.generateNoiseOctaves(this.scaleNoiseBuffer, x, y, z, sizeX, 1, sizeZ, 1.0, 0.0, 1.0);
        this.depthNoiseBuffer = this.depthNoise.generateNoiseOctaves(this.depthNoiseBuffer, x, y, z, sizeX, 1, sizeZ, 100.0, 0.0, 100.0);
        this.perlinNoiseBuffer = this.perlinNoise1.generateNoiseOctaves(this.perlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0 / 80.0, d1 / 60.0, d0 / 80.0);
        this.minLimitPerlinNoiseBuffer = this.minLimitPerlinNoise
            .generateNoiseOctaves(this.minLimitPerlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0, d1, d0);
        this.maxLimitPerlinNoiseBuffer = this.maxLimitPerlinNoise
            .generateNoiseOctaves(this.maxLimitPerlinNoiseBuffer, x, y, z, sizeX, sizeY, sizeZ, d0, d1, d0);
        int i = 0;
        int j = 0;
        double[] adouble = new double[sizeY];

        for (int k = 0; k < sizeY; k++) {
            adouble[k] = Math.cos(k * Math.PI * 6.0 / sizeY) * 2.0;
            double d2 = k;
            if (k > sizeY / 2) {
                d2 = sizeY - 1 - k;
            }

            if (d2 < 4.0) {
                d2 = 4.0 - d2;
                adouble[k] -= d2 * d2 * d2 * 10.0;
            }
        }

        for (int i1 = 0; i1 < sizeX; i1++) {
            for (int j1 = 0; j1 < sizeZ; j1++) {
                double d3 = (this.scaleNoiseBuffer[j] + 256.0) / 512.0;
                if (d3 > 1.0) {
                    d3 = 1.0;
                }

                double d4 = 0.0;
                double d5 = this.depthNoiseBuffer[j] / 8000.0;
                if (d5 < 0.0) {
                    d5 = -d5;
                }

                d5 = d5 * 3.0 - 3.0;
                if (d5 < 0.0) {
                    d5 /= 2.0;
                    if (d5 < -1.0) {
                        d5 = -1.0;
                    }

                    d5 /= 1.4;
                    d5 /= 2.0;
                    d3 = 0.0;
                } else {
                    if (d5 > 1.0) {
                        d5 = 1.0;
                    }

                    d5 /= 6.0;
                }

                d3 += 0.5;
                d5 = d5 * sizeY / 16.0;
                j++;

                for (int l = 0; l < sizeY; l++) {
                    double d6 = 0.0;
                    double d7 = adouble[l];
                    double d8 = this.minLimitPerlinNoiseBuffer[i] / 512.0;
                    double d9 = this.maxLimitPerlinNoiseBuffer[i] / 512.0;
                    double d10 = (this.perlinNoiseBuffer[i] / 10.0 + 1.0) / 2.0;
                    if (d10 < 0.0) {
                        d6 = d8;
                    } else if (d10 > 1.0) {
                        d6 = d9;
                    } else {
                        d6 = d8 + (d9 - d8) * d10;
                    }

                    d6 -= d7;
                    if (l > sizeY - 4) {
                        double d11 = (l - (sizeY - 4)) / 3.0F;
                        d6 = d6 * (1.0 - d11) + -10.0 * d11;
                    }

                    if (l < d4) {
                        double d12 = (d4 - l) / 4.0;
                        if (d12 < 0.0) {
                            d12 = 0.0;
                        }

                        if (d12 > 1.0) {
                            d12 = 1.0;
                        }

                        d6 = d6 * (1.0 - d12) + -10.0 * d12;
                    }

                    heightMap[i] = d6;
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

        for (int k = 0; k < 8; k++) {
            int l = i + this.hellRNG.nextInt(16) + 8;
            int i1 = this.hellRNG.nextInt(120) + 4;
            int j1 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenHellLava(Block.lavaMoving.blockID).generate(this.world, this.hellRNG, l, i1, j1);
        }

        int l1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1;

        for (int i2 = 0; i2 < l1; i2++) {
            int j3 = i + this.hellRNG.nextInt(16) + 8;
            int k4 = this.hellRNG.nextInt(120) + 4;
            int k1 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenFire().generate(this.world, this.hellRNG, j3, k4, k1);
        }

        l1 = this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1);

        for (int j2 = 0; j2 < l1; j2++) {
            int k3 = i + this.hellRNG.nextInt(16) + 8;
            int l4 = this.hellRNG.nextInt(120) + 4;
            int l5 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenGlowStone1().generate(this.world, this.hellRNG, k3, l4, l5);
        }

        for (int k2 = 0; k2 < 10; k2++) {
            int l3 = i + this.hellRNG.nextInt(16) + 8;
            int i5 = this.hellRNG.nextInt(128);
            int i6 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenGlowStone2().generate(this.world, this.hellRNG, l3, i5, i6);
        }

        if (this.hellRNG.nextInt(1) == 0) {
            int l2 = i + this.hellRNG.nextInt(16) + 8;
            int i4 = this.hellRNG.nextInt(128);
            int j5 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomBrown.blockID).generate(this.world, this.hellRNG, l2, i4, j5);
        }

        if (this.hellRNG.nextInt(1) == 0) {
            int i3 = i + this.hellRNG.nextInt(16) + 8;
            int j4 = this.hellRNG.nextInt(128);
            int k5 = j + this.hellRNG.nextInt(16) + 8;
            new WorldGenFlowers(Block.mushroomRed.blockID).generate(this.world, this.hellRNG, i3, j4, k5);
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
