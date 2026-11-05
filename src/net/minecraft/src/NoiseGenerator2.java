package net.minecraft.src;

import java.util.Random;

public class NoiseGenerator2 {
    private static int[][] GRADIENTS = new int[][]{
        {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };
    private int[] permutations = new int[512];
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    public NoiseGenerator2() {
        this(new Random());
    }

    public NoiseGenerator2(Random random) {
        int n;
        this.offsetX = random.nextDouble() * 256.0;
        this.offsetY = random.nextDouble() * 256.0;
        this.offsetZ = random.nextDouble() * 256.0;
        for (n = 0; n < 256; ++n) {
            this.permutations[n] = n;
        }
        for (n = 0; n < 256; ++n) {
            int n2 = random.nextInt(256 - n) + n;
            int n3 = this.permutations[n];
            this.permutations[n] = this.permutations[n2];
            this.permutations[n2] = n3;
            this.permutations[n + 256] = this.permutations[n];
        }
    }

    private static int wrap(double x) {
        return x > 0.0 ? (int)x : (int)x - 1;
    }

    private static double dot(int[] gradient, double x, double y) {
        return gradient[0] * x + gradient[1] * y;
    }

    public void add(double[] values, double x, double y, int sizeX, int sizeY, double scaleX, double scaleY, double noiseScale) {
        int i = 0;

        for (int j = 0; j < sizeX; j++) {
            double d0 = (x + j) * scaleX + this.offsetX;

            for (int k = 0; k < sizeY; k++) {
                double d1 = (y + k) * scaleY + this.offsetY;
                double d5 = (d0 + d1) * F2;
                int l = wrap(d0 + d5);
                int i1 = wrap(d1 + d5);
                double d6 = (l + i1) * G2;
                double d7 = l - d6;
                double d8 = i1 - d6;
                double d9 = d0 - d7;
                double d10 = d1 - d8;
                byte b0;
                byte b1;
                if (d9 > d10) {
                    b0 = 1;
                    b1 = 0;
                } else {
                    b0 = 0;
                    b1 = 1;
                }

                double d11 = d9 - b0 + G2;
                double d12 = d10 - b1 + G2;
                double d13 = d9 - 1.0 + 2.0 * G2;
                double d14 = d10 - 1.0 + 2.0 * G2;
                int j1 = l & 0xFF;
                int k1 = i1 & 0xFF;
                int l1 = this.permutations[j1 + this.permutations[k1]] % 12;
                int i2 = this.permutations[j1 + b0 + this.permutations[k1 + b1]] % 12;
                int j2 = this.permutations[j1 + 1 + this.permutations[k1 + 1]] % 12;
                double d15 = 0.5 - d9 * d9 - d10 * d10;
                double d2;
                if (d15 < 0.0) {
                    d2 = 0.0;
                } else {
                    d15 *= d15;
                    d2 = d15 * d15 * dot(GRADIENTS[l1], d9, d10);
                }

                double d16 = 0.5 - d11 * d11 - d12 * d12;
                double d3;
                if (d16 < 0.0) {
                    d3 = 0.0;
                } else {
                    d16 *= d16;
                    d3 = d16 * d16 * dot(GRADIENTS[i2], d11, d12);
                }

                double d17 = 0.5 - d13 * d13 - d14 * d14;
                double d4;
                if (d17 < 0.0) {
                    d4 = 0.0;
                } else {
                    d17 *= d17;
                    d4 = d17 * d17 * dot(GRADIENTS[j2], d13, d14);
                }

                values[i++] += 70.0 * (d2 + d3 + d4) * noiseScale;
            }
        }
    }
}
