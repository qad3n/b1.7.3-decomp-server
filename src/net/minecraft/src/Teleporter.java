package net.minecraft.src;

import java.util.Random;

public class Teleporter {
    private Random random = new Random();

    public void setExitLocation(World world, Entity entity) {
        if (this.findExitLocation(world, entity)) {
            return;
        }
        this.createExitLocation(world, entity);
        this.findExitLocation(world, entity);
    }

    public boolean findExitLocation(World world, Entity entity) {
        short short1 = 128;
        double d0 = -1.0;
        int i = 0;
        int j = 0;
        int k = 0;
        int l = MathHelper.floor_double(entity.posX);
        int i1 = MathHelper.floor_double(entity.posZ);

        for (int j1 = l - short1; j1 <= l + short1; j1++) {
            double d1 = j1 + 0.5 - entity.posX;

            for (int l1 = i1 - short1; l1 <= i1 + short1; l1++) {
                double d2 = l1 + 0.5 - entity.posZ;

                for (int i2 = 127; i2 >= 0; i2--) {
                    if (world.getBlockId(j1, i2, l1) == Block.portal.blockID) {
                        while (world.getBlockId(j1, i2 - 1, l1) == Block.portal.blockID) {
                            i2--;
                        }

                        double d4 = i2 + 0.5 - entity.posY;
                        double d5 = d1 * d1 + d4 * d4 + d2 * d2;
                        if (d0 < 0.0 || d5 < d0) {
                            d0 = d5;
                            i = j1;
                            j = i2;
                            k = l1;
                        }
                    }
                }
            }
        }

        if (d0 >= 0.0) {
            int j2 = i;
            int k2 = j;
            int k1 = k;
            double d6 = j2 + 0.5;
            double d3 = k2 + 0.5;
            double d7 = k1 + 0.5;
            if (world.getBlockId(j2 - 1, k2, k1) == Block.portal.blockID) {
                d6 -= 0.5;
            }

            if (world.getBlockId(j2 + 1, k2, k1) == Block.portal.blockID) {
                d6 += 0.5;
            }

            if (world.getBlockId(j2, k2, k1 - 1) == Block.portal.blockID) {
                d7 -= 0.5;
            }

            if (world.getBlockId(j2, k2, k1 + 1) == Block.portal.blockID) {
                d7 += 0.5;
            }

            entity.setLocationAndAngles(d6, d3, d7, entity.rotationYaw, 0.0F);
            entity.motionX = entity.motionY = entity.motionZ = 0.0;
            return true;
        } else {
            return false;
        }
    }

    public boolean createExitLocation(World world, Entity entity) {
        byte b0 = 16;
        double d0 = -1.0;
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);
        int l = i;
        int i1 = j;
        int j1 = k;
        int k1 = 0;
        int l1 = this.random.nextInt(4);

        for (int i2 = i - b0; i2 <= i + b0; i2++) {
            double d1 = i2 + 0.5 - entity.posX;

            for (int k2 = k - b0; k2 <= k + b0; k2++) {
                double d2 = k2 + 0.5 - entity.posZ;

                label296:
                for (int i3 = 127; i3 >= 0; i3--) {
                    if (world.isAirBlock(i2, i3, k2)) {
                        while (i3 > 0 && world.isAirBlock(i2, i3 - 1, k2)) {
                            i3--;
                        }

                        for (int j3 = l1; j3 < l1 + 4; j3++) {
                            int k3 = j3 % 2;
                            int l3 = 1 - k3;
                            if (j3 % 4 >= 2) {
                                k3 = -k3;
                                l3 = -l3;
                            }

                            for (int i4 = 0; i4 < 3; i4++) {
                                for (int j4 = 0; j4 < 4; j4++) {
                                    for (int k4 = -1; k4 < 4; k4++) {
                                        int l4 = i2 + (j4 - 1) * k3 + i4 * l3;
                                        int i5 = i3 + k4;
                                        int j5 = k2 + (j4 - 1) * l3 - i4 * k3;
                                        if (k4 < 0 && !world.getBlockMaterial(l4, i5, j5).isSolid() || k4 >= 0 && !world.isAirBlock(l4, i5, j5)) {
                                            continue label296;
                                        }
                                    }
                                }
                            }

                            double d5 = i3 + 0.5 - entity.posY;
                            double d7 = d1 * d1 + d5 * d5 + d2 * d2;
                            if (d0 < 0.0 || d7 < d0) {
                                d0 = d7;
                                l = i2;
                                i1 = i3;
                                j1 = k2;
                                k1 = j3 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (d0 < 0.0) {
            for (int k5 = i - b0; k5 <= i + b0; k5++) {
                double d3 = k5 + 0.5 - entity.posX;

                for (int j6 = k - b0; j6 <= k + b0; j6++) {
                    double d4 = j6 + 0.5 - entity.posZ;

                    label233:
                    for (int i7 = 127; i7 >= 0; i7--) {
                        if (world.isAirBlock(k5, i7, j6)) {
                            while (world.isAirBlock(k5, i7 - 1, j6)) {
                                i7--;
                            }

                            for (int l7 = l1; l7 < l1 + 2; l7++) {
                                int l8 = l7 % 2;
                                int l9 = 1 - l8;

                                for (int l10 = 0; l10 < 4; l10++) {
                                    for (int l11 = -1; l11 < 4; l11++) {
                                        int l12 = k5 + (l10 - 1) * l8;
                                        int i13 = i7 + l11;
                                        int j13 = j6 + (l10 - 1) * l9;
                                        if (l11 < 0 && !world.getBlockMaterial(l12, i13, j13).isSolid() || l11 >= 0 && !world.isAirBlock(l12, i13, j13)) {
                                            continue label233;
                                        }
                                    }
                                }

                                double d6 = i7 + 0.5 - entity.posY;
                                double d8 = d3 * d3 + d6 * d6 + d4 * d4;
                                if (d0 < 0.0 || d8 < d0) {
                                    d0 = d8;
                                    l = k5;
                                    i1 = i7;
                                    j1 = j6;
                                    k1 = l7 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int l5 = k1;
        int i6 = l;
        int j2 = i1;
        int k6 = j1;
        int l6 = l5 % 2;
        int l2 = 1 - l6;
        if (l5 % 4 >= 2) {
            l6 = -l6;
            l2 = -l2;
        }

        if (d0 < 0.0) {
            if (i1 < 70) {
                i1 = 70;
            }

            if (i1 > 118) {
                i1 = 118;
            }

            j2 = i1;

            for (int j7 = -1; j7 <= 1; j7++) {
                for (int i8 = 1; i8 < 3; i8++) {
                    for (int i9 = -1; i9 < 3; i9++) {
                        int i10 = i6 + (i8 - 1) * l6 + j7 * l2;
                        int i11 = j2 + i9;
                        int i12 = k6 + (i8 - 1) * l2 - j7 * l6;
                        boolean flag = i9 < 0;
                        world.setBlockWithNotify(i10, i11, i12, flag ? Block.obsidian.blockID : 0);
                    }
                }
            }
        }

        for (int k7 = 0; k7 < 4; k7++) {
            world.editingBlocks = true;

            for (int j8 = 0; j8 < 4; j8++) {
                for (int j9 = -1; j9 < 4; j9++) {
                    int j10 = i6 + (j8 - 1) * l6;
                    int j11 = j2 + j9;
                    int j12 = k6 + (j8 - 1) * l2;
                    boolean flag1 = j8 == 0 || j8 == 3 || j9 == -1 || j9 == 3;
                    world.setBlockWithNotify(j10, j11, j12, flag1 ? Block.obsidian.blockID : Block.portal.blockID);
                }
            }

            world.editingBlocks = false;

            for (int k8 = 0; k8 < 4; k8++) {
                for (int k9 = -1; k9 < 4; k9++) {
                    int k10 = i6 + (k8 - 1) * l6;
                    int k11 = j2 + k9;
                    int k12 = k6 + (k8 - 1) * l2;
                    world.notifyBlocksOfNeighborChange(k10, k11, k12, world.getBlockId(k10, k11, k12));
                }
            }
        }

        return true;
    }
}
