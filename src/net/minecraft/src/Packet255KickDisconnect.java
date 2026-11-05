package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet255KickDisconnect
extends Packet {
    public String reason;

    public Packet255KickDisconnect() {
    }

    public Packet255KickDisconnect(String reason) {
        this.reason = reason;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.reason = Packet255KickDisconnect.readString(input, 100);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        Packet255KickDisconnect.writeString(this.reason, output);
    }

    public void processPacket(NetHandler handler) {
        handler.handleKickDisconnect(this);
    }

    public int getPacketSize() {
        return this.reason.length();
    }
}

