package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet3Chat
extends Packet {
    public String message;

    public Packet3Chat() {
    }

    public Packet3Chat(String message) {
        if (message.length() > 119) {
            message = message.substring(0, 119);
        }
        this.message = message;
    }

    public void readPacketData(DataInputStream input) throws IOException {
        this.message = Packet3Chat.readString(input, 119);
    }

    public void writePacketData(DataOutputStream output) throws IOException {
        Packet3Chat.writeString(this.message, output);
    }

    public void processPacket(NetHandler handler) {
        handler.handleChat(this);
    }

    public int getPacketSize() {
        return this.message.length();
    }
}

