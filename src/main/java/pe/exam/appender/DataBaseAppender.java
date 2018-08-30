package pe.exam.appender;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DataBaseAppender
    implements Appender
{
    private static Connection connection;
    private static Properties dbProps;

    public DataBaseAppender() {
        connection = null;
        dbProps = new Properties();
        try {
            dbProps.load(DataBaseAppender.class.getResourceAsStream("/jdbc.properties"));
            final String url = dbProps.getProperty("url");
            connection = DriverManager.getConnection(url, dbProps);
        } catch (final IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(final String _messageText, final int logType)
    {
        Statement stmt;
        try {
            stmt = connection.createStatement();
            final String sql = new StringBuilder("insert into log_values (message,logtype) values('")
                            .append(_messageText).append("', ")
                            .append(String.valueOf(logType)).append(")")
                            .toString();
            stmt.executeUpdate(sql);
            //connection.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection()
        throws SQLException
    {
        connection.close();
    }

}
