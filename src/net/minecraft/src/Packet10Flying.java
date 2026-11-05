package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet10Flying
extends Packet {
    public double xPosition;
    public double yPosition;
    public double zPosition;
    public double stance;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean moving;
    public boolean rotating;

    public void processPacket(NetHandler handler) {
        handler.handleFlying(this);
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.onGround = input.read() != 0;
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.write(this.onGround ? 1 : 0);
    }

    public int getPacketSize() {
        return 1;
    }

    public static class Packet13PlayerLookMove
    extends Packet10Flying {
        public Packet13PlayerLookMove() {
            this.rotating = true;
            this.moving = true;
        }

        public Packet13PlayerLookMove(double x, double minY, double y, double z, float yaw, float pitch, boolean onGround) {
            this.xPosition = x;
            this.yPosition = minY;
            this.stance = y;
            this.zPosition = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.onGround = onGround;
            this.rotating = true;
            this.moving = true;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            this.xPosition = input.readDouble();
            this.yPosition = input.readDouble();
            this.stance = input.readDouble();
            this.zPosition = input.readDouble();
            this.yaw = input.readFloat();
            this.pitch = input.readFloat();
            super.readPacketData(input);
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            output.writeDouble(this.xPosition);
            output.writeDouble(this.yPosition);
            output.writeDouble(this.stance);
            output.writeDouble(this.zPosition);
            output.writeFloat(this.yaw);
            output.writeFloat(this.pitch);
            super.writePacketData(output);
        }

        public int getPacketSize() {
            return 41;
        }
    }

    public static class Packet12PlayerLook
    extends Packet10Flying {
        public Packet12PlayerLook() {
            this.rotating = true;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            this.yaw = input.readFloat();
            this.pitch = input.readFloat();
            super.readPacketData(input);
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            output.writeFloat(this.yaw);
            output.writeFloat(this.pitch);
            super.writePacketData(output);
        }

        public int getPacketSize() {
            return 9;
        }
    }

    public static class Packet11PlayerPosition
    extends Packet10Flying {
        public Packet11PlayerPosition() {
            this.moving = true;
        }

        public void readPacketData(DataInputStream input) throws IOException {
            this.xPosition = input.readDouble();
            this.yPosition = input.readDouble();
            this.stance = input.readDouble();
            this.zPosition = input.readDouble();
            super.readPacketData(input);
        }

        public void writePacketData(DataOutputStream output) throws IOException {
            output.writeDouble(this.xPosition);
            output.writeDouble(this.yPosition);
            output.writeDouble(this.stance);
            output.writeDouble(this.zPosition);
            super.writePacketData(output);
        }

        public int getPacketSize() {
            return 33;
        }
    }
}

