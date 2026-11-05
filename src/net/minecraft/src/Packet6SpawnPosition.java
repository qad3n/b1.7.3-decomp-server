package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet6SpawnPosition
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;

    public Packet6SpawnPosition() {
    }

    public Packet6SpawnPosition(int x, int y, int z) {
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
    }

    public void processPacket(NetHandler handler) {
        handler.handleSpawnPosition(this);
    }

    public int getPacketSize() {
        return 12;
    }
}

