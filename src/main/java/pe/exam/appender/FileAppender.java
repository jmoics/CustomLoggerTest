package pe.exam.appender;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import pe.exam.Utility;

public class FileAppender
    extends LoggerHandlerAppender
{
    //private static Logger logger;

    public FileAppender()
    {
        //LogManager.getLogManager().reset();
        logger = Logger.getLogger("MyLog");

        final Properties loggerProps = new Properties();
        try {
            loggerProps.load(FileAppender.class.getResourceAsStream("/logger.properties"));
            final String filePath = loggerProps.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
            final FileHandler fh = new FileHandler(filePath);
            logger.addHandler(fh);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    // For Testing
    public FileAppender(final Properties _loggerProps)
    {
        //LogManager.getLogManager().reset();
        logger = Logger.getLogger("MyLog");
        final String filePath = _loggerProps.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        try {
            final FileHandler fh = new FileHandler(filePath);
            logger.addHandler(fh);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void removeHandler() {
        for (final Handler handler : logger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                setHandlerTemp(handler);
                logger.removeHandler(handler);
            }
        }
    }

    @Override
    protected void reinsertHandler() {
        logger.addHandler(getHandlerTemp());
    }
}
