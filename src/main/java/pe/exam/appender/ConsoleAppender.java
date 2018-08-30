package pe.exam.appender;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class ConsoleAppender
    extends LoggerHandlerAppender
{
    //private static Logger logger;

    public ConsoleAppender()
    {
        //LogManager.getLogManager().reset();
        logger = Logger.getLogger("MyLog");
        final ConsoleHandler ch = new ConsoleHandler();
        logger.addHandler(ch);
    }

    @Override
    protected void removeHandler() {
        for (final Handler handler : logger.getHandlers()) {
            if (handler instanceof FileHandler) {
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
