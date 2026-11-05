package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.MinecraftServer;

public class EntityTracker {
    private Set<EntityTrackerEntry> trackedEntitySet = new HashSet<>();
    private MCHash<EntityTrackerEntry> trackedEntityHashTable = new MCHash<>();
    private MinecraftServer mcServer;
    private int maxTrackingDistanceThreshold;
    private int dimension;

    public EntityTracker(MinecraftServer server, int dimension) {
        this.mcServer = server;
        this.dimension = dimension;
        this.maxTrackingDistanceThreshold = server.configManager.getMaxTrackingDistance();
    }

    public void trackEntity(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            this.trackEntity(entity, 512, 2);
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entity;

            for (EntityTrackerEntry entitytrackerentry : this.trackedEntitySet) {
                if (entitytrackerentry.trackedEntity != entityplayermp) {
                    entitytrackerentry.updatePlayerEntity(entityplayermp);
                }
            }
        } else if (entity instanceof EntityFish) {
            this.trackEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.trackEntity(entity, 64, 20, false);
        } else if (entity instanceof EntityFireball) {
            this.trackEntity(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.trackEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEgg) {
            this.trackEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.trackEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecart) {
            this.trackEntity(entity, 160, 5, true);
        } else if (entity instanceof EntityBoat) {
            this.trackEntity(entity, 160, 5, true);
        } else if (entity instanceof EntitySquid) {
            this.trackEntity(entity, 160, 3, true);
        } else if (entity instanceof IAnimals) {
            this.trackEntity(entity, 160, 3);
        } else if (entity instanceof EntityTNTPrimed) {
            this.trackEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingSand) {
            this.trackEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityPainting) {
            this.trackEntity(entity, 160, Integer.MAX_VALUE, false);
        }
    }

    public void trackEntity(Entity entity, int trackingRange, int updateInterval) {
        this.trackEntity(entity, trackingRange, updateInterval, false);
    }

    public void trackEntity(Entity entity, int trackingRange, int updateInterval, boolean syncVelocity) {
        if (trackingRange > this.maxTrackingDistanceThreshold) {
            trackingRange = this.maxTrackingDistanceThreshold;
        }

        if (this.trackedEntityHashTable.containsItem(entity.entityId)) {
            throw new IllegalStateException("Entity is already tracked!");
        }

        EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, trackingRange, updateInterval, syncVelocity);
        this.trackedEntitySet.add(entitytrackerentry);
        this.trackedEntityHashTable.addKey(entity.entityId, entitytrackerentry);
        entitytrackerentry.updatePlayerEntities(this.mcServer.getWorldManager(this.dimension).playerEntities);
    }

    public void untrackEntity(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entity;

            for (EntityTrackerEntry entitytrackerentry : this.trackedEntitySet) {
                entitytrackerentry.removeFromTrackedPlayers(entityplayermp);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = this.trackedEntityHashTable.removeObject(entity.entityId);
        if (entitytrackerentry1 != null) {
            this.trackedEntitySet.remove(entitytrackerentry1);
            entitytrackerentry1.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void updateTrackedEntities() {
        ArrayList arraylist = new ArrayList();

        for (EntityTrackerEntry entitytrackerentry : this.trackedEntitySet) {
            entitytrackerentry.updatePlayerList(this.mcServer.getWorldManager(this.dimension).playerEntities);
            if (entitytrackerentry.playerEntitiesUpdated && entitytrackerentry.trackedEntity instanceof EntityPlayerMP) {
                arraylist.add((EntityPlayerMP)entitytrackerentry.trackedEntity);
            }
        }

        for (int i = 0; i < arraylist.size(); i++) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)arraylist.get(i);

            for (EntityTrackerEntry entitytrackerentry1 : this.trackedEntitySet) {
                if (entitytrackerentry1.trackedEntity != entityplayermp) {
                    entitytrackerentry1.updatePlayerEntity(entityplayermp);
                }
            }
        }
    }

    public void sendPacketToTrackedPlayers(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(entity.entityId);
        if (entitytrackerentry != null) {
            entitytrackerentry.sendPacketToTrackedPlayers(packet);
        }
    }

    public void sendPacketToTrackedPlayersAndTrackedEntity(Entity entity, Packet packet) {
        EntityTrackerEntry entitytrackerentry = this.trackedEntityHashTable.lookup(entity.entityId);
        if (entitytrackerentry != null) {
            entitytrackerentry.sendPacketToTrackedPlayersAndTrackedEntity(packet);
        }
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP player) {
        for (EntityTrackerEntry entitytrackerentry : this.trackedEntitySet) {
            entitytrackerentry.removeTrackedPlayerSymmetric(player);
        }
    }
}
