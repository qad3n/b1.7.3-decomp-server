package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet27Position
extends Packet {
    private float strafeMovement;
    private float fowardMovement;
    private boolean jumping;
    private boolean isInJump;
    private float pitchRotation;
    private float yawRotation;

    public void readPacketData(DataInputStream input) throws IOException {
        this.strafeMovement = input.readFloat();
        this.fowardMovement = input.readFloat();
        this.pitchRotation = input.readFloat();
        this.yawRotation = input.readFloat();
        this.jumping = input.readBoolean();
        this.isInJump = input.readBoolean();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeFloat(this.strafeMovement);
        output.writeFloat(this.fowardMovement);
        output.writeFloat(this.pitchRotation);
        output.writeFloat(this.yawRotation);
        output.writeBoolean(this.jumping);
        output.writeBoolean(this.isInJump);
    }

    public void processPacket(NetHandler handler) {
        handler.handleMovementTypePacket(this);
    }

    public int getPacketSize() {
        return 18;
    }

    public float getSidewaysSpeed() {
        return this.strafeMovement;
    }

    public float getPitch() {
        return this.pitchRotation;
    }

    public float getForwardSpeed() {
        return this.fowardMovement;
    }

    public float getYaw() {
        return this.yawRotation;
    }

    public boolean getJumping() {
        return this.jumping;
    }

    public boolean getSneaking() {
        return this.isInJump;
    }
}

