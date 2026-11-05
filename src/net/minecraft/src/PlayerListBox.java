package net.minecraft.src;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PlayerListBox
extends JList<String>
implements IUpdatePlayerListBox {
    private MinecraftServer mcServer;
    private int updateCounter = 0;

    public PlayerListBox(MinecraftServer server) {
        this.mcServer = server;
        server.addTickable(this);
    }

    @Override
    public void update() {
        if (this.updateCounter++ % 20 == 0) {
            Vector<String> vector = new Vector<String>();
            for (int i = 0; i < this.mcServer.configManager.playerEntities.size(); ++i) {
                vector.add(this.mcServer.configManager.playerEntities.get((int)i).username);
            }
            this.setListData(vector);
        }
    }
}

