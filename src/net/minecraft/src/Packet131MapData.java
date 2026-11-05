package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet131MapData
extends Packet {
    public short type;
    public short id;
    public byte[] data;

    public Packet131MapData() {
        this.isChunkDataPacket = true;
    }

    public Packet131MapData(short type, short id, byte[] data) {
        this.isChunkDataPacket = true;
        this.type = type;
        this.id = id;
        this.data = data;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.type = input.readShort();
        this.id = input.readShort();
        this.data = new byte[input.readByte() & 0xFF];
        input.readFully(this.data);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeShort(this.type);
        output.writeShort(this.id);
        output.writeByte(this.data.length);
        output.write(this.data);
    }

    public void processPacket(NetHandler handler) {
        handler.handleMapData(this);
    }

    public int getPacketSize() {
        return 4 + this.data.length;
    }
}

