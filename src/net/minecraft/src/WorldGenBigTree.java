package net.minecraft.src;

import java.util.Random;

public class WorldGenBigTree extends WorldGenerator {
    static final byte[] MINOR_AXES = new byte[]{2, 0, 0, 1, 2, 1};
    Random random0 = new Random();
    World worldObj;
    int[] basePos = new int[]{0, 0, 0};
    int heightLimit = 0;
    int height;
    double trunkScale = 0.618;
    double branchDensity = 1.0;
    double branchSlope = 0.381;
    double branchLengthScale = 1.0;
    double foliageDensity = 1.0;
    int trunkWidth = 1;
    int maxTrunkHeight = 12;
    int foliageClusterHeight = 4;
    int[][] branches;

    void makeBranches() {
        this.height = (int)(this.heightLimit * this.trunkScale);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int i = (int)(1.382 + Math.pow(this.foliageDensity * this.heightLimit / 13.0, 2.0));
        if (i < 1) {
            i = 1;
        }

        int[][] aint = new int[i * this.heightLimit][4];
        int j = this.basePos[1] + this.heightLimit - this.foliageClusterHeight;
        int k = 1;
        int l = this.basePos[1] + this.height;
        int i1 = j - this.basePos[1];
        aint[0][0] = this.basePos[0];
        aint[0][1] = j;
        aint[0][2] = this.basePos[2];
        aint[0][3] = l;
        j--;

        while (i1 >= 0) {
            int j1 = 0;
            float f = this.getTreeShape(i1);
            if (f < 0.0F) {
                j--;
                i1--;
            } else {
                double d0 = 0.5;

                while (j1 < i) {
                    double d1 = this.branchLengthScale * (f * (this.random0.nextFloat() + 0.328));
                    double d2 = this.random0.nextFloat() * 2.0 * 3.14159;
                    int k1 = MathHelper.floor_double(d1 * Math.sin(d2) + this.basePos[0] + d0);
                    int l1 = MathHelper.floor_double(d1 * Math.cos(d2) + this.basePos[2] + d0);
                    int[] aint1 = new int[]{k1, j, l1};
                    int[] aint2 = new int[]{k1, j + this.foliageClusterHeight, l1};
                    if (this.tryBranch(aint1, aint2) == -1) {
                        int[] aint3 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
                        double d3 = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - aint1[0]), 2.0) + Math.pow(Math.abs(this.basePos[2] - aint1[2]), 2.0));
                        double d4 = d3 * this.branchSlope;
                        if (aint1[1] - d4 > l) {
                            aint3[1] = l;
                        } else {
                            aint3[1] = (int)(aint1[1] - d4);
                        }

                        if (this.tryBranch(aint3, aint1) == -1) {
                            aint[k][0] = k1;
                            aint[k][1] = j;
                            aint[k][2] = l1;
                            aint[k][3] = aint3[1];
                            k++;
                        }
                    }

                    j1++;
                }

                j--;
                i1--;
            }
        }

        this.branches = new int[k][4];
        System.arraycopy(aint, 0, this.branches, 0, k);
    }

    void placeCluster(int x, int y, int z, float shape, byte majorAxis, int clusterBlock) {
        int i = (int)(shape + 0.618);
        byte b0 = MINOR_AXES[majorAxis];
        byte b1 = MINOR_AXES[majorAxis + 3];
        int[] aint = new int[]{x, y, z};
        int[] aint1 = new int[]{0, 0, 0};
        int j = -i;
        int k = -i;

        for (aint1[majorAxis] = aint[majorAxis]; j <= i; j++) {
            aint1[b0] = aint[b0] + j;
            k = -i;

            while (k <= i) {
                double d0 = Math.sqrt(Math.pow(Math.abs(j) + 0.5, 2.0) + Math.pow(Math.abs(k) + 0.5, 2.0));
                if (d0 > shape) {
                    k++;
                } else {
                    aint1[b1] = aint[b1] + k;
                    int l = this.worldObj.getBlockId(aint1[0], aint1[1], aint1[2]);
                    if (l != 0 && l != 18) {
                        k++;
                    } else {
                        this.worldObj.setBlock(aint1[0], aint1[1], aint1[2], clusterBlock);
                        k++;
                    }
                }
            }
        }
    }

    float getTreeShape(int height) {
        if (height < (float)this.heightLimit * 0.3) {
            return -1.618F;
        }

        float f = this.heightLimit / 2.0F;
        float f1 = this.heightLimit / 2.0F - height;
        float f2;
        if (f1 == 0.0F) {
            f2 = f;
        } else if (Math.abs(f1) >= f) {
            f2 = 0.0F;
        } else {
            f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2.0) - Math.pow(Math.abs(f1), 2.0));
        }

        return f2 * 0.5F;
    }

    float getClusterShape(int layer) {
        if (layer < 0 || layer >= this.foliageClusterHeight) {
            return -1.0f;
        }
        if (layer == 0 || layer == this.foliageClusterHeight - 1) {
            return 2.0f;
        }
        return 3.0f;
    }

    void placeFoliageCluster(int x, int baseY, int z) {
        int i = baseY;

        for (int j = baseY + this.foliageClusterHeight; i < j; i++) {
            float f = this.getClusterShape(i - baseY);
            this.placeCluster(x, i, z, f, (byte)1, 18);
        }
    }

    void placeBranch(int[] from, int[] to, int log) {
        int[] aint = new int[]{0, 0, 0};
        byte b0 = 0;
        byte b1 = 0;

        while (b0 < 3) {
            aint[b0] = to[b0] - from[b0];
            if (Math.abs(aint[b0]) > Math.abs(aint[b1])) {
                b1 = b0;
            }

            b0++;
        }

        if (aint[b1] != 0) {
            byte b2 = MINOR_AXES[b1];
            byte b3 = MINOR_AXES[b1 + 3];
            byte b4;
            if (aint[b1] > 0) {
                b4 = 1;
            } else {
                b4 = -1;
            }

            double d0 = (double)aint[b2] / aint[b1];
            double d1 = (double)aint[b3] / aint[b1];
            int[] aint1 = new int[]{0, 0, 0};
            int b5 = 0;

            for (int i = aint[b1] + b4; b5 != i; b5 += b4) {
                aint1[b1] = MathHelper.floor_double(from[b1] + b5 + 0.5);
                aint1[b2] = MathHelper.floor_double(from[b2] + b5 * d0 + 0.5);
                aint1[b3] = MathHelper.floor_double(from[b3] + b5 * d1 + 0.5);
                this.worldObj.setBlock(aint1[0], aint1[1], aint1[2], log);
            }
        }
    }

    void placeFoliage() {
        int i = 0;

        for (int j = this.branches.length; i < j; i++) {
            int k = this.branches[i][0];
            int l = this.branches[i][1];
            int i1 = this.branches[i][2];
            this.placeFoliageCluster(k, l, i1);
        }
    }

    boolean shouldPlaceBranch(int height) {
        return !(height < this.heightLimit * 0.2);
    }

    void placeTrunk() {
        int i = this.basePos[0];
        int j = this.basePos[1];
        int k = this.basePos[1] + this.height;
        int l = this.basePos[2];
        int[] aint = new int[]{i, j, l};
        int[] aint1 = new int[]{i, k, l};
        this.placeBranch(aint, aint1, 17);
        if (this.trunkWidth == 2) {
            aint[0]++;
            aint1[0]++;
            this.placeBranch(aint, aint1, 17);
            aint[2]++;
            aint1[2]++;
            this.placeBranch(aint, aint1, 17);
            aint[0] += -1;
            aint1[0] += -1;
            this.placeBranch(aint, aint1, 17);
        }
    }

    void placeBranches() {
        int i = 0;
        int j = this.branches.length;
        int[] aint = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};

        while (i < j) {
            int[] aint1 = this.branches[i];
            int[] aint2 = new int[]{aint1[0], aint1[1], aint1[2]};
            aint[1] = aint1[3];
            int k = aint[1] - this.basePos[1];
            if (this.shouldPlaceBranch(k)) {
                this.placeBranch(aint, aint2, 17);
            }

            i++;
        }
    }

    int tryBranch(final int[] from, final int[] to) {
        final int[] array = { 0, 0, 0 };
        int i = 0;
        int n = 0;
        while (i < 3) {
            array[i] = to[i] - from[i];
            if (Math.abs(array[i]) > Math.abs(array[n])) {
                n = i;
            }
            i = (byte) (i + 1);
        }
        if (array[n] == 0) {
            return -1;
        }
        final byte b = WorldGenBigTree.MINOR_AXES[n];
        final byte b2 = WorldGenBigTree.MINOR_AXES[n + 3];
        int n2;
        if (array[n] > 0) {
            n2 = 1;
        } else {
            n2 = -1;
        }
        final double n3 = array[b] / (double) array[n];
        final double n4 = array[b2] / (double) array[n];
        final int[] array2 = { 0, 0, 0 };
        int j;
        int n5;
        for (j = 0, n5 = array[n] + n2; j != n5; j += n2) {
            array2[n] = from[n] + j;
            array2[b] = MathHelper.floor_double(from[b] + j * n3);
            array2[b2] = MathHelper.floor_double(from[b2] + j * n4);
            final int blockId = this.worldObj.getBlockId(array2[0], array2[1], array2[2]);
            if (blockId != 0 && blockId != 18) {
                break;
            }
        }
        if (j == n5) {
            return -1;
        }
        return Math.abs(j);
    }

    boolean canPlace() {
        int[] aint = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
        int[] aint1 = new int[]{this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2]};
        int i = this.worldObj.getBlockId(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);
        if (i != 2 && i != 3) {
            return false;
        }

        int j = this.tryBranch(aint, aint1);
        if (j == -1) {
            return true;
        }

        if (j < 6) {
            return false;
        }

        this.heightLimit = j;
        return true;
    }

    @Override
    public void prepare(double d0, double d1, double d2) {
        this.maxTrunkHeight = (int)(d0 * 12.0);
        if (d0 > 0.5) {
            this.foliageClusterHeight = 5;
        }

        this.branchLengthScale = d1;
        this.foliageDensity = d2;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        this.worldObj = world;
        long i = random.nextLong();
        this.random0.setSeed(i);
        this.basePos[0] = x;
        this.basePos[1] = y;
        this.basePos[2] = z;
        if (this.heightLimit == 0) {
            this.heightLimit = 5 + this.random0.nextInt(this.maxTrunkHeight);
        }

        if (!this.canPlace()) {
            return false;
        }

        this.makeBranches();
        this.placeFoliage();
        this.placeTrunk();
        this.placeBranches();
        return true;
    }
}
