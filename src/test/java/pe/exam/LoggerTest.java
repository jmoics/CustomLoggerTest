package pe.exam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pe.exam.appender.DataBaseAppender;
import pe.exam.logger.Logger;
import pe.exam.logger.LoggerFactoryTest;

public class LoggerTest
{
    private static Logger LOGGER;
    private static Properties props;
    Connection connection;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpBefore()
        throws Exception
    {
        final Properties dbProps = new Properties();
        try {
            dbProps.load(DataBaseAppender.class.getResourceAsStream("/jdbc.properties"));
            connection = DriverManager.getConnection(dbProps.getProperty("url"), dbProps);
            final String delete = "delete from log_values where message like '%testingmessage' or message like '%testingerror' or message like '%testingwarning'";
            final Statement stmt = connection.createStatement();
            stmt.executeUpdate(delete);

            // to capture the String of the console
            final PrintStream ps = new PrintStream(outContent);
            System.setErr(ps);

            final File file = new File("C:/logs/logFile.txt");
            file.getParentFile().mkdirs(); // Will create parent directories if not exists
            file.createNewFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void setUpAfter()
        throws Exception
    {

    }

    @Test
    public void testValidConfiguration4Appender()
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());
        try {
            LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
            fail("An exception was expected");
        } catch (final RuntimeException e) {
        }
    }

    @Test
    public void testValidConfiguration4LogType()
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());
        try {
            LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
            fail("An exception was expected");
        } catch (final RuntimeException e) {
        }
    }

    @Test
    public void testDBAppender4Message()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingmessage");

        final String sql = "select count(*) from log_values where message like '%testingmessage' and logtype=1";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(1, cant);
    }

    @Test
    public void testDBAppender4MessageInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingmessage");

        final String sql = "select count(*) from log_values where message like '%testingmessage' and logtype=1";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(0, cant);
    }

    @Test
    public void testDBAppender4Error()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerror");

        final String sql = "select count(*) from log_values where message like '%testingerror' and logtype=2";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(1, cant);
    }

    @Test
    public void testDBAppender4ErrorInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerror");

        final String sql = "select count(*) from log_values where message like '%testingerror' and logtype=2";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(0, cant);
    }

    @Test
    public void testDBAppender4Warning()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarning");

        final String sql = "select count(*) from log_values where message like '%testingwarning' and logtype=3";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(1, cant);
    }

    @Test
    public void testDBAppender4WarningInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarning");

        final String sql = "select count(*) from log_values where message like '%testingwarning' and logtype=3";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(0, cant);
    }

    @Test
    public void testConsoleAppender4Message()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingmessage");

        final String console = outContent.toString();
        assertTrue(console.contains("testingmessage"));
    }

    @Test
    public void testConsoleAppender4MessageInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingmessage");

        final String console = outContent.toString();
        assertTrue(!console.contains("testingmessage"));
    }

    @Test
    public void testConsoleAppender4Error()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerror");

        final String console = outContent.toString();
        assertTrue(console.contains("testingerror"));
    }

    @Test
    public void testConsoleAppender4ErrorInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerror");

        final String console = outContent.toString();
        assertTrue(!console.contains("testingerror"));
    }

    @Test
    public void testConsoleAppender4Warning()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarning");

        final String console = outContent.toString();
        assertTrue(console.contains("testingwarning"));
    }

    @Test
    public void testConsoleAppender4WarningInactive()
        throws SQLException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.FALSE.toString());

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarning");

        final String console = outContent.toString();
        assertTrue(!console.contains("testingwarning"));
    }

    @Test
    public void testFileAppender4Message()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingfile");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingfile")) {
                assertTrue(true);
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void testFileAppender4MessageInactive()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingfileInactive");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingfileInactive")) {
                fail("Shouldn't find nothing");
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void testFileAppender4Error()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerror");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingerror")) {
                assertTrue(true);
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void testFileAppender4ErrorInactive()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.error("testingerrorInactive");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingerrorInactive")) {
                fail("Shouldn't find nothing");
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void testFileAppender4Warning()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarning");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingwarning")) {
                assertTrue(true);
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void testFileAppender4WarningInactive()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.FALSE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.warn("testingwarningInactive");

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingwarningInactive")) {
                fail("Shouldn't find nothing");
                break;
            }
        }
        scanner.close();
    }

    @Test
    public void test4All()
        throws SQLException, IOException
    {
        props = new Properties();
        props.setProperty(Utility.LOGGER_ERROR, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_MESSAGE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_WARN, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_CONSOLE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_DATABASE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE, Boolean.TRUE.toString());
        props.setProperty(Utility.LOGGER_APPENDER_FILE_PATH, "C:/logs/logFile.txt");

        LOGGER = LoggerFactoryTest.getLogger(LoggerTest.class, props);
        LOGGER.message("testingmessage");
        LOGGER.error("testingerror");
        LOGGER.warn("testingwarning");

        final String sql = "select count(*) from log_values where (message like '%testingmessage' and logtype=1) "
                    + "or (message like '%testingerror' and logtype=2) or (message like '%testingwarning' and logtype=3)";
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(sql);
        res.next();
        final int cant = res.getInt(1);
        assertEquals(3, cant);

        final String console = outContent.toString();
        assertTrue(console.contains("testingmessage"));
        assertTrue(console.contains("testingerror"));
        assertTrue(console.contains("testingwarning"));

        final String filePath = props.getProperty(Utility.LOGGER_APPENDER_FILE_PATH);
        final Path path = Paths.get(filePath);
        final Charset encoding = Charset.forName("UTF-8");
        final Scanner scanner = new Scanner(path, encoding.name());
        final List<Boolean> lst = new ArrayList<>();
        while (scanner.hasNext()) {
            final String line = scanner.nextLine();
            if (line.contains("testingmessage")) {
                lst.add(true);
            }
            if (line.contains("testingerror")) {
                lst.add(true);
            }
            if (line.contains("testingwarning")) {
                lst.add(true);
            }
        }
        assertEquals(3, lst.size());
        scanner.close();
    }
}
