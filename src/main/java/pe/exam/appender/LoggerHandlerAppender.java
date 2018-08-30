package pe.exam.appender;

import java.util.logging.Handler;
import java.util.logging.Logger;

import pe.exam.Utility;
import pe.exam.Utility.LevelLog;

public abstract class LoggerHandlerAppender
    implements Appender
{
    private Handler handlerTemp;
    protected static Logger logger;

    /**
     * To remove others handlers when an specific handler is used, storing the remove handler to put again later.
     */
    protected abstract void removeHandler();
    /**
     * Reinsert the removed handler.
     */
    protected abstract void reinsertHandler();

    /**
     * Getter method for the variable {@link #handlerTemp}.
     *
     * @return value of variable {@link #handlerTemp}
     */
    public Handler getHandlerTemp()
    {
        return this.handlerTemp;
    }

    /**
      * Setter method for variable {@link #handlerTemp}.
      *
      * @param _handlerTemp value for variable {@link #handlerTemp}
      */
    public void setHandlerTemp(final Handler _handlerTemp)
    {
        this.handlerTemp = _handlerTemp;
    }

    @Override
    public void execute(final String _message,
                        final int _logType)
    {
        removeHandler();
        if (_logType == Utility.LevelLog.MESSAGE.getLogType()) {
            logger.log(Utility.LevelLog.MESSAGE.getLevel(), _message);
        } else if (_logType == Utility.LevelLog.ERROR.getLogType()) {
            logger.log(Utility.LevelLog.ERROR.getLevel(), _message);
        } else {
            logger.log(Utility.LevelLog.WARNING.getLevel(), _message);
        }
        reinsertHandler();
    }

}
