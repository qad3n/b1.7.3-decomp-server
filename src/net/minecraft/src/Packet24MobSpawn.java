package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet24MobSpawn
extends Packet {
    public int entityId;
    public byte type;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte yaw;
    public byte pitch;
    private DataWatcher metaData;
    private List<DataWatcher.WatchableObject> receivedMetadata;

    public Packet24MobSpawn() {
    }

    public Packet24MobSpawn(EntityLiving entity) {
        this.entityId = entity.entityId;
        this.type = (byte)EntityList.getEntityID(entity);
        this.xPosition = MathHelper.floor_double(entity.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entity.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entity.posZ * 32.0);
        this.yaw = (byte)(entity.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(entity.rotationPitch * 256.0f / 360.0f);
        this.metaData = entity.getDataWatcher();
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.type = input.readByte();
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
        this.yaw = input.readByte();
        this.pitch = input.readByte();
        this.receivedMetadata = DataWatcher.readWatchableObjects(input);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeByte(this.type);
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
        output.writeByte(this.yaw);
        output.writeByte(this.pitch);
        this.metaData.writeWatchableObjects(output);
    }

    public void processPacket(NetHandler handler) {
        handler.handleMobSpawn(this);
    }

    public int getPacketSize() {
        return 20;
    }
}

