package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet50PreChunk
extends Packet {
    public int xPosition;
    public int yPosition;
    public boolean mode;

    public Packet50PreChunk() {
        this.isChunkDataPacket = false;
    }

    public Packet50PreChunk(int chunkX, int chunkZ, boolean load) {
        this.isChunkDataPacket = false;
        this.xPosition = chunkX;
        this.yPosition = chunkZ;
        this.mode = load;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.mode = input.read() != 0;
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.write(this.mode ? 1 : 0);
    }

    public void processPacket(NetHandler handler) {
        handler.handlePreChunk(this);
    }

    public int getPacketSize() {
        return 9;
    }
}

