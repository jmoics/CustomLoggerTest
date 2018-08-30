package pe.exam.logger;

import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.LogManager;

import pe.exam.Utility;
import pe.exam.appender.Appender;
import pe.exam.appender.ConsoleAppender;
import pe.exam.appender.DataBaseAppender;
import pe.exam.appender.FileAppender;

public class JobLogger
    implements Logger
{
    private static boolean initialized;
    private static Properties configuration;
    private static Appender DBAPPENDER;
    private static Appender CONSOLEAPPENDER;
    private static Appender FILEAPPENDER;

    public JobLogger()
    {
        if (!initialized) {
            configuration = new Properties();
            try {
                configuration.load(JobLogger.class.getResourceAsStream("/logger.properties"));
            } catch (final Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            validateConfiguration();
            LogManager.getLogManager().reset();
            DBAPPENDER = new DataBaseAppender();
            CONSOLEAPPENDER = new ConsoleAppender();
            FILEAPPENDER = new FileAppender();
            initialized = true;
        }
    }

    // For Testing
    public JobLogger(final Properties _configuration) {
        configuration = _configuration;
        validateConfiguration();
        LogManager.getLogManager().reset();
        DBAPPENDER = new DataBaseAppender();
        CONSOLEAPPENDER = new ConsoleAppender();
        FILEAPPENDER = new FileAppender();
    }

    public static JobLogger getLogger() {
        return new JobLogger();
    }

    private void validateConfiguration()
    {
        final boolean logFile = configuration.containsKey(Utility.LOGGER_APPENDER_FILE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_FILE));
        final boolean logConsole = configuration.containsKey(Utility.LOGGER_APPENDER_CONSOLE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_CONSOLE));
        final boolean logDB = configuration.containsKey(Utility.LOGGER_APPENDER_DATABASE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_DATABASE));
        if (!logFile && !logConsole && !logDB) {
            throw new RuntimeException("Invalid configuration");
        }
        final boolean logError = configuration.containsKey(Utility.LOGGER_ERROR)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_ERROR));
        final boolean logWarn = configuration.containsKey(Utility.LOGGER_WARN)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_WARN));
        final boolean logMessage = configuration.containsKey(Utility.LOGGER_MESSAGE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_MESSAGE));
        if ((!logError && !logMessage && !logWarn)) {
            throw new RuntimeException("Error or Warning or Message must be specified");
        }
    }

    private boolean validateMessage(final String _messageText) {
        return _messageText != null && _messageText.trim().length() > 0;
    }

    @Override
    public void message(final String _messageText) {
        if (configuration.containsKey(Utility.LOGGER_MESSAGE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_MESSAGE))
                        && validateMessage(_messageText)) {
            final String message = formatMessage(_messageText, Utility.MESSAGE);
            publishMessage(message, Utility.MESSAGE_ID);
        }
    }

    @Override
    public void warn(final String _messageText) {
        if (configuration.containsKey(Utility.LOGGER_WARN)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_WARN))
                        && validateMessage(_messageText)) {
            final String message = formatMessage(_messageText, Utility.WARN);
            publishMessage(message, Utility.WARN_ID);
        }
    }

    @Override
    public void error(final String _messageText) {
        if (configuration.containsKey(Utility.LOGGER_ERROR)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_ERROR))
                        && validateMessage(_messageText)) {
            final String message = formatMessage(_messageText, Utility.ERROR);
            publishMessage(message, Utility.ERROR_ID);
        }
    }

    private String formatMessage(final String _message, final String _logType) {
        return new StringBuilder(_logType).append(getNow()).append(": ").append(_message).toString();
    }

    private void publishMessage(final String _message, final int _logType) {
        if (configuration.containsKey(Utility.LOGGER_APPENDER_FILE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_FILE))) {
            FILEAPPENDER.execute(_message, _logType);
        }

        if (configuration.containsKey(Utility.LOGGER_APPENDER_CONSOLE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_CONSOLE))) {
            CONSOLEAPPENDER.execute(_message, _logType);
        }

        if (configuration.containsKey(Utility.LOGGER_APPENDER_DATABASE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_DATABASE))) {
            DBAPPENDER.execute(_message, _logType);
        }
    }

    private static String getNow() {
        return DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
    }
}
