package pe.exam.logger;

import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.LogManager;

import pe.exam.Utility;
import pe.exam.appender.ConsoleAppender;
import pe.exam.appender.DataBaseAppender;
import pe.exam.appender.FileAppender;

public class JobLogger
    implements Logger
{
    private String name;
    private boolean initialized;
    private Properties configuration;
    private AppenderManager appenderManager;

    protected JobLogger(final String _name)
    {
        if (!initialized) {
            this.name = _name;
            appenderManager = AppenderManager.getAppenderManager();
            configuration = new Properties();
            try {
                configuration.load(JobLogger.class.getResourceAsStream("/logger.properties"));
            } catch (final Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            validateConfiguration();
            LogManager.getLogManager().reset();
            addAppenders();
            initialized = true;
        }
    }

    // For Testing
    protected JobLogger(final String _name,
                        final Properties _configuration)
    {
        this.name = _name;
        appenderManager = AppenderManager.getAppenderManager();
        appenderManager.removeAppenders();
        configuration = _configuration;
        validateConfiguration();
        LogManager.getLogManager().reset();
        addAppenders();
    }

    /**
     * Getter method for the variable {@link #name}.
     *
     * @return value of variable {@link #name}
     */
    public String getName()
    {
        return this.name;
    }

    private void addAppenders() {
        if (configuration.containsKey(Utility.LOGGER_APPENDER_CONSOLE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_CONSOLE))) {
            appenderManager.addAppender(new ConsoleAppender());
        }
        if (configuration.containsKey(Utility.LOGGER_APPENDER_FILE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_FILE))) {
            appenderManager.addAppender(new FileAppender());
        }
        if (configuration.containsKey(Utility.LOGGER_APPENDER_DATABASE)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_APPENDER_DATABASE))) {
            appenderManager.addAppender(new DataBaseAppender());
        }
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
            appenderManager.executeAppenders(message, Utility.MESSAGE_ID);
        }
    }

    @Override
    public void warn(final String _messageText) {
        if (configuration.containsKey(Utility.LOGGER_WARN)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_WARN))
                        && validateMessage(_messageText)) {
            final String message = formatMessage(_messageText, Utility.WARN);
            appenderManager.executeAppenders(message, Utility.WARN_ID);
        }
    }

    @Override
    public void error(final String _messageText) {
        if (configuration.containsKey(Utility.LOGGER_ERROR)
                        && Boolean.parseBoolean(configuration.getProperty(Utility.LOGGER_ERROR))
                        && validateMessage(_messageText)) {
            final String message = formatMessage(_messageText, Utility.ERROR);
            appenderManager.executeAppenders(message, Utility.ERROR_ID);
        }
    }

    private String formatMessage(final String _message, final String _logType) {
        return new StringBuilder(_logType).append(getNow()).append(": ").append(_message).toString();
    }

    private static String getNow() {
        return DateFormat.getDateInstance(DateFormat.LONG).format(new Date());
    }
}
