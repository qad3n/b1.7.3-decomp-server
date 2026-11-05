package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class NetworkListenThread {
    public static Logger logger = Logger.getLogger("Minecraft");
    private ServerSocket serverSocket;
    private Thread networkAcceptThread;
    public volatile boolean open = false;
    private int connectionCounter = 0;
    private ArrayList<NetLoginHandler> pendingConnections = new ArrayList<>();
    private ArrayList<NetServerHandler> playerList = new ArrayList<>();
    public MinecraftServer mcServer;

    public NetworkListenThread(final MinecraftServer server, InetAddress address, int port) throws IOException {
        this.mcServer = server;
        this.serverSocket = new ServerSocket(port, 0, address);
        this.serverSocket.setPerformancePreferences(0, 2, 1);
        this.open = true;
        this.networkAcceptThread = new Thread("Listen thread") {
            @Override
            public void run() {
                HashMap hashmap = new HashMap();

                while (NetworkListenThread.this.open) {
                    try {
                        Socket socket = NetworkListenThread.this.serverSocket.accept();
                        if (socket != null) {
                            InetAddress inetaddress = socket.getInetAddress();
                            if (hashmap.containsKey(inetaddress)
                                && !"127.0.0.1".equals(inetaddress.getHostAddress())
                                && System.currentTimeMillis() - (Long)hashmap.get(inetaddress) < 5000L) {
                                hashmap.put(inetaddress, System.currentTimeMillis());
                                socket.close();
                            } else {
                                hashmap.put(inetaddress, System.currentTimeMillis());
                                NetLoginHandler netloginhandler = new NetLoginHandler(
                                    server, socket, "Connection #" + NetworkListenThread.this.connectionCounter++
                                );
                                NetworkListenThread.this.addPendingConnection(netloginhandler);
                            }
                        }
                    } catch (IOException ioexception) {
                        ioexception.printStackTrace();
                    }
                }
            }
        };
        this.networkAcceptThread.start();
    }

    public void addPlayer(NetServerHandler connection) {
        this.playerList.add(connection);
    }

    private void addPendingConnection(NetLoginHandler connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Got null pendingconnection!");
        }

        this.pendingConnections.add(connection);
    }

    public void handleNetworkListenThread() {
        for (int i = 0; i < this.pendingConnections.size(); i++) {
            NetLoginHandler netloginhandler = this.pendingConnections.get(i);

            try {
                netloginhandler.tryLogin();
            } catch (Exception exception1) {
                netloginhandler.kickUser("Internal server error");
                logger.log(Level.WARNING, "Failed to handle packet: " + exception1, exception1);
            }

            if (netloginhandler.finishedProcessing) {
                this.pendingConnections.remove(i--);
            }

            netloginhandler.netManager.interrupt();
        }

        for (int j = 0; j < this.playerList.size(); j++) {
            NetServerHandler netserverhandler = this.playerList.get(j);

            try {
                netserverhandler.handlePackets();
            } catch (Exception exception) {
                logger.log(Level.WARNING, "Failed to handle packet: " + exception, exception);
                netserverhandler.kickPlayer("Internal server error");
            }

            if (netserverhandler.connectionClosed) {
                this.playerList.remove(j--);
            }

            netserverhandler.netManager.interrupt();
        }
    }
}
