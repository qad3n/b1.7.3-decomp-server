package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet22Collect
extends Packet {
    public int collectedEntityId;
    public int collectorEntityId;

    public Packet22Collect() {
    }

    public Packet22Collect(int id, int collectorId) {
        this.collectedEntityId = id;
        this.collectorEntityId = collectorId;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.collectedEntityId = input.readInt();
        this.collectorEntityId = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.collectedEntityId);
        output.writeInt(this.collectorEntityId);
    }

    public void processPacket(NetHandler handler) {
        handler.handleCollect(this);
    }

    public int getPacketSize() {
        return 8;
    }
}

