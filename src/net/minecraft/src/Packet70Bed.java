package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet70Bed
extends Packet {
    public static final String[] EVENT_MESSAGES = new String[]{"tile.bed.notValid", null, null};
    public int event;

    public Packet70Bed() {
    }

    public Packet70Bed(int n) {
        this.event = n;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.event = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.event);
    }

    public void processPacket(NetHandler handler) {
        handler.handleGameEvent(this);
    }

    public int getPacketSize() {
        return 1;
    }
}

