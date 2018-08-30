package pe.exam;

import java.util.logging.Level;

public class Utility
{
    public static String WARN = "warning ";
    public static String ERROR = "error ";
    public static String MESSAGE = "message ";

    public static int WARN_ID = 3;
    public static int ERROR_ID = 2;
    public static int MESSAGE_ID = 1;

    public static String LOGGER_ERROR = "logger.error";
    public static String LOGGER_WARN = "logger.warn";
    public static String LOGGER_MESSAGE = "logger.message";

    public static String LOGGER_APPENDER_DATABASE = "logger.appender.database";
    public static String LOGGER_APPENDER_FILE = "logger.appender.file";
    public static String LOGGER_APPENDER_CONSOLE = "logger.appender.console";
    public static String LOGGER_APPENDER_FILE_PATH = "logger.file.path";
    
    public enum LevelLog {
        MESSAGE(1, Level.INFO),
        ERROR(2, Level.SEVERE),
        WARNING(3, Level.WARNING);

        private final int logType;
        private final Level level;

        private LevelLog(final int _logType,
                     final Level _level) {
            this.logType = _logType;
            this.level = _level;
        }
        
        public int getLogType()
        {
            return logType;
        }
        
        public Level getLevel()
        {
            return level;
        }
    }
}
