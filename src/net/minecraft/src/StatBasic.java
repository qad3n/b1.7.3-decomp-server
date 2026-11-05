package net.minecraft.src;


public class StatBasic
extends StatBase {
    public StatBasic(int n, String string, IStatType iStatType) {
        super(n, string, iStatType);
    }

    public StatBasic(int n, String string) {
        super(n, string);
    }

    public StatBase register() {
        super.register();
        StatList.GENERAL.add(this);
        return this;
    }
}

