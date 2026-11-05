package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet18Animation
extends Packet {
    public int entityId;
    public int animate;

    public Packet18Animation() {
    }

    public Packet18Animation(Entity entity, int action) {
        this.entityId = entity.entityId;
        this.animate = action;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.animate = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeByte(this.animate);
    }

    public void processPacket(NetHandler handler) {
        handler.handleArmAnimation(this);
    }

    public int getPacketSize() {
        return 5;
    }
}

