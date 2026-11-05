package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class WorldServer extends World {
    public ChunkProviderServer chunkProviderServer;
    public boolean bypassSpawnProtection = false;
    public boolean levelSaving;
    private MinecraftServer mcServer;
    private MCHash<Entity> entitiesById = new MCHash<>();

    public WorldServer(MinecraftServer server, ISaveHandler storage, String name, int dimension, long seed) {
        super(storage, name, seed, WorldProvider.fromId(dimension));
        this.mcServer = server;
    }

    @Override
    public void updateEntityWithOptionalForce(Entity entity, boolean requireLoaded) {
        if (!this.mcServer.spawnPeacefulMobs && (entity instanceof EntityAnimal || entity instanceof EntityWaterMob)) {
            entity.setEntityDead();
        }

        if (entity.riddenByEntity == null || !(entity.riddenByEntity instanceof EntityPlayer)) {
            super.updateEntityWithOptionalForce(entity, requireLoaded);
        }
    }

    public void tickVehicle(Entity vehicle, boolean requireLoaded) {
        super.updateEntityWithOptionalForce(vehicle, requireLoaded);
    }

    @Override
    protected IChunkProvider getChunkProvider() {
        IChunkLoader ichunkloader = this.worldFile.getChunkStorage(this.worldProvider);
        this.chunkProviderServer = new ChunkProviderServer(this, ichunkloader, this.worldProvider.getChunkProvider());
        return this.chunkProviderServer;
    }

    public List<TileEntity> getTileEntityList(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        ArrayList arraylist = new ArrayList();

        for (int i = 0; i < this.loadedTileEntityList.size(); i++) {
            TileEntity tileentity = this.loadedTileEntityList.get(i);
            if (tileentity.xCoord >= minX
                && tileentity.yCoord >= minY
                && tileentity.zCoord >= minZ
                && tileentity.xCoord < maxX
                && tileentity.yCoord < maxY
                && tileentity.zCoord < maxZ) {
                arraylist.add(tileentity);
            }
        }

        return arraylist;
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
        int i = (int)MathHelper.abs(x - this.worldInfo.getSpawnX());
        int j = (int)MathHelper.abs(z - this.worldInfo.getSpawnZ());
        if (i > j) {
            j = i;
        }

        return j > 16 || this.mcServer.configManager.isOp(player.username);
    }

    @Override
    protected void obtainEntitySkin(Entity entity) {
        super.obtainEntitySkin(entity);
        this.entitiesById.addKey(entity.entityId, entity);
    }

    @Override
    protected void releaseEntitySkin(Entity entity) {
        super.releaseEntitySkin(entity);
        this.entitiesById.removeObject(entity.entityId);
    }

    public Entity getEntity(int networkId) {
        return this.entitiesById.lookup(networkId);
    }

    @Override
    public boolean addLightningBolt(Entity entity) {
        if (super.addLightningBolt(entity)) {
            this.mcServer
                .configManager
                .sendPacketToPlayersAroundPoint(entity.posX, entity.posY, entity.posZ, 512.0, this.worldProvider.worldType, new Packet71Weather(entity));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void sendTrackedEntityStatusUpdatePacket(Entity entity, byte event) {
        Packet38EntityStatus packet38entitystatus = new Packet38EntityStatus(entity.entityId, event);
        this.mcServer.getEntityTracker(this.worldProvider.worldType).sendPacketToTrackedPlayersAndTrackedEntity(entity, packet38entitystatus);
    }

    @Override
    public Explosion newExplosion(Entity source, double x, double y, double z, float power, boolean createFire) {
        Explosion explosion = new Explosion(this, source, x, y, z, power);
        explosion.isFlaming = createFire;
        explosion.doExplosion();
        explosion.doEffects(false);
        this.mcServer
            .configManager
            .sendPacketToPlayersAroundPoint(
                x, y, z, 64.0, this.worldProvider.worldType, new Packet60Explosion(x, y, z, power, explosion.destroyedBlockPositions)
            );
        return explosion;
    }

    @Override
    public void playNoteAt(int x, int y, int z, int type, int data) {
        super.playNoteAt(x, y, z, type, data);
        this.mcServer.configManager.sendPacketToPlayersAroundPoint(x, y, z, 64.0, this.worldProvider.worldType, new Packet54PlayNoteBlock(x, y, z, type, data));
    }

    public void forceSave() {
        this.worldFile.forceSave();
    }

    @Override
    protected void updateWeather() {
        boolean flag = this.isRaining();
        super.updateWeather();
        if (flag != this.isRaining()) {
            if (flag) {
                this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(2));
            } else {
                this.mcServer.configManager.sendPacketToAllPlayers(new Packet70Bed(1));
            }
        }
    }
}
