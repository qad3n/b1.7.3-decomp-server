package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet102WindowClick
extends Packet {
    public int window_Id;
    public int inventorySlot;
    public int mouseClick;
    public short action;
    public ItemStack itemStack;
    public boolean quickMove;

    public void processPacket(NetHandler handler) {
        handler.handleInventoryMenuClickSlot(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.window_Id = input.readByte();
        this.inventorySlot = input.readShort();
        this.mouseClick = input.readByte();
        this.action = input.readShort();
        this.quickMove = input.readBoolean();
        short s = input.readShort();
        if (s >= 0) {
            byte by = input.readByte();
            short s2 = input.readShort();
            this.itemStack = new ItemStack(s, (int)by, (int)s2);
        } else {
            this.itemStack = null;
        }
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.window_Id);
        output.writeShort(this.inventorySlot);
        output.writeByte(this.mouseClick);
        output.writeShort(this.action);
        output.writeBoolean(this.quickMove);
        if (this.itemStack == null) {
            output.writeShort(-1);
        } else {
            output.writeShort(this.itemStack.itemID);
            output.writeByte(this.itemStack.stackSize);
            output.writeShort(this.itemStack.getItemDamage());
        }
    }

    public int getPacketSize() {
        return 11;
    }
}

