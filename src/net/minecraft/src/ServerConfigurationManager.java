package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class ServerConfigurationManager {
    public static Logger logger = Logger.getLogger("Minecraft");
    public List<EntityPlayerMP> playerEntities = new ArrayList<>();
    private MinecraftServer mcServer;
    private PlayerManager[] playerManagerObj;
    private int maxPlayers;
    private Set<String> bannedPlayers = new HashSet<>();
    private Set<String> bannedIPs = new HashSet<>();
    private Set<String> ops = new HashSet<>();
    private Set<String> whiteListedIPs = new HashSet<>();
    private File bannedPlayersFile;
    private File ipBanFile;
    private File opFile;
    private File whitelistPlayersFile;
    private IPlayerFileData playerNBTManagerObj;
    private boolean whiteListEnforced;

    public ServerConfigurationManager(MinecraftServer server) {
        this.playerManagerObj = new PlayerManager[2];
        this.mcServer = server;
        this.bannedPlayersFile = server.getFile("banned-players.txt");
        this.ipBanFile = server.getFile("banned-ips.txt");
        this.opFile = server.getFile("ops.txt");
        this.whitelistPlayersFile = server.getFile("white-list.txt");
        int i = server.propertyManagerObj.getIntProperty("view-distance", 10);
        this.playerManagerObj[0] = new PlayerManager(server, 0, i);
        this.playerManagerObj[1] = new PlayerManager(server, -1, i);
        this.maxPlayers = server.propertyManagerObj.getIntProperty("max-players", 20);
        this.whiteListEnforced = server.propertyManagerObj.getBooleanProperty("white-list", false);
        this.readBannedPlayers();
        this.loadBannedList();
        this.loadOps();
        this.loadWhiteList();
        this.writeBannedPlayers();
        this.saveBannedList();
        this.saveOps();
        this.saveWhiteList();
    }

    public void setPlayerManager(WorldServer[] worlds) {
        this.playerNBTManagerObj = worlds[0].getWorldFile().getPlayerDataStorage();
    }

    public void onChangedDimension(EntityPlayerMP player) {
        this.playerManagerObj[0].removePlayer(player);
        this.playerManagerObj[1].removePlayer(player);
        this.getPlayerManager(player.dimension).addPlayer(player);
        WorldServer worldserver = this.mcServer.getWorldManager(player.dimension);
        worldserver.chunkProviderServer.loadChunk((int)player.posX >> 4, (int)player.posZ >> 4);
    }

    public int getMaxTrackingDistance() {
        return this.playerManagerObj[0].getMaxTrackingDistance();
    }

    private PlayerManager getPlayerManager(int dimension) {
        return dimension == -1 ? this.playerManagerObj[1] : this.playerManagerObj[0];
    }

    public void readPlayerDataFromFile(EntityPlayerMP player) {
        this.playerNBTManagerObj.readPlayerData(player);
    }

    public void playerLoggedIn(EntityPlayerMP player) {
        this.playerEntities.add(player);
        WorldServer worldserver = this.mcServer.getWorldManager(player.dimension);
        worldserver.chunkProviderServer.loadChunk((int)player.posX >> 4, (int)player.posZ >> 4);

        while (worldserver.getCollidingBoundingBoxes(player, player.boundingBox).size() != 0) {
            player.setPosition(player.posX, player.posY + 1.0, player.posZ);
        }

        worldserver.entityJoinedWorld(player);
        this.getPlayerManager(player.dimension).addPlayer(player);
    }

    public void move(EntityPlayerMP player) {
        this.getPlayerManager(player.dimension).movePlayer(player);
    }

    public void playerLoggedOut(EntityPlayerMP player) {
        this.playerNBTManagerObj.writePlayerData(player);
        this.mcServer.getWorldManager(player.dimension).removePlayerForLogoff(player);
        this.playerEntities.remove(player);
        this.getPlayerManager(player.dimension).removePlayer(player);
    }

    public EntityPlayerMP login(NetLoginHandler networkHandler, String name) {
        if (this.bannedPlayers.contains(name.trim().toLowerCase())) {
            networkHandler.kickUser("You are banned from this server!");
            return null;
        }

        if (!this.isAllowedToLogin(name)) {
            networkHandler.kickUser("You are not white-listed on this server!");
            return null;
        }

        String s = networkHandler.netManager.getRemoteAddress().toString();
        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        if (this.bannedIPs.contains(s)) {
            networkHandler.kickUser("Your IP address is banned from this server!");
            return null;
        }

        if (this.playerEntities.size() >= this.maxPlayers) {
            networkHandler.kickUser("The server is full!");
            return null;
        }

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            if (entityplayermp.username.equalsIgnoreCase(name)) {
                entityplayermp.playerNetServerHandler.kickPlayer("You logged in from another location");
            }
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(0), name, new ItemInWorldManager(this.mcServer.getWorldManager(0)));
    }

    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP player, int dimension) {
        this.mcServer.getEntityTracker(player.dimension).removeTrackedPlayerSymmetric(player);
        this.mcServer.getEntityTracker(player.dimension).untrackEntity(player);
        this.getPlayerManager(player.dimension).removePlayer(player);
        this.playerEntities.remove(player);
        this.mcServer.getWorldManager(player.dimension).removePlayer(player);
        ChunkCoordinates chunkcoordinates = player.getSpawnChunk();
        player.dimension = dimension;
        EntityPlayerMP entityplayermp = new EntityPlayerMP(
            this.mcServer,
            this.mcServer.getWorldManager(player.dimension),
            player.username,
            new ItemInWorldManager(this.mcServer.getWorldManager(player.dimension))
        );
        entityplayermp.entityId = player.entityId;
        entityplayermp.playerNetServerHandler = player.playerNetServerHandler;
        WorldServer worldserver = this.mcServer.getWorldManager(player.dimension);
        if (chunkcoordinates != null) {
            ChunkCoordinates chunkcoordinates1 = EntityPlayer.loadSpawnPoint(this.mcServer.getWorldManager(player.dimension), chunkcoordinates);
            if (chunkcoordinates1 != null) {
                entityplayermp.setLocationAndAngles(chunkcoordinates1.posX + 0.5F, chunkcoordinates1.posY + 0.1F, chunkcoordinates1.posZ + 0.5F, 0.0F, 0.0F);
                entityplayermp.setSpawnChunk(chunkcoordinates);
            } else {
                entityplayermp.playerNetServerHandler.sendPacket(new Packet70Bed(0));
            }
        }

        worldserver.chunkProviderServer.loadChunk((int)entityplayermp.posX >> 4, (int)entityplayermp.posZ >> 4);

        while (worldserver.getCollidingBoundingBoxes(entityplayermp, entityplayermp.boundingBox).size() != 0) {
            entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0, entityplayermp.posZ);
        }

        entityplayermp.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)entityplayermp.dimension));
        entityplayermp.playerNetServerHandler
            .teleportTo(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
        this.sendWorldInfo(entityplayermp, worldserver);
        this.getPlayerManager(entityplayermp.dimension).addPlayer(entityplayermp);
        worldserver.entityJoinedWorld(entityplayermp);
        this.playerEntities.add(entityplayermp);
        entityplayermp.initMenu();
        entityplayermp.afterRespawn();
        return entityplayermp;
    }

    public void sendPlayerToOtherDimension(EntityPlayerMP player) {
        WorldServer worldserver = this.mcServer.getWorldManager(player.dimension);
        byte b0 = 0;
        if (player.dimension == -1) {
            b0 = 0;
        } else {
            b0 = -1;
        }

        player.dimension = b0;
        WorldServer worldserver1 = this.mcServer.getWorldManager(player.dimension);
        player.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)player.dimension));
        worldserver.removePlayer(player);
        player.isDead = false;
        double d0 = player.posX;
        double d1 = player.posZ;
        double d2 = 8.0;
        if (player.dimension == -1) {
            d0 /= d2;
            d1 /= d2;
            player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
            if (player.isEntityAlive()) {
                worldserver.updateEntityWithOptionalForce(player, false);
            }
        } else {
            d0 *= d2;
            d1 *= d2;
            player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
            if (player.isEntityAlive()) {
                worldserver.updateEntityWithOptionalForce(player, false);
            }
        }

        if (player.isEntityAlive()) {
            worldserver1.entityJoinedWorld(player);
            player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
            worldserver1.updateEntityWithOptionalForce(player, false);
            worldserver1.chunkProviderServer.chunkLoadOverride = true;
            new Teleporter().setExitLocation(worldserver1, player);
            worldserver1.chunkProviderServer.chunkLoadOverride = false;
        }

        this.onChangedDimension(player);
        player.playerNetServerHandler.teleportTo(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        player.setWorldHandler(worldserver1);
        this.sendWorldInfo(player, worldserver1);
        this.sendPlayerInfo(player);
    }

    public void onTick() {
        for (int i = 0; i < this.playerManagerObj.length; i++) {
            this.playerManagerObj[i].updatePlayerInstances();
        }
    }

    public void markBlockNeedsUpdate(int x, int y, int z, int dimension) {
        this.getPlayerManager(dimension).markBlockNeedsUpdate(x, y, z);
    }

    public void sendPacketToAllPlayers(Packet packet) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            entityplayermp.playerNetServerHandler.sendPacket(packet);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet packet, int dimension) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            if (entityplayermp.dimension == dimension) {
                entityplayermp.playerNetServerHandler.sendPacket(packet);
            }
        }
    }

    public String getPlayerList() {
        String s = "";

        for (int i = 0; i < this.playerEntities.size(); i++) {
            if (i > 0) {
                s = s + ", ";
            }

            s = s + this.playerEntities.get(i).username;
        }

        return s;
    }

    public void banPlayer(String name) {
        this.bannedPlayers.add(name.toLowerCase());
        this.writeBannedPlayers();
    }

    public void pardonPlayer(String name) {
        this.bannedPlayers.remove(name.toLowerCase());
        this.writeBannedPlayers();
    }

    private void readBannedPlayers() {
        try {
            this.bannedPlayers.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.bannedPlayersFile));
            String s = "";

            while ((s = bufferedreader.readLine()) != null) {
                this.bannedPlayers.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            logger.warning("Failed to load ban list: " + exception);
        }
    }

    private void writeBannedPlayers() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.bannedPlayersFile, false));

            for (String s : this.bannedPlayers) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            logger.warning("Failed to save ban list: " + exception);
        }
    }

    public void banIP(String ip) {
        this.bannedIPs.add(ip.toLowerCase());
        this.saveBannedList();
    }

    public void pardonIP(String ip) {
        this.bannedIPs.remove(ip.toLowerCase());
        this.saveBannedList();
    }

    private void loadBannedList() {
        try {
            this.bannedIPs.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.ipBanFile));
            String s = "";

            while ((s = bufferedreader.readLine()) != null) {
                this.bannedIPs.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            logger.warning("Failed to load ip ban list: " + exception);
        }
    }

    private void saveBannedList() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.ipBanFile, false));

            for (String s : this.bannedIPs) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            logger.warning("Failed to save ip ban list: " + exception);
        }
    }

    public void opPlayer(String name) {
        this.ops.add(name.toLowerCase());
        this.saveOps();
    }

    public void deopPlayer(String name) {
        this.ops.remove(name.toLowerCase());
        this.saveOps();
    }

    private void loadOps() {
        try {
            this.ops.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.opFile));
            String s = "";

            while ((s = bufferedreader.readLine()) != null) {
                this.ops.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            logger.warning("Failed to load ip ban list: " + exception);
        }
    }

    private void saveOps() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.opFile, false));

            for (String s : this.ops) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            logger.warning("Failed to save ip ban list: " + exception);
        }
    }

    private void loadWhiteList() {
        try {
            this.whiteListedIPs.clear();
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.whitelistPlayersFile));
            String s = "";

            while ((s = bufferedreader.readLine()) != null) {
                this.whiteListedIPs.add(s.trim().toLowerCase());
            }

            bufferedreader.close();
        } catch (Exception exception) {
            logger.warning("Failed to load white-list: " + exception);
        }
    }

    private void saveWhiteList() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.whitelistPlayersFile, false));

            for (String s : this.whiteListedIPs) {
                printwriter.println(s);
            }

            printwriter.close();
        } catch (Exception exception) {
            logger.warning("Failed to save white-list: " + exception);
        }
    }

    public boolean isAllowedToLogin(String name) {
        name = name.trim().toLowerCase();
        return !this.whiteListEnforced || this.ops.contains(name) || this.whiteListedIPs.contains(name);
    }

    public boolean isOp(String name) {
        return this.ops.contains(name.trim().toLowerCase());
    }

    public EntityPlayerMP getPlayerEntity(String name) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            if (entityplayermp.username.equalsIgnoreCase(name)) {
                return entityplayermp;
            }
        }

        return null;
    }

    public void sendChatMessageToPlayer(String name, String message) {
        EntityPlayerMP entityplayermp = this.getPlayerEntity(name);
        if (entityplayermp != null) {
            entityplayermp.playerNetServerHandler.sendPacket(new Packet3Chat(message));
        }
    }

    public void sendPacketToPlayersAroundPoint(double x, double y, double z, double range, int dimension, Packet packet) {
        this.sendPacket(null, x, y, z, range, dimension, packet);
    }

    public void sendPacket(EntityPlayer source, double x, double y, double z, double range, int dimension, Packet packet) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            if (entityplayermp != source && entityplayermp.dimension == dimension) {
                double d0 = x - entityplayermp.posX;
                double d1 = y - entityplayermp.posY;
                double d2 = z - entityplayermp.posZ;
                if (d0 * d0 + d1 * d1 + d2 * d2 < range * range) {
                    entityplayermp.playerNetServerHandler.sendPacket(packet);
                }
            }
        }
    }

    public void sendChatMessageToAllOps(String message) {
        Packet3Chat packet3chat = new Packet3Chat(message);

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityplayermp = this.playerEntities.get(i);
            if (this.isOp(entityplayermp.username)) {
                entityplayermp.playerNetServerHandler.sendPacket(packet3chat);
            }
        }
    }

    public boolean sendPacketToPlayer(String player, Packet packet) {
        EntityPlayerMP entityplayermp = this.getPlayerEntity(player);
        if (entityplayermp != null) {
            entityplayermp.playerNetServerHandler.sendPacket(packet);
            return true;
        } else {
            return false;
        }
    }

    public void savePlayerStates() {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            this.playerNBTManagerObj.writePlayerData(this.playerEntities.get(i));
        }
    }

    public void sentTileEntityToPlayer(int x, int y, int z, TileEntity blockEntity) {
    }

    public void addToWhiteList(String name) {
        this.whiteListedIPs.add(name);
        this.saveWhiteList();
    }

    public void removeFromWhiteList(String name) {
        this.whiteListedIPs.remove(name);
        this.saveWhiteList();
    }

    public Set<String> getWhiteListedIPs() {
        return this.whiteListedIPs;
    }

    public void reloadWhiteList() {
        this.loadWhiteList();
    }

    public void sendWorldInfo(EntityPlayerMP player, WorldServer world) {
        player.playerNetServerHandler.sendPacket(new Packet4UpdateTime(world.getWorldTime()));
        if (world.isRaining()) {
            player.playerNetServerHandler.sendPacket(new Packet70Bed(1));
        }
    }

    public void sendPlayerInfo(EntityPlayerMP player) {
        player.setMenu(player.personalCraftingInventory);
        player.markHealthDirty();
    }
}
