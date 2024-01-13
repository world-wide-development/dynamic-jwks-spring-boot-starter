package core.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public final class LogbackUtils {

    private LogbackUtils() {
        // Suppresses default constructor
    }

    public static <T> void changeLoggingLevel(@NonNull final Level level, @NonNull Class<T> classType) {
        if (LoggerFactory.getLogger(classType) instanceof Logger logbackLogger) {
            logbackLogger.setLevel(level);
        }
    }

}
