package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet61DoorChange
extends Packet {
    public int event;
    public int data;
    public int x;
    public int y;
    public int z;

    public Packet61DoorChange() {
    }

    public Packet61DoorChange(int n, int n2, int n3, int n4, int n5) {
        this.event = n;
        this.x = n2;
        this.y = n3;
        this.z = n4;
        this.data = n5;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.event = input.readInt();
        this.x = input.readInt();
        this.y = input.readByte();
        this.z = input.readInt();
        this.data = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.event);
        output.writeInt(this.x);
        output.writeByte(this.y);
        output.writeInt(this.z);
        output.writeInt(this.data);
    }

    public void processPacket(NetHandler handler) {
        handler.handleWorldEvent(this);
    }

    public int getPacketSize() {
        return 20;
    }
}

