package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet14BlockDig
extends Packet {
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int face;
    public int status;

    public void readPacketData(DataInputStream input) throws IOException {
        this.status = input.read();
        this.xPosition = input.readInt();
        this.yPosition = input.read();
        this.zPosition = input.readInt();
        this.face = input.read();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.write(this.status);
        output.writeInt(this.xPosition);
        output.write(this.yPosition);
        output.writeInt(this.zPosition);
        output.write(this.face);
    }

    public void processPacket(NetHandler handler) {
        handler.handleBlockDig(this);
    }

    public int getPacketSize() {
        return 11;
    }
}

