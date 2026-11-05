package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet104WindowItems extends Packet {
    public int windowId;
    public ItemStack[] itemStack;

    public Packet104WindowItems() {
    }

    public Packet104WindowItems(int menuId, List<ItemStack> items) {
        this.windowId = menuId;
        this.itemStack = new ItemStack[items.size()];

        for (int i = 0; i < this.itemStack.length; i++) {
            ItemStack itemstack = (ItemStack)items.get(i);
            this.itemStack[i] = itemstack == null ? null : itemstack.copy();
        }
    }

    @Override
    public void readPacketData(DataInputStream input) throws IOException {
        this.windowId = input.readByte();
        short short1 = input.readShort();
        this.itemStack = new ItemStack[short1];

        for (int i = 0; i < short1; i++) {
            short short2 = input.readShort();
            if (short2 >= 0) {
                byte b0 = input.readByte();
                short short3 = input.readShort();
                this.itemStack[i] = new ItemStack(short2, b0, short3);
            }
        }
    }

    @Override
    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeByte(this.windowId);
        output.writeShort(this.itemStack.length);

        for (int i = 0; i < this.itemStack.length; i++) {
            if (this.itemStack[i] == null) {
                output.writeShort(-1);
            } else {
                output.writeShort((short)this.itemStack[i].itemID);
                output.writeByte((byte)this.itemStack[i].stackSize);
                output.writeShort((short)this.itemStack[i].getItemDamage());
            }
        }
    }

    @Override
    public void processPacket(NetHandler handler) {
        handler.handleInventoryMenuContent(this);
    }

    @Override
    public int getPacketSize() {
        return 3 + this.itemStack.length * 5;
    }
}
