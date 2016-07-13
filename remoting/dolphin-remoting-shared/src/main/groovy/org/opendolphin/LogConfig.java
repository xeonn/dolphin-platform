package org.opendolphin;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Keep logging details in one place
 **/
public class LogConfig {

    private static final Logger ROOT_LOGGER = Logger.getLogger("");

    private static final Logger DOLPHIN_LOGGER = Logger.getLogger("org.opendolphin");

    public static void noLogs() {
        logOnLevel(DOLPHIN_LOGGER, Level.OFF);
    }

    public static void logCommunication() {
        logOnLevel(DOLPHIN_LOGGER, Level.INFO);
    }

    public static void logOnLevel(Level level) {
        logOnLevel(DOLPHIN_LOGGER, level);
    }

    /**
     * One may choose to use DOLPHIN_LOGGER or ROOT_LOGGER.
     */
    public static void logOnLevel(Logger logger, final Level level) {
        logger.setLevel(level);
        for(Handler handler : logger.getHandlers()) {
            handler.setLevel(level);
            if(handler instanceof ConsoleHandler) {
                handler.setFormatter(new ShortFormatter());
            }
        }
    }
}

