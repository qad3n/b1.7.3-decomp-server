package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet200Statistic
extends Packet {
    public int statId;
    public int value;

    public Packet200Statistic() {
    }

    public Packet200Statistic(int statId, int value) {
        this.statId = statId;
        this.value = value;
    }

    public void processPacket(NetHandler handler) {
        handler.handleStat(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.statId = input.readInt();
        this.value = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.statId);
        output.writeByte(this.value);
    }

    public int getPacketSize() {
        return 6;
    }
}

