package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BiomeGenBase {
    public static final BiomeGenBase rainforest = new BiomeGenRainforest().setColor(588342).setBiomeName("Rainforest").setMutatedColor(2094168);
    public static final BiomeGenBase swampland = new BiomeGenSwamp().setColor(522674).setBiomeName("Swampland").setMutatedColor(9154376);
    public static final BiomeGenBase seasonalForest = new BiomeGenBase().setColor(10215459).setBiomeName("Seasonal Forest");
    public static final BiomeGenBase forest = new BiomeGenForest().setColor(353825).setBiomeName("Forest").setMutatedColor(5159473);
    public static final BiomeGenBase savanna = new BiomeGenDesert().setColor(14278691).setBiomeName("Savanna");
    public static final BiomeGenBase shrubland = new BiomeGenBase().setColor(10595616).setBiomeName("Shrubland");
    public static final BiomeGenBase taiga = new BiomeGenTaiga().setColor(3060051).setBiomeName("Taiga").setEnableSnow().setMutatedColor(8107825);
    public static final BiomeGenBase desert = new BiomeGenDesert().setColor(16421912).setBiomeName("Desert").setDisableRain();
    public static final BiomeGenBase plains = new BiomeGenDesert().setColor(16767248).setBiomeName("Plains");
    public static final BiomeGenBase iceDesert = new BiomeGenDesert().setColor(16772499).setBiomeName("Ice Desert").setEnableSnow().setDisableRain().setMutatedColor(12899129);
    public static final BiomeGenBase tundra = new BiomeGenBase().setColor(5762041).setBiomeName("Tundra").setEnableSnow().setMutatedColor(12899129);
    public static final BiomeGenBase hell = new BiomeGenHell().setColor(0xFF0000).setBiomeName("Hell").setDisableRain();
    public static final BiomeGenBase SKY = new BiomeGenSky().setColor(0x8080FF).setBiomeName("Sky").setDisableRain();
    public String biomeName;
    public int color;
    public byte topBlock;
    public byte fillerBlock;
    public int mutatedColor;
    protected List<SpawnListEntry> spawnableMonsterList;
    protected List<SpawnListEntry> spawnableCreatureList;
    protected List<SpawnListEntry> spawnableWaterCreatureList;
    private boolean enableSnow;
    private boolean enableRain;
    private static BiomeGenBase[] biomeLookupTable = new BiomeGenBase[4096];

    protected BiomeGenBase() {
        this.topBlock = (byte)Block.grass.blockID;
        this.fillerBlock = (byte)Block.dirt.blockID;
        this.mutatedColor = 5169201;
        this.spawnableMonsterList = new ArrayList<SpawnListEntry>();
        this.spawnableCreatureList = new ArrayList<SpawnListEntry>();
        this.spawnableWaterCreatureList = new ArrayList<SpawnListEntry>();
        this.enableRain = true;
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 10));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10));
        this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8));
        this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10));
    }

    private BiomeGenBase setDisableRain() {
        this.enableRain = false;
        return this;
    }

    public static void generateBiomeLookup() {
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                BiomeGenBase.biomeLookupTable[i + j * 64] = BiomeGenBase.getBiome((float)i / 63.0f, (float)j / 63.0f);
            }
        }
        BiomeGenBase.desert.topBlock = BiomeGenBase.desert.fillerBlock = (byte)Block.sand.blockID;
        BiomeGenBase.iceDesert.topBlock = BiomeGenBase.iceDesert.fillerBlock = (byte)Block.sand.blockID;
    }

    public WorldGenerator getRandomWorldGenForTrees(Random random) {
        if (random.nextInt(10) == 0) {
            return new WorldGenBigTree();
        }
        return new WorldGenTrees();
    }

    protected BiomeGenBase setEnableSnow() {
        this.enableSnow = true;
        return this;
    }

    protected BiomeGenBase setBiomeName(String name) {
        this.biomeName = name;
        return this;
    }

    protected BiomeGenBase setMutatedColor(int color) {
        this.mutatedColor = color;
        return this;
    }

    protected BiomeGenBase setColor(int color) {
        this.color = color;
        return this;
    }

    public static BiomeGenBase getBiomeFromLookup(double temperature, double downfall) {
        int n = (int)(temperature * 63.0);
        int n2 = (int)(downfall * 63.0);
        return biomeLookupTable[n + n2 * 64];
    }

    public static BiomeGenBase getBiome(float temperature, float downfall) {
        downfall *= temperature;
        if (temperature < 0.1f) {
            return tundra;
        }
        if (downfall < 0.2f) {
            if (temperature < 0.5f) {
                return tundra;
            }
            if (temperature < 0.95f) {
                return savanna;
            }
            return desert;
        }
        if (downfall > 0.5f && temperature < 0.7f) {
            return swampland;
        }
        if (temperature < 0.5f) {
            return taiga;
        }
        if (temperature < 0.97f) {
            if (downfall < 0.35f) {
                return shrubland;
            }
            return forest;
        }
        if (downfall < 0.45f) {
            return plains;
        }
        if (downfall < 0.9f) {
            return seasonalForest;
        }
        return rainforest;
    }

    public List<SpawnListEntry> getSpawnableList(EnumCreatureType category) {
        if (category == EnumCreatureType.monster) {
            return this.spawnableMonsterList;
        }
        if (category == EnumCreatureType.creature) {
            return this.spawnableCreatureList;
        }
        if (category == EnumCreatureType.waterCreature) {
            return this.spawnableWaterCreatureList;
        }
        return null;
    }

    public boolean getEnableSnow() {
        return this.enableSnow;
    }

    public boolean canSpawnLightningBolt() {
        if (this.enableSnow) {
            return false;
        }
        return this.enableRain;
    }

    static {
        BiomeGenBase.generateBiomeLookup();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SpawnListEntry {
        public Class<? extends EntityLiving> entityClass;
        public int spawnRarityRate;

        public SpawnListEntry(Class<? extends EntityLiving> type, int weight) {
            this.entityClass = type;
            this.spawnRarityRate = weight;
        }
    }
}

