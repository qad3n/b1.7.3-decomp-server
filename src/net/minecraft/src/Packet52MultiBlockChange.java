package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet52MultiBlockChange
extends Packet {
    public int xPosition;
    public int zPosition;
    public short[] coordinateArray;
    public byte[] typeArray;
    public byte[] metadataArray;
    public int size;

    public Packet52MultiBlockChange() {
        this.isChunkDataPacket = true;
    }

    public Packet52MultiBlockChange(int chunkX, int chunkZ, short[] positions, int blockChangeCount, World world) {
        this.isChunkDataPacket = true;
        this.xPosition = chunkX;
        this.zPosition = chunkZ;
        this.size = blockChangeCount;
        this.coordinateArray = new short[blockChangeCount];
        this.typeArray = new byte[blockChangeCount];
        this.metadataArray = new byte[blockChangeCount];
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        for (int i = 0; i < blockChangeCount; ++i) {
            int n = positions[i] >> 12 & 0xF;
            int n2 = positions[i] >> 8 & 0xF;
            int n3 = positions[i] & 0xFF;
            this.coordinateArray[i] = positions[i];
            this.typeArray[i] = (byte)chunk.getBlockID(n, n3, n2);
            this.metadataArray[i] = (byte)chunk.getBlockMetadata(n, n3, n2);
        }
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.zPosition = input.readInt();
        this.size = input.readShort() & 0xFFFF;
        this.coordinateArray = new short[this.size];
        this.typeArray = new byte[this.size];
        this.metadataArray = new byte[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.coordinateArray[i] = input.readShort();
        }
        input.readFully(this.typeArray);
        input.readFully(this.metadataArray);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.writeInt(this.zPosition);
        output.writeShort((short)this.size);
        for (int i = 0; i < this.size; ++i) {
            output.writeShort(this.coordinateArray[i]);
        }
        output.write(this.typeArray);
        output.write(this.metadataArray);
    }

    public void processPacket(NetHandler handler) {
        handler.handleMultiBlockChange(this);
    }

    public int getPacketSize() {
        return 10 + this.size * 4;
    }
}

