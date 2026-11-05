package net.minecraft.src;


public class StatCrafting
extends StatBase {
    private final int item;

    public StatCrafting(int id, String name, int item) {
        super(id, name);
        this.item = item;
    }
}

