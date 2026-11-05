package net.minecraft.src;

import java.util.Random;

public class BiomeGenTaiga
extends BiomeGenBase {
    public BiomeGenTaiga() {
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 2));
    }

    public WorldGenerator getRandomWorldGenForTrees(Random random) {
        if (random.nextInt(3) == 0) {
            return new WorldGenTaiga1();
        }
        return new WorldGenTaiga2();
    }
}

