package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet9Respawn
extends Packet {
    public byte dimension;

    public Packet9Respawn() {
    }

    public Packet9Respawn(byte dimensionId) {
        this.dimension = dimensionId;
    }

    public void processPacket(NetHandler handler) {
        handler.handleRespawnPacket(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.dimension = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.dimension);
    }

    public int getPacketSize() {
        return 1;
    }
}

