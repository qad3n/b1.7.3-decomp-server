package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet34EntityTeleport
extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte yaw;
    public byte pitch;

    public Packet34EntityTeleport() {
    }

    public Packet34EntityTeleport(Entity entity) {
        this.entityId = entity.entityId;
        this.xPosition = MathHelper.floor_double(entity.posX * 32.0);
        this.yPosition = MathHelper.floor_double(entity.posY * 32.0);
        this.zPosition = MathHelper.floor_double(entity.posZ * 32.0);
        this.yaw = (byte)(entity.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(entity.rotationPitch * 256.0f / 360.0f);
    }

    public Packet34EntityTeleport(int id, int x, int y, int z, byte yaw, byte pitch) {
        this.entityId = id;
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
        this.yaw = (byte)input.read();
        this.pitch = (byte)input.read();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
        output.write(this.yaw);
        output.write(this.pitch);
    }

    public void processPacket(NetHandler handler) {
        handler.handleEntityTeleport(this);
    }

    public int getPacketSize() {
        return 34;
    }
}

