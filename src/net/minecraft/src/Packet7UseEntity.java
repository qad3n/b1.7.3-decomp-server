package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet7UseEntity
extends Packet {
    public int playerEntityId;
    public int targetEntity;
    public int isLeftClick;

    public void readPacketData(DataInputStream input) throws IOException {
        this.playerEntityId = input.readInt();
        this.targetEntity = input.readInt();
        this.isLeftClick = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.playerEntityId);
        output.writeInt(this.targetEntity);
        output.writeByte(this.isLeftClick);
    }

    public void processPacket(NetHandler handler) {
        handler.handleInteractEntity(this);
    }

    public int getPacketSize() {
        return 9;
    }
}

