package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet4UpdateTime
extends Packet {
    public long time;

    public Packet4UpdateTime() {
    }

    public Packet4UpdateTime(long l) {
        this.time = l;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.time = input.readLong();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeLong(this.time);
    }

    public void processPacket(NetHandler handler) {
        handler.handleUpdateTime(this);
    }

    public int getPacketSize() {
        return 8;
    }
}

