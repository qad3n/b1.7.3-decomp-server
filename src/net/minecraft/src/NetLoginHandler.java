package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class NetLoginHandler
extends NetHandler {
    public static Logger logger = Logger.getLogger("Minecraft");
    private static Random rand = new Random();
    public NetworkManager netManager;
    public boolean finishedProcessing = false;
    private MinecraftServer mcServer;
    private int loginTimer = 0;
    private String username = null;
    private Packet1Login packet1login = null;
    private String serverId = "";

    public NetLoginHandler(MinecraftServer server, Socket socket, String name) throws IOException {
        this.mcServer = server;
        this.netManager = new NetworkManager(socket, name, this);
        this.netManager.chunkDataSendCounter = 0;
    }

    public void tryLogin() {
        if (this.packet1login != null) {
            this.doLogin(this.packet1login);
            this.packet1login = null;
        }
        if (this.loginTimer++ == 600) {
            this.kickUser("Took too long to log in");
        } else {
            this.netManager.processReadPackets();
        }
    }

    public void kickUser(String reason) {
        try {
            logger.info("Disconnecting " + this.getUserAndIPString() + ": " + reason);
            this.netManager.addToSendQueue(new Packet255KickDisconnect(reason));
            this.netManager.serverShutdown();
            this.finishedProcessing = true;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void handleHandshake(Packet2Handshake packet) {
        if (this.mcServer.onlineMode) {
            this.serverId = Long.toHexString(rand.nextLong());
            this.netManager.addToSendQueue(new Packet2Handshake(this.serverId));
        } else {
            this.netManager.addToSendQueue(new Packet2Handshake("-"));
        }
    }

    public void handleLogin(final Packet1Login packet) {
        this.username = packet.username;
        if (packet.protocolVersion != 14) {
            if (packet.protocolVersion > 14) {
                this.kickUser("Outdated server!");
            } else {
                this.kickUser("Outdated client!");
            }
            return;
        }
        if (!this.mcServer.onlineMode) {
            this.doLogin(packet);
        } else {
            new Thread(){

                public void run() {
                    try {
                        String string = NetLoginHandler.this.serverId;
                        URL uRL = new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(packet.username, "UTF-8") + "&serverId=" + URLEncoder.encode(string, "UTF-8"));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream()));
                        String string2 = bufferedReader.readLine();
                        bufferedReader.close();
                        if (string2.equals("YES")) {
                            NetLoginHandler.this.packet1login = packet;
                        } else {
                            NetLoginHandler.this.kickUser("Failed to verify username!");
                        }
                    }
                    catch (Exception exception) {
                        NetLoginHandler.this.kickUser("Failed to verify username! [internal error " + exception + "]");
                        exception.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void doLogin(Packet1Login packet) {
        EntityPlayerMP entityPlayerMP = this.mcServer.configManager.login(this, packet.username);
        if (entityPlayerMP != null) {
            this.mcServer.configManager.readPlayerDataFromFile(entityPlayerMP);
            entityPlayerMP.setWorldHandler(this.mcServer.getWorldManager(entityPlayerMP.dimension));
            logger.info(this.getUserAndIPString() + " logged in with entity id " + entityPlayerMP.entityId + " at (" + entityPlayerMP.posX + ", " + entityPlayerMP.posY + ", " + entityPlayerMP.posZ + ")");
            WorldServer worldServer = this.mcServer.getWorldManager(entityPlayerMP.dimension);
            ChunkCoordinates chunkCoordinates = worldServer.getSpawnPoint();
            NetServerHandler netServerHandler = new NetServerHandler(this.mcServer, this.netManager, entityPlayerMP);
            netServerHandler.sendPacket(new Packet1Login("", entityPlayerMP.entityId, worldServer.getRandomSeed(), (byte)worldServer.worldProvider.worldType));
            netServerHandler.sendPacket(new Packet6SpawnPosition(chunkCoordinates.posX, chunkCoordinates.posY, chunkCoordinates.posZ));
            this.mcServer.configManager.sendWorldInfo(entityPlayerMP, worldServer);
            this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + entityPlayerMP.username + " joined the game."));
            this.mcServer.configManager.playerLoggedIn(entityPlayerMP);
            netServerHandler.teleportTo(entityPlayerMP.posX, entityPlayerMP.posY, entityPlayerMP.posZ, entityPlayerMP.rotationYaw, entityPlayerMP.rotationPitch);
            this.mcServer.networkServer.addPlayer(netServerHandler);
            netServerHandler.sendPacket(new Packet4UpdateTime(worldServer.getWorldTime()));
            entityPlayerMP.initMenu();
        }
        this.finishedProcessing = true;
    }

    public void handleErrorMessage(String reason, Object[] args) {
        logger.info(this.getUserAndIPString() + " lost connection");
        this.finishedProcessing = true;
    }

    public void registerPacket(Packet packet) {
        this.kickUser("Protocol error");
    }

    public String getUserAndIPString() {
        if (this.username != null) {
            return this.username + " [" + this.netManager.getRemoteAddress().toString() + "]";
        }
        return this.netManager.getRemoteAddress().toString();
    }

    public boolean isServerHandler() {
        return true;
    }
}

