package net.minecraft.src;

import java.util.Random;

public class MapGenCavesHell extends MapGenBase {
    protected void carveRoom(int chunkX, int chunkZ, byte[] blocks, double x, double y, double z) {
        this.carveTunnel(chunkX, chunkZ, blocks, x, y, z, 1.0F + this.rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
    }

    protected void carveTunnel(
        int chunkX,
        int chunkZ,
        byte[] blocks,
        double x,
        double y,
        double z,
        float baseWidth,
        float yaw,
        float pitch,
        int tunnel,
        int tunnelCount,
        double widthHeightRatio
    ) {
        double d0 = chunkX * 16 + 8;
        double d1 = chunkZ * 16 + 8;
        float f = 0.0F;
        float f1 = 0.0F;
        Random random = new Random(this.rand.nextLong());
        if (tunnelCount <= 0) {
            int i = this.range * 16 - 16;
            tunnelCount = i - random.nextInt(i / 4);
        }

        boolean flag1 = false;
        if (tunnel == -1) {
            tunnel = tunnelCount / 2;
            flag1 = true;
        }

        int j = random.nextInt(tunnelCount / 2) + tunnelCount / 4;
        boolean flag = random.nextInt(6) == 0;

        while (tunnel < tunnelCount) {
            double d2 = 1.5 + MathHelper.sin(tunnel * (float) Math.PI / tunnelCount) * baseWidth * 1.0F;
            double d3 = d2 * widthHeightRatio;
            float f2 = MathHelper.cos(pitch);
            float f3 = MathHelper.sin(pitch);
            x += MathHelper.cos(yaw) * f2;
            y += f3;
            z += MathHelper.sin(yaw) * f2;
            if (flag) {
                pitch *= 0.92F;
            } else {
                pitch *= 0.7F;
            }

            pitch += f1 * 0.1F;
            yaw += f * 0.1F;
            f1 *= 0.9F;
            f *= 0.75F;
            f1 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (!flag1 && tunnel == j && baseWidth > 1.0F) {
                this.carveTunnel(
                    chunkX, chunkZ, blocks, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw - (float) (Math.PI / 2), pitch / 3.0F, tunnel, tunnelCount, 1.0
                );
                this.carveTunnel(
                    chunkX, chunkZ, blocks, x, y, z, random.nextFloat() * 0.5F + 0.5F, yaw + (float) (Math.PI / 2), pitch / 3.0F, tunnel, tunnelCount, 1.0
                );
                return;
            }

            if (flag1 || random.nextInt(4) != 0) {
                double d4 = x - d0;
                double d5 = z - d1;
                double d6 = tunnelCount - tunnel;
                double d7 = baseWidth + 2.0F + 16.0F;
                if (d4 * d4 + d5 * d5 - d6 * d6 > d7 * d7) {
                    return;
                }

                if (!(x < d0 - 16.0 - d2 * 2.0) && !(z < d1 - 16.0 - d2 * 2.0) && !(x > d0 + 16.0 + d2 * 2.0) && !(z > d1 + 16.0 + d2 * 2.0)) {
                    int l2 = MathHelper.floor_double(x - d2) - chunkX * 16 - 1;
                    int k = MathHelper.floor_double(x + d2) - chunkX * 16 + 1;
                    int i3 = MathHelper.floor_double(y - d3) - 1;
                    int l = MathHelper.floor_double(y + d3) + 1;
                    int j3 = MathHelper.floor_double(z - d2) - chunkZ * 16 - 1;
                    int i1 = MathHelper.floor_double(z + d2) - chunkZ * 16 + 1;
                    if (l2 < 0) {
                        l2 = 0;
                    }

                    if (k > 16) {
                        k = 16;
                    }

                    if (i3 < 1) {
                        i3 = 1;
                    }

                    if (l > 120) {
                        l = 120;
                    }

                    if (j3 < 0) {
                        j3 = 0;
                    }

                    if (i1 > 16) {
                        i1 = 16;
                    }

                    boolean flag2 = false;

                    for (int j1 = l2; !flag2 && j1 < k; j1++) {
                        for (int k1 = j3; !flag2 && k1 < i1; k1++) {
                            for (int l1 = l + 1; !flag2 && l1 >= i3 - 1; l1--) {
                                int i2 = (j1 * 16 + k1) * 128 + l1;
                                if (l1 >= 0 && l1 < 128) {
                                    if (blocks[i2] == Block.lavaMoving.blockID || blocks[i2] == Block.lavaStill.blockID) {
                                        flag2 = true;
                                    }

                                    if (l1 != i3 - 1 && j1 != l2 && j1 != k - 1 && k1 != j3 && k1 != i1 - 1) {
                                        l1 = i3;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2) {
                        for (int k3 = l2; k3 < k; k3++) {
                            double d10 = (k3 + chunkX * 16 + 0.5 - x) / d2;

                            for (int l3 = j3; l3 < i1; l3++) {
                                double d8 = (l3 + chunkZ * 16 + 0.5 - z) / d2;
                                int j2 = (k3 * 16 + l3) * 128 + l;

                                for (int k2 = l - 1; k2 >= i3; k2--) {
                                    double d9 = (k2 + 0.5 - y) / d3;
                                    if (d9 > -0.7 && d10 * d10 + d9 * d9 + d8 * d8 < 1.0) {
                                        byte b0 = blocks[j2];
                                        if (b0 == Block.bloodStone.blockID || b0 == Block.dirt.blockID || b0 == Block.grass.blockID) {
                                            blocks[j2] = 0;
                                        }
                                    }

                                    j2--;
                                }
                            }
                        }

                        if (flag1) {
                            break;
                        }
                    }
                }
            }

            tunnel++;
        }
    }

    @Override
    protected void place(World world, int startChunkX, int startChunkZ, int chunkX, int chunkZ, byte[] blocks) {
        int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(10) + 1) + 1);
        if (this.rand.nextInt(5) != 0) {
            i = 0;
        }

        for (int j = 0; j < i; j++) {
            double d0 = startChunkX * 16 + this.rand.nextInt(16);
            double d1 = this.rand.nextInt(128);
            double d2 = startChunkZ * 16 + this.rand.nextInt(16);
            int k = 1;
            if (this.rand.nextInt(4) == 0) {
                this.carveRoom(chunkX, chunkZ, blocks, d0, d1, d2);
                k += this.rand.nextInt(4);
            }

            for (int l = 0; l < k; l++) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f2 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                this.carveTunnel(chunkX, chunkZ, blocks, d0, d1, d2, f2 * 2.0F, f, f1, 0, 0, 0.5);
            }
        }
    }
}
