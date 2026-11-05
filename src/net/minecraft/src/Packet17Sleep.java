package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet17Sleep
extends Packet {
    public int id;
    public int x;
    public int y;
    public int z;
    public int action;

    public Packet17Sleep() {
    }

    public Packet17Sleep(Entity player, int action, int x, int y, int z) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = player.entityId;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.id = input.readInt();
        this.action = input.readByte();
        this.x = input.readInt();
        this.y = input.readByte();
        this.z = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.id);
        output.writeByte(this.action);
        output.writeInt(this.x);
        output.writeByte(this.y);
        output.writeInt(this.z);
    }

    public void processPacket(NetHandler handler) {
        handler.handlePlayerSleep(this);
    }

    public int getPacketSize() {
        return 14;
    }
}

