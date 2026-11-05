package net.minecraft.src;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityTrackerEntry {
    public Entity trackedEntity;
    public int trackingDistanceThreshold;
    public int updateInterval;
    public int encodedPosX;
    public int encodedPosY;
    public int encodedPosZ;
    public int encodedRotationYaw;
    public int encodedRotationPitch;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double lastTrackedEntityMotionZ;
    public int updateCounter = 0;
    private double lastTrackedEntityPosX;
    private double lastTrackedEntityPosY;
    private double lastTrackedEntityPosZ;
    private boolean firstUpdateDone = false;
    private boolean shouldSendMotionUpdates;
    private int ticksWithoutVehicle = 0;
    public boolean playerEntitiesUpdated = false;
    public Set<EntityPlayerMP> trackedPlayers = new HashSet<>();

    public EntityTrackerEntry(Entity entity, int trackingRange, int updateInterval, boolean syncVelocity) {
        this.trackedEntity = entity;
        this.trackingDistanceThreshold = trackingRange;
        this.updateInterval = updateInterval;
        this.shouldSendMotionUpdates = syncVelocity;
        this.encodedPosX = MathHelper.floor_double(entity.posX * 32.0);
        this.encodedPosY = MathHelper.floor_double(entity.posY * 32.0);
        this.encodedPosZ = MathHelper.floor_double(entity.posZ * 32.0);
        this.encodedRotationYaw = MathHelper.floor_float(entity.rotationYaw * 256.0F / 360.0F);
        this.encodedRotationPitch = MathHelper.floor_float(entity.rotationPitch * 256.0F / 360.0F);
    }

    @Override
    public boolean equals(Object var1) {
        return var1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)var1).trackedEntity.entityId == this.trackedEntity.entityId : false;
    }

    @Override
    public int hashCode() {
        return this.trackedEntity.entityId;
    }

    public void updatePlayerList(List<EntityPlayer> players) {
        this.playerEntitiesUpdated = false;
        if (!this.firstUpdateDone
            || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
            this.lastTrackedEntityPosX = this.trackedEntity.posX;
            this.lastTrackedEntityPosY = this.trackedEntity.posY;
            this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
            this.firstUpdateDone = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(players);
        }

        this.ticksWithoutVehicle++;
        if (++this.updateCounter % this.updateInterval == 0) {
            int i = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int j = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int k = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int l = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int i1 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int j1 = i - this.encodedPosX;
            int k1 = j - this.encodedPosY;
            int l1 = k - this.encodedPosZ;
            Packet packet = null;
            boolean flag = Math.abs(i) >= 8 || Math.abs(j) >= 8 || Math.abs(k) >= 8;
            boolean flag1 = Math.abs(l - this.encodedRotationYaw) >= 8 || Math.abs(i1 - this.encodedRotationPitch) >= 8;
            if (j1 < -128 || j1 >= 128 || k1 < -128 || k1 >= 128 || l1 < -128 || l1 >= 128 || this.ticksWithoutVehicle > 400) {
                this.ticksWithoutVehicle = 0;
                this.trackedEntity.posX = i / 32.0;
                this.trackedEntity.posY = j / 32.0;
                this.trackedEntity.posZ = k / 32.0;
                packet = new Packet34EntityTeleport(this.trackedEntity.entityId, i, j, k, (byte)l, (byte)i1);
            } else if (flag && flag1) {
                packet = new Packet30Entity.Packet33RelEntityMoveLook(this.trackedEntity.entityId, (byte)j1, (byte)k1, (byte)l1, (byte)l, (byte)i1);
            } else if (flag) {
                packet = new Packet30Entity.Packet31RelEntityMove(this.trackedEntity.entityId, (byte)j1, (byte)k1, (byte)l1);
            } else if (flag1) {
                packet = new Packet30Entity.Packet32EntityLook(this.trackedEntity.entityId, (byte)l, (byte)i1);
            }

            if (this.shouldSendMotionUpdates) {
                double d0 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
                double d1 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
                double d2 = this.trackedEntity.motionZ - this.lastTrackedEntityMotionZ;
                double d3 = 0.02;
                double d4 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d4 > d3 * d3 || d4 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0) {
                    this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                    this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                    this.lastTrackedEntityMotionZ = this.trackedEntity.motionZ;
                    this.sendPacketToTrackedPlayers(
                        new Packet28EntityVelocity(
                            this.trackedEntity.entityId, this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.lastTrackedEntityMotionZ
                        )
                    );
                }
            }

            if (packet != null) {
                this.sendPacketToTrackedPlayers(packet);
            }

            DataWatcher datawatcher = this.trackedEntity.getDataWatcher();
            if (datawatcher.hasObjectChanged()) {
                this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet40EntityMetadata(this.trackedEntity.entityId, datawatcher));
            }

            if (flag) {
                this.encodedPosX = i;
                this.encodedPosY = j;
                this.encodedPosZ = k;
            }

            if (flag1) {
                this.encodedRotationYaw = l;
                this.encodedRotationPitch = i1;
            }
        }

        if (this.trackedEntity.beenAttacked) {
            this.sendPacketToTrackedPlayersAndTrackedEntity(new Packet28EntityVelocity(this.trackedEntity));
            this.trackedEntity.beenAttacked = false;
        }
    }

    public void sendPacketToTrackedPlayers(Packet packet) {
        for (EntityPlayerMP entityplayermp : this.trackedPlayers) {
            entityplayermp.playerNetServerHandler.sendPacket(packet);
        }
    }

    public void sendPacketToTrackedPlayersAndTrackedEntity(Packet packet) {
        this.sendPacketToTrackedPlayers(packet);
        if (this.trackedEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packet);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers() {
        this.sendPacketToTrackedPlayers(new Packet29DestroyEntity(this.trackedEntity.entityId));
    }

    public void removeFromTrackedPlayers(EntityPlayerMP player) {
        if (this.trackedPlayers.contains(player)) {
            this.trackedPlayers.remove(player);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP player) {
        if (player != this.trackedEntity) {
            double d0 = player.posX - this.encodedPosX / 32;
            double d1 = player.posZ - this.encodedPosZ / 32;
            if (d0 >= -this.trackingDistanceThreshold
                && d0 <= this.trackingDistanceThreshold
                && d1 >= -this.trackingDistanceThreshold
                && d1 <= this.trackingDistanceThreshold) {
                if (!this.trackedPlayers.contains(player)) {
                    this.trackedPlayers.add(player);
                    player.playerNetServerHandler.sendPacket(this.getSpawnPacket());
                    if (this.shouldSendMotionUpdates) {
                        player.playerNetServerHandler
                            .sendPacket(
                                new Packet28EntityVelocity(
                                    this.trackedEntity.entityId, this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                                )
                            );
                    }

                    ItemStack[] aitemstack = this.trackedEntity.getInventory();
                    if (aitemstack != null) {
                        for (int i = 0; i < aitemstack.length; i++) {
                            player.playerNetServerHandler.sendPacket(new Packet5PlayerInventory(this.trackedEntity.entityId, i, aitemstack[i]));
                        }
                    }

                    if (this.trackedEntity instanceof EntityPlayer) {
                        EntityPlayer entityplayer = (EntityPlayer)this.trackedEntity;
                        if (entityplayer.isSleeping()) {
                            player.playerNetServerHandler
                                .sendPacket(
                                    new Packet17Sleep(
                                        this.trackedEntity,
                                        0,
                                        MathHelper.floor_double(this.trackedEntity.posX),
                                        MathHelper.floor_double(this.trackedEntity.posY),
                                        MathHelper.floor_double(this.trackedEntity.posZ)
                                    )
                                );
                        }
                    }
                }
            } else if (this.trackedPlayers.contains(player)) {
                this.trackedPlayers.remove(player);
                player.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
            }
        }
    }

    public void updatePlayerEntities(List<EntityPlayer> players) {
        for (int i = 0; i < players.size(); i++) {
            this.updatePlayerEntity((EntityPlayerMP)players.get(i));
        }
    }

    private Packet getSpawnPacket() {
        if (this.trackedEntity instanceof EntityItem) {
            EntityItem entityitem = (EntityItem)this.trackedEntity;
            Packet21PickupSpawn packet21pickupspawn = new Packet21PickupSpawn(entityitem);
            entityitem.posX = packet21pickupspawn.xPosition / 32.0;
            entityitem.posY = packet21pickupspawn.yPosition / 32.0;
            entityitem.posZ = packet21pickupspawn.zPosition / 32.0;
            return packet21pickupspawn;
        }

        if (this.trackedEntity instanceof EntityPlayerMP) {
            return new Packet20NamedEntitySpawn((EntityPlayer)this.trackedEntity);
        }

        if (this.trackedEntity instanceof EntityMinecart) {
            EntityMinecart entityminecart = (EntityMinecart)this.trackedEntity;
            if (entityminecart.minecartType == 0) {
                return new Packet23VehicleSpawn(this.trackedEntity, 10);
            }

            if (entityminecart.minecartType == 1) {
                return new Packet23VehicleSpawn(this.trackedEntity, 11);
            }

            if (entityminecart.minecartType == 2) {
                return new Packet23VehicleSpawn(this.trackedEntity, 12);
            }
        }

        if (this.trackedEntity instanceof EntityBoat) {
            return new Packet23VehicleSpawn(this.trackedEntity, 1);
        }

        if (this.trackedEntity instanceof IAnimals) {
            return new Packet24MobSpawn((EntityLiving)this.trackedEntity);
        }

        if (this.trackedEntity instanceof EntityFish) {
            return new Packet23VehicleSpawn(this.trackedEntity, 90);
        }

        if (this.trackedEntity instanceof EntityArrow) {
            EntityLiving entityliving = ((EntityArrow)this.trackedEntity).owner;
            return new Packet23VehicleSpawn(this.trackedEntity, 60, entityliving != null ? entityliving.entityId : this.trackedEntity.entityId);
        }

        if (this.trackedEntity instanceof EntitySnowball) {
            return new Packet23VehicleSpawn(this.trackedEntity, 61);
        }

        if (this.trackedEntity instanceof EntityFireball) {
            EntityFireball entityfireball = (EntityFireball)this.trackedEntity;
            Packet23VehicleSpawn packet23vehiclespawn = new Packet23VehicleSpawn(this.trackedEntity, 63, ((EntityFireball)this.trackedEntity).owner.entityId);
            packet23vehiclespawn.velocityX = (int)(entityfireball.accelerationX * 8000.0);
            packet23vehiclespawn.velocityY = (int)(entityfireball.accelerationY * 8000.0);
            packet23vehiclespawn.velocityZ = (int)(entityfireball.accelerationZ * 8000.0);
            return packet23vehiclespawn;
        }

        if (this.trackedEntity instanceof EntityEgg) {
            return new Packet23VehicleSpawn(this.trackedEntity, 62);
        }

        if (this.trackedEntity instanceof EntityTNTPrimed) {
            return new Packet23VehicleSpawn(this.trackedEntity, 50);
        }

        if (this.trackedEntity instanceof EntityFallingSand) {
            EntityFallingSand entityfallingsand = (EntityFallingSand)this.trackedEntity;
            if (entityfallingsand.blockID == Block.sand.blockID) {
                return new Packet23VehicleSpawn(this.trackedEntity, 70);
            }

            if (entityfallingsand.blockID == Block.gravel.blockID) {
                return new Packet23VehicleSpawn(this.trackedEntity, 71);
            }
        }

        if (this.trackedEntity instanceof EntityPainting) {
            return new Packet25EntityPainting((EntityPainting)this.trackedEntity);
        } else {
            throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
        }
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP player) {
        if (this.trackedPlayers.contains(player)) {
            this.trackedPlayers.remove(player);
            player.playerNetServerHandler.sendPacket(new Packet29DestroyEntity(this.trackedEntity.entityId));
        }
    }
}
