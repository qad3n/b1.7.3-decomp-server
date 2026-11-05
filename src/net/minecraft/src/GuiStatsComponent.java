package net.minecraft.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.Timer;

public class GuiStatsComponent extends JComponent {
    private int[] memoryUse = new int[256];
    private int updateCounter = 0;
    private String[] displayStrings = new String[10];

    public GuiStatsComponent() {
        this.setPreferredSize(new Dimension(256, 196));
        this.setMinimumSize(new Dimension(256, 196));
        this.setMaximumSize(new Dimension(256, 196));
        new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiStatsComponent.this.updateStats();
            }
        }).start();
        this.setBackground(Color.BLACK);
    }

    private void updateStats() {
        long i = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        this.displayStrings[0] = "Memory use: "
            + i / 1024L / 1024L
            + " mb ("
            + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory()
            + "% free)";
        this.displayStrings[1] = "Threads: " + NetworkManager.numReadThreads + " + " + NetworkManager.numWriteThreads;
        this.memoryUse[this.updateCounter++ & 0xFF] = (int)(i * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(new Color(16777215));
        graphics.fillRect(0, 0, 256, 192);

        for (int i = 0; i < 256; i++) {
            int j = this.memoryUse[i + this.updateCounter & 0xFF];
            graphics.setColor(new Color(j + 28 << 16));
            graphics.fillRect(i, 100 - j, 1, j);
        }

        graphics.setColor(Color.BLACK);

        for (int k = 0; k < this.displayStrings.length; k++) {
            String s = this.displayStrings[k];
            if (s != null) {
                graphics.drawString(s, 32, 116 + k * 16);
            }
        }
    }
}
