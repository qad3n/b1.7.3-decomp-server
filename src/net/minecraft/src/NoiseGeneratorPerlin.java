// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator
{
    private int[] permutations;
    public double xCoord;
    public double yCoord;
    public double zCoord;
    
    public NoiseGeneratorPerlin() {
        this(new Random());
    }
    
    public NoiseGeneratorPerlin(final Random random) {
        this.permutations = new int[512];
        this.xCoord = random.nextDouble() * 256.0;
        this.yCoord = random.nextDouble() * 256.0;
        this.zCoord = random.nextDouble() * 256.0;
        for (int i = 0; i < 256; ++i) {
            this.permutations[i] = i;
        }
        for (int j = 0; j < 256; ++j) {
            final int n = random.nextInt(256 - j) + j;
            final int n2 = this.permutations[j];
            this.permutations[j] = this.permutations[n];
            this.permutations[n] = n2;
            this.permutations[j + 256] = this.permutations[j];
        }
    }
    
    public double generateNoise(final double x, final double y, final double z) {
        final double n = x + this.xCoord;
        final double n2 = y + this.yCoord;
        final double n3 = z + this.zCoord;
        int n4 = (int)n;
        int n5 = (int)n2;
        int n6 = (int)n3;
        if (n < n4) {
            --n4;
        }
        if (n2 < n5) {
            --n5;
        }
        if (n3 < n6) {
            --n6;
        }
        final int n7 = n4 & 0xFF;
        final int n8 = n5 & 0xFF;
        final int n9 = n6 & 0xFF;
        final double n10 = n - n4;
        final double n11 = n2 - n5;
        final double n12 = n3 - n6;
        final double n13 = n10 * n10 * n10 * (n10 * (n10 * 6.0 - 15.0) + 10.0);
        final double n14 = n11 * n11 * n11 * (n11 * (n11 * 6.0 - 15.0) + 10.0);
        final double delta = n12 * n12 * n12 * (n12 * (n12 * 6.0 - 15.0) + 10.0);
        final int n15 = this.permutations[n7] + n8;
        final int n16 = this.permutations[n15] + n9;
        final int n17 = this.permutations[n15 + 1] + n9;
        final int n18 = this.permutations[n7 + 1] + n8;
        final int n19 = this.permutations[n18] + n9;
        final int n20 = this.permutations[n18 + 1] + n9;
        return this.lerp(delta, this.lerp(n14, this.lerp(n13, this.grad(this.permutations[n16], n10, n11, n12), this.grad(this.permutations[n19], n10 - 1.0, n11, n12)), this.lerp(n13, this.grad(this.permutations[n17], n10, n11 - 1.0, n12), this.grad(this.permutations[n20], n10 - 1.0, n11 - 1.0, n12))), this.lerp(n14, this.lerp(n13, this.grad(this.permutations[n16 + 1], n10, n11, n12 - 1.0), this.grad(this.permutations[n19 + 1], n10 - 1.0, n11, n12 - 1.0)), this.lerp(n13, this.grad(this.permutations[n17 + 1], n10, n11 - 1.0, n12 - 1.0), this.grad(this.permutations[n20 + 1], n10 - 1.0, n11 - 1.0, n12 - 1.0))));
    }
    
    public final double lerp(final double delta, final double min, final double max) {
        return min + delta * (max - min);
    }
    
    public final double gradientDot(final int hash, final double x, final double z) {
        final int n = hash & 0xF;
        final double n2 = (1 - ((n & 0x8) >> 3)) * x;
        final double n3 = (n < 4) ? 0.0 : ((n == 12 || n == 14) ? x : z);
        return (((n & 0x1) == 0x0) ? n2 : (-n2)) + (((n & 0x2) == 0x0) ? n3 : (-n3));
    }
    
    public final double grad(final int hash, final double x, final double y, final double z) {
        final int n = hash & 0xF;
        final double n2 = (n < 8) ? x : y;
        final double n3 = (n < 4) ? y : ((n == 12 || n == 14) ? x : z);
        return (((n & 0x1) == 0x0) ? n2 : (-n2)) + (((n & 0x2) == 0x0) ? n3 : (-n3));
    }
    
    public double getValue(final double x, final double y) {
        return this.generateNoise(x, y, 0.0);
    }
    
    public void add(final double[] values, final double x, final double y, final double z, final int sizeX, final int sizeY, final int sizeZ, final double scaleX, final double scaleY, final double scaleZ, final double noiseScale) {
        if (sizeY == 1) {
            int n = 0;
            final double n2 = 1.0 / noiseScale;
            for (int i = 0; i < sizeX; ++i) {
                final double n3 = (x + i) * scaleX + this.xCoord;
                int n4 = (int)n3;
                if (n3 < n4) {
                    --n4;
                }
                final int n5 = n4 & 0xFF;
                final double n6 = n3 - n4;
                final double n7 = n6 * n6 * n6 * (n6 * (n6 * 6.0 - 15.0) + 10.0);
                for (int j = 0; j < sizeZ; ++j) {
                    final double n8 = (z + j) * scaleZ + this.zCoord;
                    int n9 = (int)n8;
                    if (n8 < n9) {
                        --n9;
                    }
                    final int n10 = n9 & 0xFF;
                    final double n11 = n8 - n9;
                    final double delta = n11 * n11 * n11 * (n11 * (n11 * 6.0 - 15.0) + 10.0);
                    final int n12 = this.permutations[this.permutations[n5] + 0] + n10;
                    final int n13 = this.permutations[this.permutations[n5 + 1] + 0] + n10;
                    final double lerp = this.lerp(delta, this.lerp(n7, this.gradientDot(this.permutations[n12], n6, n11), this.grad(this.permutations[n13], n6 - 1.0, 0.0, n11)), this.lerp(n7, this.grad(this.permutations[n12 + 1], n6, 0.0, n11 - 1.0), this.grad(this.permutations[n13 + 1], n6 - 1.0, 0.0, n11 - 1.0)));
                    final int n14 = n++;
                    values[n14] += lerp * n2;
                }
            }
            return;
        }
        int n15 = 0;
        final double n16 = 1.0 / noiseScale;
        int n17 = -1;
        double lerp2 = 0.0;
        double lerp3 = 0.0;
        double lerp4 = 0.0;
        double lerp5 = 0.0;
        for (int k = 0; k < sizeX; ++k) {
            final double n18 = (x + k) * scaleX + this.xCoord;
            int n19 = (int)n18;
            if (n18 < n19) {
                --n19;
            }
            final int n20 = n19 & 0xFF;
            final double n21 = n18 - n19;
            final double n22 = n21 * n21 * n21 * (n21 * (n21 * 6.0 - 15.0) + 10.0);
            for (int l = 0; l < sizeZ; ++l) {
                final double n23 = (z + l) * scaleZ + this.zCoord;
                int n24 = (int)n23;
                if (n23 < n24) {
                    --n24;
                }
                final int n25 = n24 & 0xFF;
                final double n26 = n23 - n24;
                final double delta2 = n26 * n26 * n26 * (n26 * (n26 * 6.0 - 15.0) + 10.0);
                for (int n27 = 0; n27 < sizeY; ++n27) {
                    final double n28 = (y + n27) * scaleY + this.yCoord;
                    int n29 = (int)n28;
                    if (n28 < n29) {
                        --n29;
                    }
                    final int n30 = n29 & 0xFF;
                    final double n31 = n28 - n29;
                    final double n32 = n31 * n31 * n31 * (n31 * (n31 * 6.0 - 15.0) + 10.0);
                    if (n27 == 0 || n30 != n17) {
                        n17 = n30;
                        final int n33 = this.permutations[n20] + n30;
                        final int n34 = this.permutations[n33] + n25;
                        final int n35 = this.permutations[n33 + 1] + n25;
                        final int n36 = this.permutations[n20 + 1] + n30;
                        final int n37 = this.permutations[n36] + n25;
                        final int n38 = this.permutations[n36 + 1] + n25;
                        lerp2 = this.lerp(n22, this.grad(this.permutations[n34], n21, n31, n26), this.grad(this.permutations[n37], n21 - 1.0, n31, n26));
                        lerp3 = this.lerp(n22, this.grad(this.permutations[n35], n21, n31 - 1.0, n26), this.grad(this.permutations[n38], n21 - 1.0, n31 - 1.0, n26));
                        lerp4 = this.lerp(n22, this.grad(this.permutations[n34 + 1], n21, n31, n26 - 1.0), this.grad(this.permutations[n37 + 1], n21 - 1.0, n31, n26 - 1.0));
                        lerp5 = this.lerp(n22, this.grad(this.permutations[n35 + 1], n21, n31 - 1.0, n26 - 1.0), this.grad(this.permutations[n38 + 1], n21 - 1.0, n31 - 1.0, n26 - 1.0));
                    }
                    final double lerp6 = this.lerp(delta2, this.lerp(n32, lerp2, lerp3), this.lerp(n32, lerp4, lerp5));
                    final int n39 = n15++;
                    values[n39] += lerp6 * n16;
                }
            }
        }
    }
}
