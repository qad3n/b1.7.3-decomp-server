package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet20NamedEntitySpawn
extends Packet {
    public int entityId;
    public String name;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public byte rotation;
    public byte pitch;
    public int currentItem;

    public Packet20NamedEntitySpawn() {
    }

    public Packet20NamedEntitySpawn(EntityPlayer player) {
        this.entityId = player.entityId;
        this.name = player.username;
        this.xPosition = MathHelper.floor_double(player.posX * 32.0);
        this.yPosition = MathHelper.floor_double(player.posY * 32.0);
        this.zPosition = MathHelper.floor_double(player.posZ * 32.0);
        this.rotation = (byte)(player.rotationYaw * 256.0f / 360.0f);
        this.pitch = (byte)(player.rotationPitch * 256.0f / 360.0f);
        ItemStack itemStack = player.inventory.getCurrentItem();
        this.currentItem = itemStack == null ? 0 : itemStack.itemID;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.name = Packet20NamedEntitySpawn.readString(input, 16);
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
        this.rotation = input.readByte();
        this.pitch = input.readByte();
        this.currentItem = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        Packet20NamedEntitySpawn.writeString(this.name, output);
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
        output.writeByte(this.rotation);
        output.writeByte(this.pitch);
        output.writeShort(this.currentItem);
    }

    public void processPacket(NetHandler handler) {
        handler.handleNamedEntitySpawn(this);
    }

    public int getPacketSize() {
        return 28;
    }
}

