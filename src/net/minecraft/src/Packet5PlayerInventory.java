package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet5PlayerInventory
extends Packet {
    public int entityID;
    public int slot;
    public int itemID;
    public int itemDamage;

    public Packet5PlayerInventory() {
    }

    public Packet5PlayerInventory(int id, int slot, ItemStack item) {
        this.entityID = id;
        this.slot = slot;
        if (item == null) {
            this.itemID = -1;
            this.itemDamage = 0;
        } else {
            this.itemID = item.itemID;
            this.itemDamage = item.getItemDamage();
        }
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityID = input.readInt();
        this.slot = input.readShort();
        this.itemID = input.readShort();
        this.itemDamage = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityID);
        output.writeShort(this.slot);
        output.writeShort(this.itemID);
        output.writeShort(this.itemDamage);
    }

    public void processPacket(NetHandler handler) {
        handler.handlePlayerInventory(this);
    }

    public int getPacketSize() {
        return 8;
    }
}

