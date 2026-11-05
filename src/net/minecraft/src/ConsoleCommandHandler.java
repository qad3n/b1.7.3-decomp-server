package net.minecraft.src;

import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class ConsoleCommandHandler {
    private static Logger minecraftLogger = Logger.getLogger("Minecraft");
    private MinecraftServer minecraftServer;

    public ConsoleCommandHandler(MinecraftServer server) {
        this.minecraftServer = server;
    }

    public void handleCommand(ServerCommand command) {
        block72: {
            String string = command.command;
            ICommandListener iCommandListener = command.commandListener;
            String string2 = iCommandListener.getUsername();
            ServerConfigurationManager serverConfigurationManager = this.minecraftServer.configManager;
            if (string.toLowerCase().startsWith("help") || string.toLowerCase().startsWith("?")) {
                this.printHelp(iCommandListener);
            } else if (string.toLowerCase().startsWith("list")) {
                iCommandListener.log("Connected players: " + serverConfigurationManager.getPlayerList());
            } else if (string.toLowerCase().startsWith("stop")) {
                this.sendNoticeToOps(string2, "Stopping the server..");
                this.minecraftServer.initiateShutdown();
            } else if (string.toLowerCase().startsWith("save-all")) {
                this.sendNoticeToOps(string2, "Forcing save..");
                if (serverConfigurationManager != null) {
                    serverConfigurationManager.savePlayerStates();
                }
                for (int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
                    WorldServer worldServer = this.minecraftServer.worldMngr[i];
                    worldServer.saveWorld(true, null);
                }
                this.sendNoticeToOps(string2, "Save complete.");
            } else if (string.toLowerCase().startsWith("save-off")) {
                this.sendNoticeToOps(string2, "Disabling level saving..");
                for (int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
                    WorldServer worldServer = this.minecraftServer.worldMngr[i];
                    worldServer.levelSaving = true;
                }
            } else if (string.toLowerCase().startsWith("save-on")) {
                this.sendNoticeToOps(string2, "Enabling level saving..");
                for (int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
                    WorldServer worldServer = this.minecraftServer.worldMngr[i];
                    worldServer.levelSaving = false;
                }
            } else if (string.toLowerCase().startsWith("op ")) {
                String string3 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.opPlayer(string3);
                this.sendNoticeToOps(string2, "Opping " + string3);
                serverConfigurationManager.sendChatMessageToPlayer(string3, "\u00a7eYou are now op!");
            } else if (string.toLowerCase().startsWith("deop ")) {
                String string4 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.deopPlayer(string4);
                serverConfigurationManager.sendChatMessageToPlayer(string4, "\u00a7eYou are no longer op!");
                this.sendNoticeToOps(string2, "De-opping " + string4);
            } else if (string.toLowerCase().startsWith("ban-ip ")) {
                String string5 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.banIP(string5);
                this.sendNoticeToOps(string2, "Banning ip " + string5);
            } else if (string.toLowerCase().startsWith("pardon-ip ")) {
                String string6 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.pardonIP(string6);
                this.sendNoticeToOps(string2, "Pardoning ip " + string6);
            } else if (string.toLowerCase().startsWith("ban ")) {
                String string7 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.banPlayer(string7);
                this.sendNoticeToOps(string2, "Banning " + string7);
                EntityPlayerMP entityPlayerMP = serverConfigurationManager.getPlayerEntity(string7);
                if (entityPlayerMP != null) {
                    entityPlayerMP.playerNetServerHandler.kickPlayer("Banned by admin");
                }
            } else if (string.toLowerCase().startsWith("pardon ")) {
                String string8 = string.substring(string.indexOf(" ")).trim();
                serverConfigurationManager.pardonPlayer(string8);
                this.sendNoticeToOps(string2, "Pardoning " + string8);
            } else if (string.toLowerCase().startsWith("kick ")) {
                String string9 = string.substring(string.indexOf(" ")).trim();
                EntityPlayerMP entityPlayerMP = null;
                for (int i = 0; i < serverConfigurationManager.playerEntities.size(); ++i) {
                    EntityPlayerMP entityPlayerMP2 = serverConfigurationManager.playerEntities.get(i);
                    if (!entityPlayerMP2.username.equalsIgnoreCase(string9)) continue;
                    entityPlayerMP = entityPlayerMP2;
                }
                if (entityPlayerMP != null) {
                    entityPlayerMP.playerNetServerHandler.kickPlayer("Kicked by admin");
                    this.sendNoticeToOps(string2, "Kicking " + entityPlayerMP.username);
                } else {
                    iCommandListener.log("Can't find user " + string9 + ". No kick.");
                }
            } else if (string.toLowerCase().startsWith("tp ")) {
                String[] stringArray = string.split(" ");
                if (stringArray.length == 3) {
                    EntityPlayerMP entityPlayerMP = serverConfigurationManager.getPlayerEntity(stringArray[1]);
                    EntityPlayerMP entityPlayerMP3 = serverConfigurationManager.getPlayerEntity(stringArray[2]);
                    if (entityPlayerMP == null) {
                        iCommandListener.log("Can't find user " + stringArray[1] + ". No tp.");
                    } else if (entityPlayerMP3 == null) {
                        iCommandListener.log("Can't find user " + stringArray[2] + ". No tp.");
                    } else if (entityPlayerMP.dimension != entityPlayerMP3.dimension) {
                        iCommandListener.log("User " + stringArray[1] + " and " + stringArray[2] + " are in different dimensions. No tp.");
                    } else {
                        entityPlayerMP.playerNetServerHandler.teleportTo(entityPlayerMP3.posX, entityPlayerMP3.posY, entityPlayerMP3.posZ, entityPlayerMP3.rotationYaw, entityPlayerMP3.rotationPitch);
                        this.sendNoticeToOps(string2, "Teleporting " + stringArray[1] + " to " + stringArray[2] + ".");
                    }
                } else {
                    iCommandListener.log("Syntax error, please provice a source and a target.");
                }
            } else if (string.toLowerCase().startsWith("give ")) {
                String[] stringArray = string.split(" ");
                if (stringArray.length != 3 && stringArray.length != 4) {
                    return;
                }
                String string10 = stringArray[1];
                EntityPlayerMP entityPlayerMP = serverConfigurationManager.getPlayerEntity(string10);
                if (entityPlayerMP != null) {
                    try {
                        int n = Integer.parseInt(stringArray[2]);
                        if (Item.itemsList[n] != null) {
                            this.sendNoticeToOps(string2, "Giving " + entityPlayerMP.username + " some " + n);
                            int n2 = 1;
                            if (stringArray.length > 3) {
                                n2 = this.tryParse(stringArray[3], 1);
                            }
                            if (n2 < 1) {
                                n2 = 1;
                            }
                            if (n2 > 64) {
                                n2 = 64;
                            }
                            entityPlayerMP.dropPlayerItem(new ItemStack(n, n2, 0));
                            break block72;
                        }
                        iCommandListener.log("There's no item with id " + n);
                    }
                    catch (NumberFormatException numberFormatException) {
                        iCommandListener.log("There's no item with id " + stringArray[2]);
                    }
                } else {
                    iCommandListener.log("Can't find user " + string10);
                }
            } else if (string.toLowerCase().startsWith("time ")) {
                String[] stringArray = string.split(" ");
                if (stringArray.length != 3) {
                    return;
                }
                String string11 = stringArray[1];
                try {
                    int n = Integer.parseInt(stringArray[2]);
                    if ("add".equalsIgnoreCase(string11)) {
                        for (int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
                            WorldServer worldServer = this.minecraftServer.worldMngr[i];
                            worldServer.changeTime(worldServer.getWorldTime() + (long)n);
                        }
                        this.sendNoticeToOps(string2, "Added " + n + " to time");
                        break block72;
                    }
                    if ("set".equalsIgnoreCase(string11)) {
                        for (int i = 0; i < this.minecraftServer.worldMngr.length; ++i) {
                            WorldServer worldServer = this.minecraftServer.worldMngr[i];
                            worldServer.changeTime(n);
                        }
                        this.sendNoticeToOps(string2, "Set time to " + n);
                        break block72;
                    }
                    iCommandListener.log("Unknown method, use either \"add\" or \"set\"");
                }
                catch (NumberFormatException numberFormatException) {
                    iCommandListener.log("Unable to convert time value, " + stringArray[2]);
                }
            } else if (string.toLowerCase().startsWith("say ")) {
                string = string.substring(string.indexOf(" ")).trim();
                minecraftLogger.info("[" + string2 + "] " + string);
                serverConfigurationManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + string));
            } else if (string.toLowerCase().startsWith("tell ")) {
                String[] stringArray = string.split(" ");
                if (stringArray.length >= 3) {
                    string = string.substring(string.indexOf(" ")).trim();
                    string = string.substring(string.indexOf(" ")).trim();
                    minecraftLogger.info("[" + string2 + "->" + stringArray[1] + "] " + string);
                    string = "\u00a77" + string2 + " whispers " + string;
                    minecraftLogger.info(string);
                    if (!serverConfigurationManager.sendPacketToPlayer(stringArray[1], new Packet3Chat(string))) {
                        iCommandListener.log("There's no player by that name online.");
                    }
                }
            } else if (string.toLowerCase().startsWith("whitelist ")) {
                this.handleWhitelist(string2, string, iCommandListener);
            } else {
                minecraftLogger.info("Unknown console command. Type \"help\" for help.");
            }
        }
    }

    private void handleWhitelist(String player, String command, ICommandListener source) {
        String[] stringArray = command.split(" ");
        if (stringArray.length < 2) {
            return;
        }
        String string = stringArray[1].toLowerCase();
        if ("on".equals(string)) {
            this.sendNoticeToOps(player, "Turned on white-listing");
            this.minecraftServer.propertyManagerObj.setProperty("white-list", true);
        } else if ("off".equals(string)) {
            this.sendNoticeToOps(player, "Turned off white-listing");
            this.minecraftServer.propertyManagerObj.setProperty("white-list", false);
        } else if ("list".equals(string)) {
            Set<String> set = this.minecraftServer.configManager.getWhiteListedIPs();
            String string2 = "";
            for (String string3 : set) {
                string2 = string2 + string3 + " ";
            }
            source.log("White-listed players: " + string2);
        } else if ("add".equals(string) && stringArray.length == 3) {
            String string4 = stringArray[2].toLowerCase();
            this.minecraftServer.configManager.addToWhiteList(string4);
            this.sendNoticeToOps(player, "Added " + string4 + " to white-list");
        } else if ("remove".equals(string) && stringArray.length == 3) {
            String string5 = stringArray[2].toLowerCase();
            this.minecraftServer.configManager.removeFromWhiteList(string5);
            this.sendNoticeToOps(player, "Removed " + string5 + " from white-list");
        } else if ("reload".equals(string)) {
            this.minecraftServer.configManager.reloadWhiteList();
            this.sendNoticeToOps(player, "Reloaded white-list from file");
        }
    }

    private void printHelp(ICommandListener source) {
        source.log("To run the server without a gui, start it like this:");
        source.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
        source.log("Console commands:");
        source.log("   help  or  ?               shows this message");
        source.log("   kick <player>             removes a player from the server");
        source.log("   ban <player>              bans a player from the server");
        source.log("   pardon <player>           pardons a banned player so that they can connect again");
        source.log("   ban-ip <ip>               bans an IP address from the server");
        source.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
        source.log("   op <player>               turns a player into an op");
        source.log("   deop <player>             removes op status from a player");
        source.log("   tp <player1> <player2>    moves one player to the same location as another player");
        source.log("   give <player> <id> [num]  gives a player a resource");
        source.log("   tell <player> <message>   sends a private message to a player");
        source.log("   stop                      gracefully stops the server");
        source.log("   save-all                  forces a server-wide level save");
        source.log("   save-off                  disables terrain saving (useful for backup scripts)");
        source.log("   save-on                   re-enables terrain saving");
        source.log("   list                      lists all currently connected players");
        source.log("   say <message>             broadcasts a message to all players");
        source.log("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
    }

    private void sendNoticeToOps(String player, String message) {
        String string = player + ": " + message;
        this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + string + ")");
        minecraftLogger.info(string);
    }

    private int tryParse(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }
}

