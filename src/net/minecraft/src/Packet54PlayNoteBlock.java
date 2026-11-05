package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet54PlayNoteBlock
extends Packet {
    public int xLocation;
    public int yLocation;
    public int zLocation;
    public int instrumentType;
    public int pitch;

    public Packet54PlayNoteBlock() {
    }

    public Packet54PlayNoteBlock(int n, int n2, int n3, int n4, int n5) {
        this.xLocation = n;
        this.yLocation = n2;
        this.zLocation = n3;
        this.instrumentType = n4;
        this.pitch = n5;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.xLocation = input.readInt();
        this.yLocation = input.readShort();
        this.zLocation = input.readInt();
        this.instrumentType = input.read();
        this.pitch = input.read();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.xLocation);
        output.writeShort(this.yLocation);
        output.writeInt(this.zLocation);
        output.write(this.instrumentType);
        output.write(this.pitch);
    }

    public void processPacket(NetHandler handler) {
        handler.handleBlockEvent(this);
    }

    public int getPacketSize() {
        return 12;
    }
}

