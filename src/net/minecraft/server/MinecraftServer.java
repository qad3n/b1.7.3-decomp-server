package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ConsoleCommandHandler;
import net.minecraft.src.ConsoleLogManager;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PropertyManager;
import net.minecraft.src.SaveConverterMcRegion;
import net.minecraft.src.SaveOldDir;
import net.minecraft.src.ServerCommand;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.ServerGUI;
import net.minecraft.src.StatList;
import net.minecraft.src.Vec3D;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WorldServerMulti;

public class MinecraftServer implements Runnable, ICommandListener {
    public static Logger logger = Logger.getLogger("Minecraft");
    public static HashMap<String, Integer> GIVE_COMMANDS_COOLDOWNS = new HashMap<>();
    public NetworkListenThread networkServer;
    public PropertyManager propertyManagerObj;
    public WorldServer[] worldMngr;
    public ServerConfigurationManager configManager;
    private ConsoleCommandHandler commandHandler;
    private boolean serverRunning = true;
    public boolean serverStopped = false;
    int deathTime = 0;
    public String currentTask;
    public int percentDone;
    private List<IUpdatePlayerListBox> tickables = new ArrayList<>();
    private List<ServerCommand> commands = Collections.synchronizedList(new ArrayList<ServerCommand>());
    public EntityTracker[] entityTracker = new EntityTracker[2];
    public boolean onlineMode;
    public boolean spawnPeacefulMobs;
    public boolean pvpOn;
    public boolean allowFlight;

    public MinecraftServer() {
        new Thread() {
            {
                this.setDaemon(true);
                this.start();
            }

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException interruptedexception) {
                    }
                }
            }
        };
    }

    private boolean startServer() throws IOException, UnknownHostException {
        this.commandHandler = new ConsoleCommandHandler(this);
        Thread thread = new Thread() {
            @Override
            public void run() {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
                String s3 = null;

                try {
                    while (!MinecraftServer.this.serverStopped && MinecraftServer.this.serverRunning && (s3 = bufferedreader.readLine()) != null) {
                        MinecraftServer.this.addCommand(s3, MinecraftServer.this);
                    }
                } catch (IOException ioexception1) {
                    ioexception1.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        ConsoleLogManager.init();
        logger.info("Starting minecraft server version Beta 1.7.3");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            logger.warning("**** NOT ENOUGH RAM!");
            logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        logger.info("Loading properties");
        this.propertyManagerObj = new PropertyManager(new File("server.properties"));
        String s = this.propertyManagerObj.getStringProperty("server-ip", "");
        this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
        this.spawnPeacefulMobs = this.propertyManagerObj.getBooleanProperty("spawn-animals", true);
        this.pvpOn = this.propertyManagerObj.getBooleanProperty("pvp", true);
        this.allowFlight = this.propertyManagerObj.getBooleanProperty("allow-flight", false);
        InetAddress inetaddress = null;
        if (s.length() > 0) {
            inetaddress = InetAddress.getByName(s);
        }

        int i = this.propertyManagerObj.getIntProperty("server-port", 25565);
        logger.info("Starting Minecraft server on " + (s.length() == 0 ? "*" : s) + ":" + i);

        try {
            this.networkServer = new NetworkListenThread(this, inetaddress, i);
        } catch (IOException ioexception) {
            logger.warning("**** FAILED TO BIND TO PORT!");
            logger.log(Level.WARNING, "The exception was: " + ioexception.toString());
            logger.warning("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.onlineMode) {
            logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            logger.warning("The server will make no attempt to authenticate usernames. Beware.");
            logger.warning(
                "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
            );
            logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
        }

        this.configManager = new ServerConfigurationManager(this);
        this.entityTracker[0] = new EntityTracker(this, 0);
        this.entityTracker[1] = new EntityTracker(this, -1);
        long j = System.nanoTime();
        String s1 = this.propertyManagerObj.getStringProperty("level-name", "world");
        String s2 = this.propertyManagerObj.getStringProperty("level-seed", "");
        long k = new Random().nextLong();
        if (s2.length() > 0) {
            try {
                k = Long.parseLong(s2);
            } catch (NumberFormatException numberformatexception) {
                k = s2.hashCode();
            }
        }

        logger.info("Preparing level \"" + s1 + "\"");
        this.initWorld(new SaveConverterMcRegion(new File(".")), s1, k);
        logger.info("Done (" + (System.nanoTime() - j) + "ns)! For help, type \"help\" or \"?\"");
        return true;
    }

    private void initWorld(ISaveFormat storageSource, String saveName, long seed) {
        if (storageSource.isOldSaveType(saveName)) {
            logger.info("Converting map!");
            storageSource.converMapToMCRegion(saveName, new IProgressUpdate() {
                private long lastTimeMillis = System.currentTimeMillis();

                @Override
                public void progressStartNoAbort(String title) {
                }

                @Override
                public void setLoadingProgress(int percentage) {
                    if (System.currentTimeMillis() - this.lastTimeMillis >= 1000L) {
                        this.lastTimeMillis = System.currentTimeMillis();
                        MinecraftServer.logger.info("Converting... " + percentage + "%");
                    }
                }

                @Override
                public void displayLoadingString(String stage) {
                }
            });
        }

        this.worldMngr = new WorldServer[2];
        SaveOldDir saveolddir = new SaveOldDir(new File("."), saveName, true);

        for (int i = 0; i < this.worldMngr.length; i++) {
            if (i == 0) {
                this.worldMngr[i] = new WorldServer(this, saveolddir, saveName, i == 0 ? 0 : -1, seed);
            } else {
                this.worldMngr[i] = new WorldServerMulti(this, saveolddir, saveName, i == 0 ? 0 : -1, seed, this.worldMngr[0]);
            }

            this.worldMngr[i].addWorldAccess(new WorldManager(this, this.worldMngr[i]));
            this.worldMngr[i].difficultySetting = this.propertyManagerObj.getBooleanProperty("spawn-monsters", true) ? 1 : 0;
            this.worldMngr[i].setAllowedSpawnTypes(this.propertyManagerObj.getBooleanProperty("spawn-monsters", true), this.spawnPeacefulMobs);
            this.configManager.setPlayerManager(this.worldMngr);
        }

        short short1 = 196;
        long j = System.currentTimeMillis();

        for (int k = 0; k < this.worldMngr.length; k++) {
            logger.info("Preparing start region for level " + k);
            if (k == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
                WorldServer worldserver = this.worldMngr[k];
                ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();

                for (int l = -short1; l <= short1 && this.serverRunning; l += 16) {
                    for (int i1 = -short1; i1 <= short1 && this.serverRunning; i1 += 16) {
                        long j1 = System.currentTimeMillis();
                        if (j1 < j) {
                            j = j1;
                        }

                        if (j1 > j + 1000L) {
                            int k1 = (short1 * 2 + 1) * (short1 * 2 + 1);
                            int l1 = (l + short1) * (short1 * 2 + 1) + i1 + 1;
                            this.outputPercentRemaining("Preparing spawn area", l1 * 100 / k1);
                            j = j1;
                        }

                        worldserver.chunkProviderServer.loadChunk(chunkcoordinates.posX + l >> 4, chunkcoordinates.posZ + i1 >> 4);

                        while (worldserver.doLightUpdates() && this.serverRunning) {
                        }
                    }
                }
            }
        }

        this.clearCurrentTask();
    }

    private void outputPercentRemaining(String progressType, int progress) {
        this.currentTask = progressType;
        this.percentDone = progress;
        logger.info(progressType + ": " + progress + "%");
    }

    private void clearCurrentTask() {
        this.currentTask = null;
        this.percentDone = 0;
    }

    private void saveServerWorld() {
        logger.info("Saving chunks");

        for (int i = 0; i < this.worldMngr.length; i++) {
            WorldServer worldserver = this.worldMngr[i];
            worldserver.saveWorld(true, null);
            worldserver.forceSave();
        }
    }

    private void stopServer() {
        logger.info("Stopping server");
        if (this.configManager != null) {
            this.configManager.savePlayerStates();
        }

        for (int i = 0; i < this.worldMngr.length; i++) {
            WorldServer worldserver = this.worldMngr[i];
            if (worldserver != null) {
                this.saveServerWorld();
            }
        }
    }

    public void initiateShutdown() {
        this.serverRunning = false;
    }

    @Override
    public void run() {
        try {
            if (this.startServer()) {
                long i = System.currentTimeMillis();
                long j = 0L;

                while (this.serverRunning) {
                    long k = System.currentTimeMillis();
                    long l = k - i;
                    if (l > 2000L) {
                        logger.warning("Can't keep up! Did the system time change, or is the server overloaded?");
                        l = 2000L;
                    }

                    if (l < 0L) {
                        logger.warning("Time ran backwards! Did the system time change?");
                        l = 0L;
                    }

                    j += l;
                    i = k;
                    if (this.worldMngr[0].isAllPlayersFullyAsleep()) {
                        this.doTick();
                        j = 0L;
                    } else {
                        while (j > 50L) {
                            j -= 50L;
                            this.doTick();
                        }
                    }

                    Thread.sleep(1L);
                }
            } else {
                while (this.serverRunning) {
                    this.commandLineParser();

                    try {
                        Thread.sleep(10L);
                    } catch (InterruptedException interruptedexception1) {
                        interruptedexception1.printStackTrace();
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable1.printStackTrace();
            logger.log(Level.SEVERE, "Unexpected exception", throwable1);

            while (this.serverRunning) {
                this.commandLineParser();

                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedexception) {
                    interruptedexception.printStackTrace();
                }
            }
        } finally {
            try {
                this.stopServer();
                this.serverStopped = true;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    private void doTick() {
        ArrayList arraylist = new ArrayList();

        for (String s : GIVE_COMMANDS_COOLDOWNS.keySet()) {
            int i = GIVE_COMMANDS_COOLDOWNS.get(s);
            if (i > 0) {
                GIVE_COMMANDS_COOLDOWNS.put(s, i - 1);
            } else {
                arraylist.add(s);
            }
        }

        for (int j = 0; j < arraylist.size(); j++) {
            GIVE_COMMANDS_COOLDOWNS.remove(arraylist.get(j));
        }

        AxisAlignedBB.clearBoundingBoxPool();
        Vec3D.initialize();
        this.deathTime++;

        for (int k = 0; k < this.worldMngr.length; k++) {
            if (k == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
                WorldServer worldserver = this.worldMngr[k];
                if (this.deathTime % 20 == 0) {
                    this.configManager
                        .sendPacketToAllPlayersInDimension(new Packet4UpdateTime(worldserver.getWorldTime()), worldserver.worldProvider.worldType);
                }

                worldserver.tick();

                while (worldserver.doLightUpdates()) {
                }

                worldserver.updateEntities();
            }
        }

        this.networkServer.handleNetworkListenThread();
        this.configManager.onTick();

        for (int l = 0; l < this.entityTracker.length; l++) {
            this.entityTracker[l].updateTrackedEntities();
        }

        for (int i1 = 0; i1 < this.tickables.size(); i1++) {
            this.tickables.get(i1).update();
        }

        try {
            this.commandLineParser();
        } catch (Exception exception) {
            logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception);
        }
    }

    public void addCommand(String command, ICommandListener source) {
        this.commands.add(new ServerCommand(command, source));
    }

    public void commandLineParser() {
        while (this.commands.size() > 0) {
            ServerCommand servercommand = this.commands.remove(0);
            this.commandHandler.handleCommand(servercommand);
        }
    }

    public void addTickable(IUpdatePlayerListBox tickable) {
        this.tickables.add(tickable);
    }

    public static void main(String[] args) {
        StatList.init();

        try {
            final MinecraftServer minecraftserver = new MinecraftServer();
            if (!GraphicsEnvironment.isHeadless() && (args.length <= 0 || !args[0].equals("nogui"))) {
                ServerGUI.initGui(minecraftserver);
            }

            (new Thread("Server thread") {
                @Override
                public void run() {
                    minecraftserver.run();
                }
            }).start();
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Failed to start the minecraft server", exception);
        }
    }

    public File getFile(String path) {
        return new File(path);
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    @Override
    public String getUsername() {
        return "CONSOLE";
    }

    public WorldServer getWorldManager(int dimension) {
        return dimension == -1 ? this.worldMngr[1] : this.worldMngr[0];
    }

    public EntityTracker getEntityTracker(int dimension) {
        return dimension == -1 ? this.entityTracker[1] : this.entityTracker[0];
    }
}
