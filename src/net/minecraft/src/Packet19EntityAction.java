package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet19EntityAction
extends Packet {
    public int entityId;
    public int state;

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.state = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeByte(this.state);
    }

    public void processPacket(NetHandler handler) {
        handler.handlePlayerMovementAction(this);
    }

    public int getPacketSize() {
        return 5;
    }
}

