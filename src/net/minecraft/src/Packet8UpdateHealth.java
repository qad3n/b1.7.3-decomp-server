package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet8UpdateHealth
extends Packet {
    public int healthMP;

    public Packet8UpdateHealth() {
    }

    public Packet8UpdateHealth(int health) {
        this.healthMP = health;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.healthMP = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeShort(this.healthMP);
    }

    public void processPacket(NetHandler handler) {
        handler.handleHealth(this);
    }

    public int getPacketSize() {
        return 2;
    }
}

