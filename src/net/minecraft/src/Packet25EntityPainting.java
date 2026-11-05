package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet25EntityPainting
extends Packet {
    public int entityId;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int direction;
    public String title;

    public Packet25EntityPainting() {
    }

    public Packet25EntityPainting(EntityPainting painting) {
        this.entityId = painting.entityId;
        this.xPosition = painting.xPosition;
        this.yPosition = painting.yPosition;
        this.zPosition = painting.zPosition;
        this.direction = painting.direction;
        this.title = painting.art.title;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.title = Packet25EntityPainting.readString(input, EntityPainting.EnumArt.SKULL_AND_ROSES_LENGTH);
        this.xPosition = input.readInt();
        this.yPosition = input.readInt();
        this.zPosition = input.readInt();
        this.direction = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        Packet25EntityPainting.writeString(this.title, output);
        output.writeInt(this.xPosition);
        output.writeInt(this.yPosition);
        output.writeInt(this.zPosition);
        output.writeInt(this.direction);
    }

    public void processPacket(NetHandler handler) {
        handler.handleAddPainting(this);
    }

    public int getPacketSize() {
        return 24;
    }
}

