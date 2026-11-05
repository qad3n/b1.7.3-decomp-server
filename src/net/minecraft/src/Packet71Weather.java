package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet71Weather
extends Packet {
    public int id;
    public int x;
    public int y;
    public int z;
    public int type;

    public Packet71Weather() {
    }

    public Packet71Weather(Entity entity) {
        this.id = entity.entityId;
        this.x = MathHelper.floor_double(entity.posX * 32.0);
        this.y = MathHelper.floor_double(entity.posY * 32.0);
        this.z = MathHelper.floor_double(entity.posZ * 32.0);
        if (entity instanceof EntityLightningBolt) {
            this.type = 1;
        }
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.id = input.readInt();
        this.type = input.readByte();
        this.x = input.readInt();
        this.y = input.readInt();
        this.z = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.id);
        output.writeByte(this.type);
        output.writeInt(this.x);
        output.writeInt(this.y);
        output.writeInt(this.z);
    }

    public void processPacket(NetHandler handler) {
        handler.handleAddGlobalEntity(this);
    }

    public int getPacketSize() {
        return 17;
    }
}

