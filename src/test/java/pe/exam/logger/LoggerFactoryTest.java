package pe.exam.logger;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LoggerFactoryTest
{
    private static ConcurrentMap<String, JobLogger> loggers = new ConcurrentHashMap<>();

    public static Logger getLogger(final String _name,
                                   final Properties _properties) {

        final JobLogger logger = new JobLogger(_name, _properties);
        final JobLogger oldLogger = loggers.putIfAbsent(_name, logger);
        if (oldLogger != null) {
            //logger = oldLogger;
        }

        return logger;
    }

    public static Logger getLogger(final Class<?> _clazz,
                                   final Properties _properties) {
        return getLogger(_clazz.getName(), _properties);
    }
}
