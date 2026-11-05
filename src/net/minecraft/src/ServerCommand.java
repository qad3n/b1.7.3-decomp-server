package net.minecraft.src;


public class ServerCommand {
    public final String command;
    public final ICommandListener commandListener;

    public ServerCommand(String command, ICommandListener source) {
        this.command = command;
        this.commandListener = source;
    }
}

