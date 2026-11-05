package net.minecraft.src;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

public final class SpawnerAnimals {
    private static Set<ChunkCoordIntPair> eligibleChunksForSpawning = new HashSet<>();
    protected static final Class<? extends EntityLiving>[] MONSTER_TYPES = new Class[]{EntitySpider.class, EntityZombie.class, EntitySkeleton.class};

    protected static ChunkPosition getRandomSpawningPointInChunk(World world, int chunkX, int chunkZ) {
        int i = chunkX + world.rand.nextInt(16);
        int j = world.rand.nextInt(128);
        int k = chunkZ + world.rand.nextInt(16);
        return new ChunkPosition(i, j, k);
    }

    public static final int performSpawning(World world, boolean spawnAnimals, boolean spawnMonsters) {
        if (!spawnAnimals && !spawnMonsters) {
            return 0;
        }

        eligibleChunksForSpawning.clear();

        for (int i = 0; i < world.playerEntities.size(); i++) {
            EntityPlayer entityplayer = world.playerEntities.get(i);
            int j = MathHelper.floor_double(entityplayer.posX / 16.0);
            int k = MathHelper.floor_double(entityplayer.posZ / 16.0);
            byte b0 = 8;

            for (int l = -b0; l <= b0; l++) {
                for (int i1 = -b0; i1 <= b0; i1++) {
                    eligibleChunksForSpawning.add(new ChunkCoordIntPair(l + j, i1 + k));
                }
            }
        }

        int k3 = 0;
        ChunkCoordinates chunkcoordinates = world.getSpawnPoint();

        for (EnumCreatureType enumcreaturetype : EnumCreatureType.values()) {
            if ((!enumcreaturetype.isPeaceful() || spawnMonsters)
                && (enumcreaturetype.isPeaceful() || spawnAnimals)
                && world.countEntities(enumcreaturetype.getCreatureClass())
                    <= enumcreaturetype.getMaxNumberOfCreature() * eligibleChunksForSpawning.size() / 256) {
                label130:
                for (ChunkCoordIntPair chunkcoordintpair : eligibleChunksForSpawning) {
                    BiomeGenBase biomegenbase = world.getWorldChunkManager().getBiome(chunkcoordintpair);
                    List<BiomeGenBase.SpawnListEntry> list = biomegenbase.getSpawnableList(enumcreaturetype);
                    if (list != null && !list.isEmpty()) {
                        int j1 = 0;

                        for (BiomeGenBase.SpawnListEntry biomegenbase$spawnlistentry : list) {
                            j1 += biomegenbase$spawnlistentry.spawnRarityRate;
                        }

                        int l3 = world.rand.nextInt(j1);
                        BiomeGenBase.SpawnListEntry biomegenbase$spawnlistentry2 = (BiomeGenBase.SpawnListEntry)list.get(0);

                        for (BiomeGenBase.SpawnListEntry biomegenbase$spawnlistentry1 : list) {
                            l3 -= biomegenbase$spawnlistentry1.spawnRarityRate;
                            if (l3 < 0) {
                                biomegenbase$spawnlistentry2 = biomegenbase$spawnlistentry1;
                                break;
                            }
                        }

                        ChunkPosition chunkposition = getRandomSpawningPointInChunk(world, chunkcoordintpair.chunkXPos * 16, chunkcoordintpair.chunkZPos * 16);
                        int i4 = chunkposition.x;
                        int k1 = chunkposition.y;
                        int l1 = chunkposition.z;
                        if (!world.isBlockNormalCube(i4, k1, l1) && world.getBlockMaterial(i4, k1, l1) == enumcreaturetype.getCreatureMaterial()) {
                            int i2 = 0;

                            for (int j2 = 0; j2 < 3; j2++) {
                                int k2 = i4;
                                int l2 = k1;
                                int i3 = l1;
                                byte b1 = 6;

                                for (int j3 = 0; j3 < 4; j3++) {
                                    k2 += world.rand.nextInt(b1) - world.rand.nextInt(b1);
                                    l2 += world.rand.nextInt(1) - world.rand.nextInt(1);
                                    i3 += world.rand.nextInt(b1) - world.rand.nextInt(b1);
                                    if (isValidSpawnPos(enumcreaturetype, world, k2, l2, i3)) {
                                        float f = k2 + 0.5F;
                                        float f1 = l2;
                                        float f2 = i3 + 0.5F;
                                        if (world.getClosestPlayer(f, f1, f2, 24.0) == null) {
                                            float f3 = f - chunkcoordinates.posX;
                                            float f4 = f1 - chunkcoordinates.posY;
                                            float f5 = f2 - chunkcoordinates.posZ;
                                            float f6 = f3 * f3 + f4 * f4 + f5 * f5;
                                            if (!(f6 < 576.0F)) {
                                                EntityLiving entityliving;
                                                try {
                                                    entityliving = biomegenbase$spawnlistentry2.entityClass.getConstructor(World.class).newInstance(world);
                                                } catch (Exception exception) {
                                                    exception.printStackTrace();
                                                    return k3;
                                                }

                                                entityliving.setLocationAndAngles(f, f1, f2, world.rand.nextFloat() * 360.0F, 0.0F);
                                                if (entityliving.getCanSpawnHere()) {
                                                    i2++;
                                                    world.entityJoinedWorld(entityliving);
                                                    postSpawnEntity(entityliving, world, f, f1, f2);
                                                    if (i2 >= entityliving.getMaxSpawnedInChunk()) {
                                                        continue label130;
                                                    }
                                                }

                                                k3 += i2;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return k3;
    }

    private static boolean isValidSpawnPos(final EnumCreatureType category, final World world, final int x, final int y, final int z) {
        if (category.getCreatureMaterial() == Material.water) {
            return world.getBlockMaterial(x, y, z).getIsLiquid() && !world.isBlockNormalCube(x, y + 1, z);
        }
        return world.isBlockNormalCube(x, y - 1, z) && !world.isBlockNormalCube(x, y, z) && !world.getBlockMaterial(x, y, z).getIsLiquid() && !world.isBlockNormalCube(x, y + 1, z);
    }

    private static void postSpawnEntity(EntityLiving entity, World world, float x, float y, float z) {
        if (entity instanceof EntitySpider && world.rand.nextInt(100) == 0) {
            EntitySkeleton entityskeleton = new EntitySkeleton(world);
            entityskeleton.setLocationAndAngles(x, y, z, entity.rotationYaw, 0.0F);
            world.entityJoinedWorld(entityskeleton);
            entityskeleton.mountEntity(entity);
        } else if (entity instanceof EntitySheep) {
            ((EntitySheep)entity).setFleeceColor(EntitySheep.pickColor(world.rand));
        }
    }

    public static boolean performSleepSpawning(World world, List<EntityPlayer> players) {
        boolean flag = false;
        Pathfinder pathfinder = new Pathfinder(world);

        for (EntityPlayer entityplayer : players) {
            Class[] aclass = MONSTER_TYPES;
            if (aclass != null && aclass.length != 0) {
                boolean flag1 = false;

                for (int i = 0; i < 20 && !flag1; i++) {
                    int j = MathHelper.floor_double(entityplayer.posX) + world.rand.nextInt(32) - world.rand.nextInt(32);
                    int k = MathHelper.floor_double(entityplayer.posZ) + world.rand.nextInt(32) - world.rand.nextInt(32);
                    int l = MathHelper.floor_double(entityplayer.posY) + world.rand.nextInt(16) - world.rand.nextInt(16);
                    if (l < 1) {
                        l = 1;
                    } else if (l > 128) {
                        l = 128;
                    }

                    int i1 = world.rand.nextInt(aclass.length);
                    int j1 = l;

                    while (j1 > 2 && !world.isBlockNormalCube(j, j1 - 1, k)) {
                        j1--;
                    }

                    while (!isValidSpawnPos(EnumCreatureType.monster, world, j, j1, k) && j1 < l + 16 && j1 < 128) {
                        j1++;
                    }

                    if (j1 >= l + 16 || j1 >= 128) {
                        j1 = l;
                    } else {
                        float f = j + 0.5F;
                        float f1 = j1;
                        float f2 = k + 0.5F;

                        EntityLiving entityliving;
                        try {
                            entityliving = (EntityLiving)aclass[i1].getConstructor(World.class).newInstance(world);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return flag;
                        }

                        entityliving.setLocationAndAngles(f, f1, f2, world.rand.nextFloat() * 360.0F, 0.0F);
                        if (entityliving.getCanSpawnHere()) {
                            PathEntity pathentity = pathfinder.createEntityPathTo(entityliving, entityplayer, 32.0F);
                            if (pathentity != null && pathentity.pathLength > 1) {
                                PathPoint pathpoint = pathentity.getLastNode();
                                if (Math.abs(pathpoint.xCoord - entityplayer.posX) < 1.5
                                    && Math.abs(pathpoint.zCoord - entityplayer.posZ) < 1.5
                                    && Math.abs(pathpoint.yCoord - entityplayer.posY) < 1.5) {
                                    ChunkCoordinates chunkcoordinates = BlockBed.getSpawnPoint(
                                        world,
                                        MathHelper.floor_double(entityplayer.posX),
                                        MathHelper.floor_double(entityplayer.posY),
                                        MathHelper.floor_double(entityplayer.posZ),
                                        1
                                    );
                                    if (chunkcoordinates == null) {
                                        chunkcoordinates = new ChunkCoordinates(j, j1 + 1, k);
                                    }

                                    entityliving.setLocationAndAngles(
                                        chunkcoordinates.posX + 0.5F, chunkcoordinates.posY, chunkcoordinates.posZ + 0.5F, 0.0F, 0.0F
                                    );
                                    world.entityJoinedWorld(entityliving);
                                    postSpawnEntity(entityliving, world, chunkcoordinates.posX + 0.5F, chunkcoordinates.posY, chunkcoordinates.posZ + 0.5F);
                                    entityplayer.wakeUpPlayer(true, false, false);
                                    entityliving.playLivingSound();
                                    flag = true;
                                    flag1 = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        return flag;
    }
}
