package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet28EntityVelocity
extends Packet {
    public int entityId;
    public int motionX;
    public int motionY;
    public int motionZ;

    public Packet28EntityVelocity() {
    }

    public Packet28EntityVelocity(Entity entity) {
        this(entity.entityId, entity.motionX, entity.motionY, entity.motionZ);
    }

    public Packet28EntityVelocity(int id, double velocityX, double velocityY, double velocityZ) {
        this.entityId = id;
        double d = 3.9;
        if (velocityX < -d) {
            velocityX = -d;
        }
        if (velocityY < -d) {
            velocityY = -d;
        }
        if (velocityZ < -d) {
            velocityZ = -d;
        }
        if (velocityX > d) {
            velocityX = d;
        }
        if (velocityY > d) {
            velocityY = d;
        }
        if (velocityZ > d) {
            velocityZ = d;
        }
        this.motionX = (int)(velocityX * 8000.0);
        this.motionY = (int)(velocityY * 8000.0);
        this.motionZ = (int)(velocityZ * 8000.0);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.motionX = input.readShort();
        this.motionY = input.readShort();
        this.motionZ = input.readShort();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeShort(this.motionX);
        output.writeShort(this.motionY);
        output.writeShort(this.motionZ);
    }

    public void processPacket(NetHandler handler) {
        handler.handleEntityVelocity(this);
    }

    public int getPacketSize() {
        return 10;
    }
}

