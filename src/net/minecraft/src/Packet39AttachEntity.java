package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet39AttachEntity
extends Packet {
    public int entityId;
    public int vehicleEntityId;

    public Packet39AttachEntity() {
    }

    public Packet39AttachEntity(Entity entity, Entity mount) {
        this.entityId = entity.entityId;
        this.vehicleEntityId = mount != null ? mount.entityId : -1;
    }

    public int getPacketSize() {
        return 8;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.vehicleEntityId = input.readInt();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        output.writeInt(this.vehicleEntityId);
    }

    public void processPacket(NetHandler handler) {
        handler.handleRideEntity(this);
    }
}

