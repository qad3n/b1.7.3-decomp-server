package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet16BlockItemSwitch
extends Packet {
    public int id;

    public void readPacketData(DataInputStream input) throws IOException {
        this.id = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeShort(this.id);
    }

    public void processPacket(NetHandler handler) {
        handler.handleBlockItemSwitch(this);
    }

    public int getPacketSize() {
        return 2;
    }
}

