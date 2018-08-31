package pe.exam;

import pe.exam.logger.Logger;
import pe.exam.logger.LoggerFactory;

public class Main
{
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args)
    {
        LOGGER.message("hola");
        final Main2 main2 = new Main2();
        main2.doSomething();
    }

}
