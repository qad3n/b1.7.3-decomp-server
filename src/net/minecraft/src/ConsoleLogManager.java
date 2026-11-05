package net.minecraft.src;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConsoleLogManager {
    public static Logger logger = Logger.getLogger("Minecraft");

    public static void init() {
        ConsoleLogFormatter consoleLogFormatter = new ConsoleLogFormatter();
        logger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(consoleLogFormatter);
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler("server.log", true);
            fileHandler.setFormatter(consoleLogFormatter);
            logger.addHandler(fileHandler);
        }
        catch (Exception exception) {
            logger.log(Level.WARNING, "Failed to log to server.log", exception);
        }
    }

    static final class ConsoleLogFormatter
    extends Formatter {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ConsoleLogFormatter() {
        }

        public String format(LogRecord logRecord) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.dateFormat.format(logRecord.getMillis()));
            Level level = logRecord.getLevel();
            if (level == Level.FINEST) {
                stringBuilder.append(" [FINEST] ");
            } else if (level == Level.FINER) {
                stringBuilder.append(" [FINER] ");
            } else if (level == Level.FINE) {
                stringBuilder.append(" [FINE] ");
            } else if (level == Level.INFO) {
                stringBuilder.append(" [INFO] ");
            } else if (level == Level.WARNING) {
                stringBuilder.append(" [WARNING] ");
            } else if (level == Level.SEVERE) {
                stringBuilder.append(" [SEVERE] ");
            } else if (level == Level.SEVERE) {
                stringBuilder.append(" [" + level.getLocalizedName() + "] ");
            }
            stringBuilder.append(logRecord.getMessage());
            stringBuilder.append('\n');
            Throwable throwable = logRecord.getThrown();
            if (throwable != null) {
                StringWriter stringWriter = new StringWriter();
                throwable.printStackTrace(new PrintWriter(stringWriter));
                stringBuilder.append(stringWriter.toString());
            }
            return stringBuilder.toString();
        }
    }
}

