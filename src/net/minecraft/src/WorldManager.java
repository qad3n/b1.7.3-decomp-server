package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class WorldManager
implements IWorldAccess {
    private MinecraftServer mcServer;
    private WorldServer world;

    public WorldManager(MinecraftServer server, WorldServer world) {
        this.mcServer = server;
        this.world = world;
    }

    public void spawnParticle(String type, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    }

    public void obtainEntitySkin(Entity entity) {
        this.mcServer.getEntityTracker(this.world.worldProvider.worldType).trackEntity(entity);
    }

    public void releaseEntitySkin(Entity entity) {
        this.mcServer.getEntityTracker(this.world.worldProvider.worldType).untrackEntity(entity);
    }

    public void playSound(String name, double x, double y, double z, float volume, float pitch) {
    }

    public void markBlockRangeNeedsUpdate(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    }

    public void updateAllRenderers() {
    }

    public void markBlockNeedsUpdate(int x, int y, int z) {
        this.mcServer.configManager.markBlockNeedsUpdate(x, y, z, this.world.worldProvider.worldType);
    }

    public void playRecord(String record, int x, int y, int z) {
    }

    public void doNothingWithTileEntity(int x, int y, int z, TileEntity blockEntity) {
        this.mcServer.configManager.sentTileEntityToPlayer(x, y, z, blockEntity);
    }

    public void doEvent(EntityPlayer source, int type, int x, int y, int z, int data) {
        this.mcServer.configManager.sendPacket(source, x, y, z, 64.0, this.world.worldProvider.worldType, new Packet61DoorChange(type, x, y, z, data));
    }
}

