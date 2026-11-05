package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet101CloseWindow
extends Packet {
    public int windowId;

    public Packet101CloseWindow() {
    }

    public Packet101CloseWindow(int id) {
        this.windowId = id;
    }

    public void processPacket(NetHandler handler) {
        handler.handleCraftingGuiClosedPacked(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.windowId = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.windowId);
    }

    public int getPacketSize() {
        return 1;
    }
}

