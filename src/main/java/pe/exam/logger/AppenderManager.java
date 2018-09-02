package pe.exam.logger;

import java.util.ArrayList;
import java.util.List;

import pe.exam.appender.Appender;

public class AppenderManager
{
    private final List<Appender> appenders = new ArrayList<>();
    private static AppenderManager appenderManager;

    private AppenderManager() {
    }

    public static synchronized AppenderManager getAppenderManager() {
        if (appenderManager == null) {
            appenderManager = new AppenderManager();
        }
        return appenderManager;
    }

    public void addAppender(final Appender _appender) {
        this.appenders.add(_appender);
    }

    protected void removeAppenders() {
        this.appenders.clear();
    }

    public void executeAppenders(final String _message,
                                 final int _logType) {
        for (final Appender appender : this.appenders) {
            appender.execute(_message, _logType);
        }
    }
}
