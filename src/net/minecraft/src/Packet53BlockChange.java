package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet53BlockChange
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int type;
    public int metadata;

    public Packet53BlockChange() {
        this.isChunkDataPacket = true;
    }

    public Packet53BlockChange(int x, int y, int z, World world) {
        this.isChunkDataPacket = true;
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.type = world.getBlockId(x, y, z);
        this.metadata = world.getBlockMetadata(x, y, z);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.read();
        this.zPosition = input.readInt();
        this.type = input.read();
        this.metadata = input.read();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.write(this.yPosition);
        output.writeInt(this.zPosition);
        output.write(this.type);
        output.write(this.metadata);
    }

    public void processPacket(NetHandler handler) {
        handler.handleBlockChange(this);
    }

    public int getPacketSize() {
        return 11;
    }
}

