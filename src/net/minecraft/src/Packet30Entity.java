package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet30Entity
extends Packet {
    public int entityId;
    public byte xPosition;
    public byte yPosition;
    public byte zPosition;
    public byte yaw;
    public byte pitch;
    public boolean rotating = false;

    public Packet30Entity() {
    }

    public Packet30Entity(int id) {
        this.entityId = id;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
    }

    public void processPacket(NetHandler handler) {
        handler.handleEntity(this);
    }

    public int getPacketSize() {
        return 4;
    }

    public static class Packet33RelEntityMoveLook
    extends Packet30Entity {
        public Packet33RelEntityMoveLook() {
            this.rotating = true;
        }

        public Packet33RelEntityMoveLook(int id, byte dx, byte dy, byte dz, byte yaw, byte pitch) {
            super(id);
            this.xPosition = dx;
            this.yPosition = dy;
            this.zPosition = dz;
            this.yaw = yaw;
            this.pitch = pitch;
            this.rotating = true;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            super.readPacketData(input);
            this.xPosition = input.readByte();
            this.yPosition = input.readByte();
            this.zPosition = input.readByte();
            this.yaw = input.readByte();
            this.pitch = input.readByte();
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            super.writePacketData(output);
            output.writeByte(this.xPosition);
            output.writeByte(this.yPosition);
            output.writeByte(this.zPosition);
            output.writeByte(this.yaw);
            output.writeByte(this.pitch);
        }

        public int getPacketSize() {
            return 9;
        }
    }

    public static class Packet32EntityLook
    extends Packet30Entity {
        public Packet32EntityLook() {
            this.rotating = true;
        }

        public Packet32EntityLook(int id, byte yaw, byte pitch) {
            super(id);
            this.yaw = yaw;
            this.pitch = pitch;
            this.rotating = true;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            super.readPacketData(input);
            this.yaw = input.readByte();
            this.pitch = input.readByte();
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            super.writePacketData(output);
            output.writeByte(this.yaw);
            output.writeByte(this.pitch);
        }

        public int getPacketSize() {
            return 6;
        }
    }

    public static class Packet31RelEntityMove
    extends Packet30Entity {
        public Packet31RelEntityMove() {
        }

        public Packet31RelEntityMove(int id, byte dx, byte dy, byte dz) {
            super(id);
            this.xPosition = dx;
            this.yPosition = dy;
            this.zPosition = dz;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            super.readPacketData(input);
            this.xPosition = input.readByte();
            this.yPosition = input.readByte();
            this.zPosition = input.readByte();
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            super.writePacketData(output);
            output.writeByte(this.xPosition);
            output.writeByte(this.yPosition);
            output.writeByte(this.zPosition);
        }

        public int getPacketSize() {
            return 7;
        }
    }
}

