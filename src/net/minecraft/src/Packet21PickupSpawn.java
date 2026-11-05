package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet21PickupSpawn
extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte rotation;
    public byte pitch;
    public byte roll;
    public int itemID;
    public int count;
    public int itemDamage;

    public Packet21PickupSpawn() {
    }

    public Packet21PickupSpawn(EntityItem item) {
        this.entityId = item.entityId;
        this.itemID = item.item.itemID;
        this.count = item.item.stackSize;
        this.itemDamage = item.item.getItemDamage();
        this.xPosition = MathHelper.floor_double(item.posX * 32.0);
        this.yPosition = MathHelper.floor_double(item.posY * 32.0);
        this.zPosition = MathHelper.floor_double(item.posZ * 32.0);
        this.rotation = (byte)(item.motionX * 128.0);
        this.pitch = (byte)(item.motionY * 128.0);
        this.roll = (byte)(item.motionZ * 128.0);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.itemID = input.readShort();
        this.count = input.readByte();
        this.itemDamage = input.readShort();
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
        this.rotation = input.readByte();
        this.pitch = input.readByte();
        this.roll = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeShort(this.itemID);
        output.writeByte(this.count);
        output.writeShort(this.itemDamage);
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
        output.writeByte(this.rotation);
        output.writeByte(this.pitch);
        output.writeByte(this.roll);
    }

    public void processPacket(NetHandler handler) {
        handler.handlePickupSpawn(this);
    }

    public int getPacketSize() {
        return 24;
    }
}

