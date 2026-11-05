package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet40EntityMetadata
extends Packet {
    public int entityId;
    private List<DataWatcher.WatchableObject> dataEntries;

    public Packet40EntityMetadata() {
    }

    public Packet40EntityMetadata(int id, DataWatcher syncedData) {
        this.entityId = id;
        this.dataEntries = syncedData.getChangedObjects();
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.entityId = input.readInt();
        this.dataEntries = DataWatcher.readWatchableObjects(input);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.entityId);
        DataWatcher.writeObjectsInListToStream(this.dataEntries, output);
    }

    public void processPacket(NetHandler handler) {
        handler.handleEntityData(this);
    }

    public int getPacketSize() {
        return 5;
    }
}

