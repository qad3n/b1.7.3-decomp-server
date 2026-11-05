package net.minecraft.src;

import java.io.IOException;
import java.util.Properties;

public class StringTranslate {
    private static StringTranslate instance = new StringTranslate();
    private Properties translateTable = new Properties();

    private StringTranslate() {
        try {
            this.translateTable.load(StringTranslate.class.getResourceAsStream("/lang/en_US.lang"));
            this.translateTable.load(StringTranslate.class.getResourceAsStream("/lang/stats_US.lang"));
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public static StringTranslate getInstance() {
        return instance;
    }

    public String translateKey(String key) {
        return this.translateTable.getProperty(key, key);
    }

    public String translateKeyFormat(String key, Object ... args) {
        String string = this.translateTable.getProperty(key, key);
        return String.format(string, args);
    }
}

