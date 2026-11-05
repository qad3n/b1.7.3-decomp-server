package net.minecraft.src;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StatBase {
    public final int statId;
    public final String statName;
    public boolean local = false;
    public String uuid;
    private final IStatType formatter;
    private static NumberFormat NUMBER_FORMAT = NumberFormat.getIntegerInstance(Locale.US);
    public static IStatType NUMBER_FORMATTER = new IStatType(){};
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("########0.00");
    public static IStatType TIME_FORMATTER = new IStatType(){};
    public static IStatType DISTANCE_FORMATTER = new IStatType(){};

    public StatBase(int id, String key, IStatType formatter) {
        this.statId = id;
        this.statName = key;
        this.formatter = formatter;
    }

    public StatBase(int id, String key) {
        this(id, key, NUMBER_FORMATTER);
    }

    public StatBase setLocal() {
        this.local = true;
        return this;
    }

    public StatBase register() {
        if (StatList.BY_ID.containsKey(this.statId)) {
            throw new RuntimeException("Duplicate stat id: \"" + StatList.BY_ID.get((Object)Integer.valueOf((int)this.statId)).statName + "\" and \"" + this.statName + "\" at id " + this.statId);
        }
        StatList.ALL.add(this);
        StatList.BY_ID.put(this.statId, this);
        this.uuid = AchievementMap.getUuid(this.statId);
        return this;
    }

    public String toString() {
        return this.statName;
    }
}

