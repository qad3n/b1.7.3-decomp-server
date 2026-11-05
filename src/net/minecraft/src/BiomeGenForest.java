package net.minecraft.src;

import java.util.Random;

public class BiomeGenForest
extends BiomeGenBase {
    public BiomeGenForest() {
        this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 2));
    }

    public WorldGenerator getRandomWorldGenForTrees(Random random) {
        if (random.nextInt(5) == 0) {
            return new WorldGenForest();
        }
        if (random.nextInt(3) == 0) {
            return new WorldGenBigTree();
        }
        return new WorldGenTrees();
    }
}

