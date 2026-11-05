package net.minecraft.src;


/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum EnumCreatureType {
    monster(IMob.class, 70, Material.air, false),
    creature(EntityAnimal.class, 15, Material.air, true),
    waterCreature(EntityWaterMob.class, 5, Material.water, true);

    private final Class<?> creatureClass;
    private final int maxNumberOfCreature;
    private final Material creatureMaterial;
    private final boolean peaceful;

    private EnumCreatureType(Class<?> type, int cap, Material spawnableMaterial, boolean peaceful) {
        this.creatureClass = type;
        this.maxNumberOfCreature = cap;
        this.creatureMaterial = spawnableMaterial;
        this.peaceful = peaceful;
    }

    public Class<?> getCreatureClass() {
        return this.creatureClass;
    }

    public int getMaxNumberOfCreature() {
        return this.maxNumberOfCreature;
    }

    public Material getCreatureMaterial() {
        return this.creatureMaterial;
    }

    public boolean isPeaceful() {
        return this.peaceful;
    }
}

