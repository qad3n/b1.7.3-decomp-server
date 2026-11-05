package net.minecraft.src;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.OutputStream;

public class NetworkManager {
    public static final Object threadSyncObject = new Object();
    public static int numReadThreads;
    public static int numWriteThreads;
    private Object sendQueueLock = new Object();
    private Socket networkSocket;
    private final SocketAddress remoteSocketAddress;
    private DataInputStream socketInputStream;
    private DataOutputStream socketOutputStream;
    private boolean isRunning = true;
    private List<Packet> readPackets = Collections.synchronizedList(new ArrayList<Packet>());
    private List<Packet> dataPackets = Collections.synchronizedList(new ArrayList<Packet>());
    private List<Packet> chunkDataPackets = Collections.synchronizedList(new ArrayList<Packet>());
    private NetHandler netHandler;
    private boolean isServerTerminating = false;
    private Thread writeThread;
    private Thread readThread;
    private boolean isTerminating = false;
    private String terminationReason = "";
    private Object[] disconnectReasonArgs;
    private int timeSinceLastRead = 0;
    private int sendQueueByteLength = 0;
    public static int[] TOTAL_READ_SIZE = new int[256];
    public static int[] TOTAL_SEND_SIZE = new int[256];
    public int chunkDataSendCounter = 0;
    private int delay = 50;

    public NetworkManager(Socket socket, String name, NetHandler listener) throws IOException {
        this.networkSocket = socket;
        this.remoteSocketAddress = socket.getRemoteSocketAddress();
        this.netHandler = listener;

        try {
            socket.setSoTimeout(30000);
            socket.setTrafficClass(24);
        } catch (SocketException socketexception) {
            System.err.println(socketexception.getMessage());
        }

        this.socketInputStream = new DataInputStream(socket.getInputStream());
        this.socketOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 5120));
        this.readThread = new Thread(name + " read thread") {
            @Override
            public void run() {
                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numReadThreads++;
                }

                try {
                    while (NetworkManager.this.isRunning && !NetworkManager.this.isServerTerminating) {
                        while (NetworkManager.this.readPacket()) {
                        }

                        try {
                            sleep(100L);
                        } catch (InterruptedException interruptedexception) {
                        }
                    }
                } finally {
                    synchronized (NetworkManager.threadSyncObject) {
                        NetworkManager.numReadThreads--;
                    }
                }
            }
        };
        this.writeThread = new Thread(name + " write thread") {
            @Override
            public void run() {
                synchronized (NetworkManager.threadSyncObject) {
                    NetworkManager.numWriteThreads++;
                }

                try {
                    while (NetworkManager.this.isRunning) {
                        while (NetworkManager.this.sendPacket()) {
                        }

                        try {
                            sleep(100L);
                        } catch (InterruptedException interruptedexception) {
                        }

                        try {
                            if (NetworkManager.this.socketOutputStream != null) {
                                NetworkManager.this.socketOutputStream.flush();
                            }
                        } catch (IOException ioexception) {
                            if (!NetworkManager.this.isTerminating) {
                                NetworkManager.this.onNetworkError(ioexception);
                            }

                            ioexception.printStackTrace();
                        }
                    }
                } finally {
                    synchronized (NetworkManager.threadSyncObject) {
                        NetworkManager.numWriteThreads--;
                    }
                }
            }
        };
        this.readThread.start();
        this.writeThread.start();
    }

    public void setNetHandler(NetHandler listener) {
        this.netHandler = listener;
    }

    public void addToSendQueue(final Packet packet) {
        if (this.isServerTerminating) {
            return;
        }
        synchronized (this.sendQueueLock) {
            this.sendQueueByteLength += packet.getPacketSize() + 1;
            if (packet.isChunkDataPacket) {
                this.chunkDataPackets.add(packet);
            } else {
                this.dataPackets.add(packet);
            }
        }
    }

    private boolean sendPacket() {
        boolean flag = false;

        try {
            if (!this.dataPackets.isEmpty()
                && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - this.dataPackets.get(0).creationTimeMillis >= this.chunkDataSendCounter)) {
                Packet packet;
                synchronized (this.sendQueueLock) {
                    packet = this.dataPackets.remove(0);
                    this.sendQueueByteLength = this.sendQueueByteLength - (packet.getPacketSize() + 1);
                }

                Packet.writePacket(packet, this.socketOutputStream);
                TOTAL_SEND_SIZE[packet.getPacketId()] += packet.getPacketSize() + 1;
                flag = true;
            }

            if (this.delay-- <= 0
                && !this.chunkDataPackets.isEmpty()
                && (this.chunkDataSendCounter == 0 || System.currentTimeMillis() - this.chunkDataPackets.get(0).creationTimeMillis >= this.chunkDataSendCounter)
                )
             {
                Packet packet1;
                synchronized (this.sendQueueLock) {
                    packet1 = this.chunkDataPackets.remove(0);
                    this.sendQueueByteLength = this.sendQueueByteLength - (packet1.getPacketSize() + 1);
                }

                Packet.writePacket(packet1, this.socketOutputStream);
                TOTAL_SEND_SIZE[packet1.getPacketId()] += packet1.getPacketSize() + 1;
                this.delay = 0;
                flag = true;
            }

            return flag;
        } catch (Exception exception) {
            if (!this.isTerminating) {
                this.onNetworkError(exception);
            }

            return false;
        }
    }

    public void interrupt() {
        this.readThread.interrupt();
        this.writeThread.interrupt();
    }

    private boolean readPacket() {
        boolean flag = false;

        try {
            Packet packet = Packet.readPacket(this.socketInputStream, this.netHandler.isServerHandler());
            if (packet != null) {
                TOTAL_READ_SIZE[packet.getPacketId()] += packet.getPacketSize() + 1;
                this.readPackets.add(packet);
                flag = true;
            } else {
                this.networkShutdown("disconnect.endOfStream");
            }

            return flag;
        } catch (Exception exception) {
            if (!this.isTerminating) {
                this.onNetworkError(exception);
            }

            return false;
        }
    }

    private void onNetworkError(Exception exception) {
        exception.printStackTrace();
        this.networkShutdown("disconnect.genericReason", "Internal exception: " + exception.toString());
    }

    public void networkShutdown(String reason, Object... args) {
        if (!this.isRunning) {
            return;
        }
        this.isTerminating = true;
        this.terminationReason = reason;
        this.disconnectReasonArgs = args;
        new Thread() {
            
            public void run() {
                try {
                    Thread.sleep(5000L);
                    if (NetworkManager.this.readThread.isAlive()) {
                        try {
                            NetworkManager.this.readThread.stop();
                        } catch (Throwable throwable) {
                        }
                    }
                    if (NetworkManager.this.writeThread.isAlive()) {
                        try {
                            NetworkManager.this.writeThread.stop();
                        } catch (Throwable throwable) {
                        }
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }.start();
        this.isRunning = false;
        try {
            this.socketInputStream.close();
            this.socketInputStream = null;
        } catch (Throwable throwable) {
        }
        try {
            this.socketOutputStream.close();
            this.socketOutputStream = null;
        } catch (Throwable throwable) {
        }
        try {
            this.networkSocket.close();
            this.networkSocket = null;
        } catch (Throwable throwable) {
        }
    }

    public void processReadPackets() {
        if (this.sendQueueByteLength > 1048576) {
            this.networkShutdown("disconnect.overflow");
        }

        if (this.readPackets.isEmpty()) {
            if (this.timeSinceLastRead++ == 1200) {
                this.networkShutdown("disconnect.timeout");
            }
        } else {
            this.timeSinceLastRead = 0;
        }

        int i = 100;

        while (!this.readPackets.isEmpty() && i-- >= 0) {
            Packet packet = this.readPackets.remove(0);
            packet.processPacket(this.netHandler);
        }

        this.interrupt();
        if (this.isTerminating && this.readPackets.isEmpty()) {
            this.netHandler.handleErrorMessage(this.terminationReason, this.disconnectReasonArgs);
        }
    }

    public SocketAddress getRemoteAddress() {
        return this.remoteSocketAddress;
    }

    public void serverShutdown() {
        this.interrupt();
        this.isServerTerminating = true;
        this.readThread.interrupt();
        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000L);
                    if (NetworkManager.this.isRunning) {
                        NetworkManager.this.writeThread.interrupt();
                        NetworkManager.this.networkShutdown("disconnect.closed");
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    public int getNumChunkDataPackets() {
        return this.chunkDataPackets.size();
    }
}
