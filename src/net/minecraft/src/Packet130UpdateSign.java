package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet130UpdateSign
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public String[] signLines;

    public Packet130UpdateSign() {
        this.isChunkDataPacket = true;
    }

    public Packet130UpdateSign(int x, int y, int z, String[] lines) {
        this.isChunkDataPacket = true;
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        this.signLines = lines;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.readShort();
        this.zPosition = input.readInt();
        this.signLines = new String[4];
        for (int i = 0; i < 4; ++i) {
            this.signLines[i] = Packet130UpdateSign.readString(input, 15);
        }
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xPosition);
        output.writeShort(this.yPosition);
        output.writeInt(this.zPosition);
        for (int i = 0; i < 4; ++i) {
            Packet130UpdateSign.writeString(this.signLines[i], output);
        }
    }

    public void processPacket(NetHandler handler) {
        handler.handleUpdateSign(this);
    }

    public int getPacketSize() {
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n += this.signLines[i].length();
        }
        return n;
    }
}

