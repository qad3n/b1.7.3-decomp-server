package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EntityList {
    private static Map<String, Class<? extends Entity>> stringToClassMapping = new HashMap<String, Class<? extends Entity>>();
    private static Map<Class<? extends Entity>, String> classToStringMapping = new HashMap<Class<? extends Entity>, String>();
    private static Map<Integer, Class<? extends Entity>> IDtoClassMapping = new HashMap<Integer, Class<? extends Entity>>();
    private static Map<Class<? extends Entity>, Integer> classToIDMapping = new HashMap<Class<? extends Entity>, Integer>();

    private static void addMapping(Class<? extends Entity> type, String key, int id) {
        stringToClassMapping.put(key, type);
        classToStringMapping.put(type, key);
        IDtoClassMapping.put(id, type);
        classToIDMapping.put(type, id);
    }

    public static Entity createEntityInWorld(String key, World world) {
        Entity entity = null;
        try {
            Class<? extends Entity> clazz = stringToClassMapping.get(key);
            if (clazz != null) {
                entity = clazz.getConstructor(World.class).newInstance(world);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return entity;
    }

    public static Entity createEntityFromNBT(NBTTagCompound nbt, World world) {
        Entity entity = null;
        try {
            Class<? extends Entity> clazz = stringToClassMapping.get(nbt.getString("id"));
            if (clazz != null) {
                entity = clazz.getConstructor(World.class).newInstance(world);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (entity != null) {
            entity.readFromNBT(nbt);
        } else {
            System.out.println("Skipping Entity with id " + nbt.getString("id"));
        }
        return entity;
    }

    public static int getEntityID(Entity entity) {
        return classToIDMapping.get(entity.getClass());
    }

    public static String getEntityString(Entity entity) {
        return classToStringMapping.get(entity.getClass());
    }

    static {
        EntityList.addMapping(EntityArrow.class, "Arrow", 10);
        EntityList.addMapping(EntitySnowball.class, "Snowball", 11);
        EntityList.addMapping(EntityItem.class, "Item", 1);
        EntityList.addMapping(EntityPainting.class, "Painting", 9);
        EntityList.addMapping(EntityLiving.class, "Mob", 48);
        EntityList.addMapping(EntityMob.class, "Monster", 49);
        EntityList.addMapping(EntityCreeper.class, "Creeper", 50);
        EntityList.addMapping(EntitySkeleton.class, "Skeleton", 51);
        EntityList.addMapping(EntitySpider.class, "Spider", 52);
        EntityList.addMapping(EntityGiantZombie.class, "Giant", 53);
        EntityList.addMapping(EntityZombie.class, "Zombie", 54);
        EntityList.addMapping(EntitySlime.class, "Slime", 55);
        EntityList.addMapping(EntityGhast.class, "Ghast", 56);
        EntityList.addMapping(EntityPigZombie.class, "PigZombie", 57);
        EntityList.addMapping(EntityPig.class, "Pig", 90);
        EntityList.addMapping(EntitySheep.class, "Sheep", 91);
        EntityList.addMapping(EntityCow.class, "Cow", 92);
        EntityList.addMapping(EntityChicken.class, "Chicken", 93);
        EntityList.addMapping(EntitySquid.class, "Squid", 94);
        EntityList.addMapping(EntityWolf.class, "Wolf", 95);
        EntityList.addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        EntityList.addMapping(EntityFallingSand.class, "FallingSand", 21);
        EntityList.addMapping(EntityMinecart.class, "Minecart", 40);
        EntityList.addMapping(EntityBoat.class, "Boat", 41);
    }
}

