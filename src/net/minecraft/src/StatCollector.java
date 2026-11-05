package net.minecraft.src;


public class StatCollector {
    private static StringTranslate localizedName = StringTranslate.getInstance();

    public static String translateToLocal(String key) {
        return localizedName.translateKey(key);
    }

    public static String translateToLocalFormatted(String key, Object ... args) {
        return localizedName.translateKeyFormat(key, args);
    }
}

