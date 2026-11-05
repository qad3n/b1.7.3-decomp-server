package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet2Handshake
extends Packet {
    public String username;

    public Packet2Handshake() {
    }

    public Packet2Handshake(String key) {
        this.username = key;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.username = Packet2Handshake.readString(input, 32);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        Packet2Handshake.writeString(this.username, output);
    }

    public void processPacket(NetHandler handler) {
        handler.handleHandshake(this);
    }

    public int getPacketSize() {
        return 4 + this.username.length() + 4;
    }
}

