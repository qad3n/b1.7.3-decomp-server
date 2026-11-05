package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet38EntityStatus
extends Packet {
    public int entityId;
    public byte entityStatus;

    public Packet38EntityStatus() {
    }

    public Packet38EntityStatus(int id, byte event) {
        this.entityId = id;
        this.entityStatus = event;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.entityStatus = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeByte(this.entityStatus);
    }

    public void processPacket(NetHandler handler) {
        handler.handleEntityEvent(this);
    }

    public int getPacketSize() {
        return 5;
    }
}

