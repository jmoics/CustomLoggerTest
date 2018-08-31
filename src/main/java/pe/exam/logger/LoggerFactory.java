package pe.exam.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LoggerFactory
{
    private static ConcurrentMap<String, JobLogger> loggers = new ConcurrentHashMap<>();

    public static Logger getLogger(final String _name) {
        JobLogger logger = loggers.get(_name);
        if (logger == null) {
            logger = new JobLogger(_name);
            final JobLogger oldLogger = loggers.putIfAbsent(_name, logger);
            if (oldLogger != null)
                logger = oldLogger;
        }
        return logger;
    }

    public static Logger getLogger(final Class<?> _clazz) {
        return getLogger(_clazz.getName());
    }
}
