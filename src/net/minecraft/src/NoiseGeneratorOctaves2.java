package net.minecraft.src;

import java.util.Random;

public class NoiseGeneratorOctaves2
extends NoiseGenerator {
    private NoiseGenerator2[] noiseLevels;
    private int levels;

    public NoiseGeneratorOctaves2(Random random, int levels) {
        this.levels = levels;
        this.noiseLevels = new NoiseGenerator2[levels];
        for (int i = 0; i < levels; ++i) {
            this.noiseLevels[i] = new NoiseGenerator2(random);
        }
    }

    public double[] getRegion(double[] values, double x, double y, int sizeX, int sizeY, double scaleX, double scaleY, double scaleExponent) {
        return this.getRegion(values, x, y, sizeX, sizeY, scaleX, scaleY, scaleExponent, 0.5);
    }

    public double[] getRegion(double[] values, double x, double y, int sizeX, int sizeY, double scaleX, double scaleY, double scaleExponentX, double scaleExponentY) {
        scaleX /= 1.5;
        scaleY /= 1.5;
        if (values == null || values.length < sizeX * sizeY) {
            values = new double[sizeX * sizeY];
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = 0.0;
            }
        }
        double d = 1.0;
        double d2 = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            this.noiseLevels[i].add(values, x, y, sizeX, sizeY, scaleX * d2, scaleY * d2, 0.55 / d);
            d2 *= scaleExponentX;
            d *= scaleExponentY;
        }
        return values;
    }
}

