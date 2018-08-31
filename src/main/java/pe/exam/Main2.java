package pe.exam;

import pe.exam.logger.Logger;
import pe.exam.logger.LoggerFactory;

public class Main2
{
    private static Logger LOGGER = LoggerFactory.getLogger(Main2.class);

    public void doSomething() {
        LOGGER.error("chau");
        LOGGER.warn("volvi");
    }
}
