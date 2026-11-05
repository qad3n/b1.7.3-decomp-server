package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet106Transaction
extends Packet {
    public int windowId;
    public short shortWindowId;
    public boolean accepted;

    public Packet106Transaction() {
    }

    public Packet106Transaction(int menuId, short interactionId, boolean accepted) {
        this.windowId = menuId;
        this.shortWindowId = interactionId;
        this.accepted = accepted;
    }

    public void processPacket(NetHandler handler) {
        handler.handleInventoryMenuConfirm(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.windowId = input.readByte();
        this.shortWindowId = input.readShort();
        this.accepted = input.readByte() != 0;
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.windowId);
        output.writeShort(this.shortWindowId);
        output.writeByte(this.accepted ? 1 : 0);
    }

    public int getPacketSize() {
        return 4;
    }
}

