package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet15Place
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int direction;
    public ItemStack itemStack;

    public void readPacketData(DataInputStream input) throws IOException {
        this.xPosition = input.readInt();
        this.yPosition = input.read();
        this.zPosition = input.readInt();
        this.direction = input.read();
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
        output.writeInt(this.xPosition);
        output.write(this.yPosition);
        output.writeInt(this.zPosition);
        output.write(this.direction);
        if (this.itemStack == null) {
            output.writeShort(-1);
        } else {
            output.writeShort(this.itemStack.itemID);
            output.writeByte(this.itemStack.stackSize);
            output.writeShort(this.itemStack.getItemDamage());
        }
    }

    public void processPacket(NetHandler handler) {
        handler.handlePlace(this);
    }

    public int getPacketSize() {
        return 15;
    }
}

