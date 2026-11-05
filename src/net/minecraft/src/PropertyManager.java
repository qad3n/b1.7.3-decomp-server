package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyManager {
    public static Logger logger = Logger.getLogger("Minecraft");
    private Properties serverProperties = new Properties();
    private File serverPropertiesFile;

    public PropertyManager(File file) {
        this.serverPropertiesFile = file;
        if (file.exists()) {
            try {
                this.serverProperties.load(new FileInputStream(file));
            }
            catch (Exception exception) {
                logger.log(Level.WARNING, "Failed to load " + file, exception);
                this.generateNewProperties();
            }
        } else {
            logger.log(Level.WARNING, file + " does not exist");
            this.generateNewProperties();
        }
    }

    public void generateNewProperties() {
        logger.log(Level.INFO, "Generating new properties file");
        this.saveProperties();
    }

    public void saveProperties() {
        try {
            this.serverProperties.store(new FileOutputStream(this.serverPropertiesFile), "Minecraft server properties");
        }
        catch (Exception exception) {
            logger.log(Level.WARNING, "Failed to save " + this.serverPropertiesFile, exception);
            this.generateNewProperties();
        }
    }

    public String getStringProperty(String key, String defaultValue) {
        if (!this.serverProperties.containsKey(key)) {
            this.serverProperties.setProperty(key, defaultValue);
            this.saveProperties();
        }
        return this.serverProperties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(this.getStringProperty(key, "" + defaultValue));
        }
        catch (Exception exception) {
            this.serverProperties.setProperty(key, "" + defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(this.getStringProperty(key, "" + defaultValue));
        }
        catch (Exception exception) {
            this.serverProperties.setProperty(key, "" + defaultValue);
            return defaultValue;
        }
    }

    public void setProperty(String key, boolean value) {
        this.serverProperties.setProperty(key, "" + value);
        this.saveProperties();
    }
}

