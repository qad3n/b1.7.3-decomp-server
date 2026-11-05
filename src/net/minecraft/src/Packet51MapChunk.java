package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int xSize;
    public int ySize;
    public int zSize;
    public byte[] chunk;
    private int chunkSize;

    public Packet51MapChunk() {
        this.isChunkDataPacket = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Packet51MapChunk(int minX, int minY, int minZ, int widthX, int height, int widthZ, World world) {
        this.isChunkDataPacket = true;
        this.xPosition = minX;
        this.yPosition = minY;
        this.zPosition = minZ;
        this.xSize = widthX;
        this.ySize = height;
        this.zSize = widthZ;
        byte[] byArray = world.getChunkData(minX, minY, minZ, widthX, height, widthZ);
        Deflater deflater = new Deflater(-1);
        try {
            deflater.setInput(byArray);
            deflater.finish();
            this.chunk = new byte[widthX * height * widthZ * 5 / 2];
            this.chunkSize = deflater.deflate(this.chunk);
        }
        finally {
            deflater.end();
        }
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.readShort();
        this.zPosition = input.readInt();
        this.xSize = input.read() + 1;
        this.ySize = input.read() + 1;
        this.zSize = input.read() + 1;
        this.chunkSize = input.readInt();
        byte[] byArray = new byte[this.chunkSize];
        input.readFully(byArray);
        this.chunk = new byte[this.xSize * this.ySize * this.zSize * 5 / 2];
        Inflater inflater = new Inflater();
        inflater.setInput(byArray);
        try {
            inflater.inflate(this.chunk);
        }
        catch (DataFormatException dataFormatException) {
            throw new IOException("Bad compressed data format");
        }
        finally {
            inflater.end();
        }
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.writeShort(this.yPosition);
        output.writeInt(this.zPosition);
        output.write(this.xSize - 1);
        output.write(this.ySize - 1);
        output.write(this.zSize - 1);
        output.writeInt(this.chunkSize);
        output.write(this.chunk, 0, this.chunkSize);
    }

    public void processPacket(NetHandler handler) {
        handler.handleMapChunk(this);
    }

    public int getPacketSize() {
        return 17 + this.chunkSize;
    }
}

