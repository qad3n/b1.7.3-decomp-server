package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet105UpdateProgressbar
extends Packet {
    public int windowId;
    public int progressBar;
    public int progressBarValue;

    public Packet105UpdateProgressbar() {
    }

    public Packet105UpdateProgressbar(int menuId, int dataId, int value) {
        this.windowId = menuId;
        this.progressBar = dataId;
        this.progressBarValue = value;
    }

    public void processPacket(NetHandler handler) {
        handler.handleInventoryMenuData(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.windowId = input.readByte();
        this.progressBar = input.readShort();
        this.progressBarValue = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.windowId);
        output.writeShort(this.progressBar);
        output.writeShort(this.progressBarValue);
    }

    public int getPacketSize() {
        return 5;
    }
}

