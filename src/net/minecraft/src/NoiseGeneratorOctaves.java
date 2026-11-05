package net.minecraft.src;

import java.util.Random;

public class NoiseGeneratorOctaves
extends NoiseGenerator {
    private NoiseGeneratorPerlin[] generatorCollection;
    private int levels;

    public NoiseGeneratorOctaves(Random random, int levels) {
        this.levels = levels;
        this.generatorCollection = new NoiseGeneratorPerlin[levels];
        for (int i = 0; i < levels; ++i) {
            this.generatorCollection[i] = new NoiseGeneratorPerlin(random);
        }
    }

    public double getValue(double x, double y) {
        double d = 0.0;
        double d2 = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            d += this.generatorCollection[i].getValue(x * d2, y * d2) / d2;
            d2 /= 2.0;
        }
        return d;
    }

    public double[] generateNoiseOctaves(double[] values, double x, double y, double z, int sizeX, int sizeY, int sizeZ, double scaleX, double scaleY, double scaleZ) {
        if (values == null) {
            values = new double[sizeX * sizeY * sizeZ];
        } else {
            for (int i = 0; i < values.length; ++i) {
                values[i] = 0.0;
            }
        }
        double d = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            this.generatorCollection[i].add(values, x, y, z, sizeX, sizeY, sizeZ, scaleX * d, scaleY * d, scaleZ * d, d);
            d /= 2.0;
        }
        return values;
    }

    public double[] getRegion(double[] values, int x, int z, int sizeX, int sizeZ, double scaleX, double scaleZ, double d) {
        return this.generateNoiseOctaves(values, x, 10.0, z, sizeX, 1, sizeZ, scaleX, 1.0, scaleZ);
    }
}

