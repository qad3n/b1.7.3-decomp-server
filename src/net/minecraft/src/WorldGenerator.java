package net.minecraft.src;

import java.util.Random;

public abstract class WorldGenerator {
    public abstract boolean generate(World var1, Random var2, int var3, int var4, int var5);

    public void prepare(double d0, double d1, double d2) {
    }
}

