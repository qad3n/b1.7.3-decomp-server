package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Packet {
    private static Map<Integer, Class<? extends Packet>> packetIdToClassMap = new HashMap<Integer, Class<? extends Packet>>();
    private static Map<Class<? extends Packet>, Integer> packetClassToIdMap = new HashMap<Class<? extends Packet>, Integer>();
    private static Set<Integer> clientPacketIdList = new HashSet<Integer>();
    private static Set<Integer> serverPacketIdList = new HashSet<Integer>();
    public final long creationTimeMillis = System.currentTimeMillis();
    public boolean isChunkDataPacket = false;
    private static HashMap<Integer, PacketCounter> packetStats;
    private static int totalPacketsCount;

    static void addIdClassMapping(int id, boolean s2c, boolean c2s, Class<? extends Packet> type) {
        if (packetIdToClassMap.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate packet id:" + id);
        }
        if (packetClassToIdMap.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate packet class:" + type);
        }
        packetIdToClassMap.put(id, type);
        packetClassToIdMap.put(type, id);
        if (s2c) {
            clientPacketIdList.add(id);
        }
        if (c2s) {
            serverPacketIdList.add(id);
        }
    }

    public static Packet getNewPacket(int id) {
        try {
            Class<? extends Packet> clazz = packetIdToClassMap.get(id);
            if (clazz == null) {
                return null;
            }
            return clazz.newInstance();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("Skipping packet with id " + id);
            return null;
        }
    }

    public final int getPacketId() {
        return packetClassToIdMap.get(this.getClass());
    }

    public static Packet readPacket(DataInputStream input, boolean server) throws IOException {
        int i = 0;
        Packet packet = null;
        try {
            i = input.read();
            if (i == -1) {
                return null;
            }
            if (server && !serverPacketIdList.contains(i) || !server && !clientPacketIdList.contains(i)) {
                throw new IOException("Bad packet id " + i);
            }
            packet = getNewPacket(i);
            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            }
            packet.readPacketData(input);
        } catch (EOFException eofexception) {
            System.out.println("Reached end of stream");
            return null;
        }
        Packet.PacketCounter packet$packetcounter = packetStats.get(i);
        if (packet$packetcounter == null) {
            packet$packetcounter = new Packet.PacketCounter();
            packetStats.put(i, packet$packetcounter);
        }
        packet$packetcounter.addPacket(packet.getPacketSize());
        totalPacketsCount++;
        if (totalPacketsCount % 1000 == 0) {
        }
        return packet;
    }

    public static void writePacket(Packet packet, DataOutputStream output) throws IOException {
        output.write(packet.getPacketId());
        packet.writePacketData(output);
    }

    public static void writeString(String s, DataOutputStream output) throws IOException {
        if (s.length() > Short.MAX_VALUE) {
            throw new IOException("String too big");
        }
        output.writeShort(s.length());
        output.writeChars(s);
    }

    public static String readString(DataInputStream input, int maxLength) throws IOException {
        int n = input.readShort();
        if (n > maxLength) {
            throw new IOException("Received string length longer than maximum allowed (" + n + " > " + maxLength + ")");
        }
        if (n < 0) {
            throw new IOException("Received string length is less than zero! Weird string!");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(input.readChar());
        }
        return stringBuilder.toString();
    }

    public abstract void readPacketData(DataInputStream var1) throws IOException;

    public abstract void writePacketData(DataOutputStream var1) throws IOException;

    public abstract void processPacket(NetHandler var1);

    public abstract int getPacketSize();

    static {
        Packet.addIdClassMapping(0, true, true, Packet0KeepAlive.class);
        Packet.addIdClassMapping(1, true, true, Packet1Login.class);
        Packet.addIdClassMapping(2, true, true, Packet2Handshake.class);
        Packet.addIdClassMapping(3, true, true, Packet3Chat.class);
        Packet.addIdClassMapping(4, true, false, Packet4UpdateTime.class);
        Packet.addIdClassMapping(5, true, false, Packet5PlayerInventory.class);
        Packet.addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
        Packet.addIdClassMapping(7, false, true, Packet7UseEntity.class);
        Packet.addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
        Packet.addIdClassMapping(9, true, true, Packet9Respawn.class);
        Packet.addIdClassMapping(10, true, true, Packet10Flying.class);
        Packet.addIdClassMapping(11, true, true, Packet10Flying.Packet11PlayerPosition.class);
        Packet.addIdClassMapping(12, true, true, Packet10Flying.Packet12PlayerLook.class);
        Packet.addIdClassMapping(13, true, true, Packet10Flying.Packet13PlayerLookMove.class);
        Packet.addIdClassMapping(14, false, true, Packet14BlockDig.class);
        Packet.addIdClassMapping(15, false, true, Packet15Place.class);
        Packet.addIdClassMapping(16, false, true, Packet16BlockItemSwitch.class);
        Packet.addIdClassMapping(17, true, false, Packet17Sleep.class);
        Packet.addIdClassMapping(18, true, true, Packet18Animation.class);
        Packet.addIdClassMapping(19, false, true, Packet19EntityAction.class);
        Packet.addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
        Packet.addIdClassMapping(21, true, false, Packet21PickupSpawn.class);
        Packet.addIdClassMapping(22, true, false, Packet22Collect.class);
        Packet.addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
        Packet.addIdClassMapping(24, true, false, Packet24MobSpawn.class);
        Packet.addIdClassMapping(25, true, false, Packet25EntityPainting.class);
        Packet.addIdClassMapping(27, false, true, Packet27Position.class);
        Packet.addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
        Packet.addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
        Packet.addIdClassMapping(30, true, false, Packet30Entity.class);
        Packet.addIdClassMapping(31, true, false, Packet30Entity.Packet31RelEntityMove.class);
        Packet.addIdClassMapping(32, true, false, Packet30Entity.Packet32EntityLook.class);
        Packet.addIdClassMapping(33, true, false, Packet30Entity.Packet33RelEntityMoveLook.class);
        Packet.addIdClassMapping(34, true, false, Packet34EntityTeleport.class);
        Packet.addIdClassMapping(38, true, false, Packet38EntityStatus.class);
        Packet.addIdClassMapping(39, true, false, Packet39AttachEntity.class);
        Packet.addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
        Packet.addIdClassMapping(50, true, false, Packet50PreChunk.class);
        Packet.addIdClassMapping(51, true, false, Packet51MapChunk.class);
        Packet.addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
        Packet.addIdClassMapping(53, true, false, Packet53BlockChange.class);
        Packet.addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
        Packet.addIdClassMapping(60, true, false, Packet60Explosion.class);
        Packet.addIdClassMapping(61, true, false, Packet61DoorChange.class);
        Packet.addIdClassMapping(70, true, false, Packet70Bed.class);
        Packet.addIdClassMapping(71, true, false, Packet71Weather.class);
        Packet.addIdClassMapping(100, true, false, Packet100OpenWindow.class);
        Packet.addIdClassMapping(101, true, true, Packet101CloseWindow.class);
        Packet.addIdClassMapping(102, false, true, Packet102WindowClick.class);
        Packet.addIdClassMapping(103, true, false, Packet103SetSlot.class);
        Packet.addIdClassMapping(104, true, false, Packet104WindowItems.class);
        Packet.addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
        Packet.addIdClassMapping(106, true, true, Packet106Transaction.class);
        Packet.addIdClassMapping(130, true, true, Packet130UpdateSign.class);
        Packet.addIdClassMapping(131, true, false, Packet131MapData.class);
        Packet.addIdClassMapping(200, true, false, Packet200Statistic.class);
        Packet.addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
        packetStats = new HashMap();
        totalPacketsCount = 0;
    }

    public static class PacketCounter {
        private int totalPackets;
        private long totalBytes;

        private PacketCounter() {
        }

        public void addPacket(int size) {
            ++this.totalPackets;
            this.totalBytes += (long)size;
        }
    }
}

