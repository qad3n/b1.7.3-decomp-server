package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet1Login
extends Packet {
    public int protocolVersion;
    public String username;
    public long mapSeed;
    public byte dimension;

    public Packet1Login() {
    }

    public Packet1Login(String username, int id, long l, byte dimensionId) {
        this.username = username;
        this.protocolVersion = id;
        this.mapSeed = l;
        this.dimension = dimensionId;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.protocolVersion = input.readInt();
        this.username = Packet1Login.readString(input, 16);
        this.mapSeed = input.readLong();
        this.dimension = input.readByte();
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        output.writeInt(this.protocolVersion);
        Packet1Login.writeString(this.username, output);
        output.writeLong(this.mapSeed);
        output.writeByte(this.dimension);
    }

    public void processPacket(NetHandler handler) {
        handler.handleLogin(this);
    }

    public int getPacketSize() {
        return 4 + this.username.length() + 4 + 5;
    }
}

