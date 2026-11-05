package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet103SetSlot
extends Packet {
    public int windowId;
    public int itemSlot;
    public ItemStack myItemStack;

    public Packet103SetSlot() {
    }

    public Packet103SetSlot(int menuId, int slot, ItemStack item) {
        this.windowId = menuId;
        this.itemSlot = slot;
        this.myItemStack = item == null ? item : item.copy();
    }

    public void processPacket(NetHandler handler) {
        handler.handleInventoryMenuSlotContent(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.windowId = input.readByte();
        this.itemSlot = input.readShort();
        short s = input.readShort();
        if (s >= 0) {
            byte by = input.readByte();
            short s2 = input.readShort();
            this.myItemStack = new ItemStack(s, (int)by, (int)s2);
        } else {
            this.myItemStack = null;
        }
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.windowId);
        output.writeShort(this.itemSlot);
        if (this.myItemStack == null) {
            output.writeShort(-1);
        } else {
            output.writeShort(this.myItemStack.itemID);
            output.writeByte(this.myItemStack.stackSize);
            output.writeShort(this.myItemStack.getItemDamage());
        }
    }

    public int getPacketSize() {
        return 8;
    }
}

