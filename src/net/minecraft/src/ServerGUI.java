package net.minecraft.src;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.minecraft.server.MinecraftServer;

public class ServerGUI
extends JComponent
implements ICommandListener {
    public static Logger logger = Logger.getLogger("Minecraft");
    private MinecraftServer mcServer;

    public static void initGui(final MinecraftServer server) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception exception) {
            // empty catch block
        }
        ServerGUI serverGUI = new ServerGUI(server);
        JFrame jFrame = new JFrame("Minecraft server");
        jFrame.add(serverGUI);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e) {
                server.initiateShutdown();
                while (!server.serverStopped) {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
    }

    public ServerGUI(MinecraftServer server) {
        this.mcServer = server;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());
        try {
            this.add((Component)this.getLogComponent(), "Center");
            this.add((Component)this.getStatsComponent(), "West");
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private JComponent getStatsComponent() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add((Component)new GuiStatsComponent(), "North");
        jPanel.add((Component)this.getPlayerListComponent(), "Center");
        jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jPanel;
    }

    private JComponent getPlayerListComponent() {
        PlayerListBox playerListBox = new PlayerListBox(this.mcServer);
        JScrollPane jScrollPane = new JScrollPane(playerListBox, 22, 30);
        jScrollPane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jScrollPane;
    }

    private JComponent getLogComponent() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JTextArea jTextArea = new JTextArea();
        logger.addHandler(new GuiLogOutputHandler(jTextArea));
        JScrollPane jScrollPane = new JScrollPane(jTextArea, 22, 30);
        jTextArea.setEditable(false);
        final JTextField jTextField = new JTextField();
        jTextField.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String string = jTextField.getText().trim();
                if (string.length() > 0) {
                    ServerGUI.this.mcServer.addCommand(string, ServerGUI.this);
                }
                jTextField.setText("");
            }
        });
        jTextArea.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent e) {
            }
        });
        jPanel.add((Component)jScrollPane, "Center");
        jPanel.add((Component)jTextField, "South");
        jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return jPanel;
    }

    public void log(String message) {
        logger.info(message);
    }

    public String getUsername() {
        return "CONSOLE";
    }
}

