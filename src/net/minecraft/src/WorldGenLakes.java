package net.minecraft.src;

import java.util.Random;

public class WorldGenLakes extends WorldGenerator {
    private int liquid;

    public WorldGenLakes(int liquid) {
        this.liquid = liquid;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        x -= 8;
        z -= 8;

        while (y > 0 && world.isAirBlock(x, y, z)) {
            y--;
        }

        y -= 4;
        boolean[] aboolean = new boolean[2048];
        int i = random.nextInt(4) + 4;

        for (int j = 0; j < i; j++) {
            double d0 = random.nextDouble() * 6.0 + 3.0;
            double d1 = random.nextDouble() * 4.0 + 2.0;
            double d2 = random.nextDouble() * 6.0 + 3.0;
            double d3 = random.nextDouble() * (16.0 - d0 - 2.0) + 1.0 + d0 / 2.0;
            double d4 = random.nextDouble() * (8.0 - d1 - 4.0) + 2.0 + d1 / 2.0;
            double d5 = random.nextDouble() * (16.0 - d2 - 2.0) + 1.0 + d2 / 2.0;

            for (int l = 1; l < 15; l++) {
                for (int i1 = 1; i1 < 15; i1++) {
                    for (int j1 = 1; j1 < 7; j1++) {
                        double d6 = (l - d3) / (d0 / 2.0);
                        double d7 = (j1 - d4) / (d1 / 2.0);
                        double d8 = (i1 - d5) / (d2 / 2.0);
                        double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                        if (d9 < 1.0) {
                            aboolean[(l * 16 + i1) * 8 + j1] = true;
                        }
                    }
                }
            }
        }

        for (int k1 = 0; k1 < 16; k1++) {
            for (int k2 = 0; k2 < 16; k2++) {
                for (int k = 0; k < 8; k++) {
                    boolean flag = !aboolean[(k1 * 16 + k2) * 8 + k]
                        && (
                            k1 < 15 && aboolean[((k1 + 1) * 16 + k2) * 8 + k]
                                || k1 > 0 && aboolean[((k1 - 1) * 16 + k2) * 8 + k]
                                || k2 < 15 && aboolean[(k1 * 16 + k2 + 1) * 8 + k]
                                || k2 > 0 && aboolean[(k1 * 16 + (k2 - 1)) * 8 + k]
                                || k < 7 && aboolean[(k1 * 16 + k2) * 8 + k + 1]
                                || k > 0 && aboolean[(k1 * 16 + k2) * 8 + (k - 1)]
                        );
                    if (flag) {
                        Material material = world.getBlockMaterial(x + k1, y + k, z + k2);
                        if (k >= 4 && material.getIsLiquid()) {
                            return false;
                        }

                        if (k < 4 && !material.isSolid() && world.getBlockId(x + k1, y + k, z + k2) != this.liquid) {
                            return false;
                        }
                    }
                }
            }
        }

        for (int l1 = 0; l1 < 16; l1++) {
            for (int l2 = 0; l2 < 16; l2++) {
                for (int k3 = 0; k3 < 8; k3++) {
                    if (aboolean[(l1 * 16 + l2) * 8 + k3]) {
                        world.setBlock(x + l1, y + k3, z + l2, k3 >= 4 ? 0 : this.liquid);
                    }
                }
            }
        }

        for (int i2 = 0; i2 < 16; i2++) {
            for (int i3 = 0; i3 < 16; i3++) {
                for (int l3 = 4; l3 < 8; l3++) {
                    if (aboolean[(i2 * 16 + i3) * 8 + l3]
                        && world.getBlockId(x + i2, y + l3 - 1, z + i3) == Block.dirt.blockID
                        && world.getSavedLightValue(EnumSkyBlock.Sky, x + i2, y + l3, z + i3) > 0) {
                        world.setBlock(x + i2, y + l3 - 1, z + i3, Block.grass.blockID);
                    }
                }
            }
        }

        if (Block.blocksList[this.liquid].blockMaterial == Material.lava) {
            for (int j2 = 0; j2 < 16; j2++) {
                for (int j3 = 0; j3 < 16; j3++) {
                    for (int i4 = 0; i4 < 8; i4++) {
                        boolean flag1 = !aboolean[(j2 * 16 + j3) * 8 + i4]
                            && (
                                j2 < 15 && aboolean[((j2 + 1) * 16 + j3) * 8 + i4]
                                    || j2 > 0 && aboolean[((j2 - 1) * 16 + j3) * 8 + i4]
                                    || j3 < 15 && aboolean[(j2 * 16 + j3 + 1) * 8 + i4]
                                    || j3 > 0 && aboolean[(j2 * 16 + (j3 - 1)) * 8 + i4]
                                    || i4 < 7 && aboolean[(j2 * 16 + j3) * 8 + i4 + 1]
                                    || i4 > 0 && aboolean[(j2 * 16 + j3) * 8 + (i4 - 1)]
                            );
                        if (flag1 && (i4 < 4 || random.nextInt(2) != 0) && world.getBlockMaterial(x + j2, y + i4, z + j3).isSolid()) {
                            world.setBlock(x + j2, y + i4, z + j3, Block.stone.blockID);
                        }
                    }
                }
            }
        }

        return true;
    }
}
