package net.minecraft.src;

import java.util.Random;

public class BiomeGenRainforest
extends BiomeGenBase {
    public WorldGenerator getRandomWorldGenForTrees(Random random) {
        if (random.nextInt(3) == 0) {
            return new WorldGenBigTree();
        }
        return new WorldGenTrees();
    }
}

