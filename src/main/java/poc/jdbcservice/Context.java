package poc.jdbcservice;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Context implements Serializable {

    private final transient Connection connection = null;

    public Context(String user, String password, String url) throws SQLException {
        // TODO: might assemble connection string with user and password in addition to URL
        Connection conn = DriverManager.getConnection(url);
    }

    public Connection getConnection() {
        return connection;
    }

    //public static FunctionEx<Processor.Context, Context> create(String user, String password, String url) {
    public static Context create(String user, String password, String url) {
        try {
            return new Context(user, password, url);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
