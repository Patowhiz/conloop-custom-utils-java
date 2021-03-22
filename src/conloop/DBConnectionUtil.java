package conloop;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author Pato A class that handles the connection to the database
 */
public class DBConnectionUtil {

    /**
     * connects in standalone mode
     *
     * @param databaseName
     * @param dbUserName
     * @param dbPassword
     * @return
     */
    public static Connection connectToDerbyDb(String databaseName, String dbUserName, String dbPassword) {
        return connectToDerbyDbViaUrl("jdbc:derby:" + databaseName, dbUserName, dbPassword);
    }

    /**
     * connects in server mode
     *
     * @param hostName
     * @param databaseName
     * @param dbUserName
     * @param dbPassword
     * @return
     */
    public static Connection connectToDerbyDb(String hostName, String databaseName, String dbUserName, String dbPassword) {
        return connectToDerbyDb(hostName, 1527, databaseName, dbUserName, dbPassword);
    }

    /**
     * connects in server mode
     *
     * @param hostName
     * @param port
     * @param databaseName
     * @param dbUserName
     * @param dbPassword
     * @return
     */
    public static Connection connectToDerbyDb(String hostName, int port, String databaseName, String dbUserName, String dbPassword) {
        return connectToDerbyDbViaUrl("jdbc:derby://" + hostName + ":" + port + "/" + databaseName, dbUserName, dbPassword);
    }

    /**
     * connects in server mode
     *
     * @param url
     * @param dbUserName
     * @param dbPassword
     * @return
     */
    public static Connection connectToDerbyDbViaUrl(String url, String dbUserName, String dbPassword) {
        Connection con = null;
        try {
            Properties connProps = new Properties(); //Connection properties
            connProps.put("user", dbUserName);
            connProps.put("password", dbPassword);
            con = DriverManager.getConnection(url, connProps);
        } catch (Exception ex) {
            LoggingUtil.e(DBConnectionUtil.class, "Could not get database connection " + ex.getMessage());
        }//end outer try//end outer try//end outer try//end outer try//end outer try//end outer try//end outer try//end outer try
        return con;
    }

    public static NetworkServerControl createDerbyServer(String hostName, int portNumber) throws UnknownHostException, Exception {
        NetworkServerControl server;
        server = new NetworkServerControl(InetAddress.getByName(hostName), portNumber);
        //server = new NetworkServerControl(InetAddress.getLocalHost(), 1527);
        //server = new NetworkServerControl(InetAddress.getByName("0.0.0.0"), 1527);
        // server.start(null);

        return server;

    }//end method

    /**
     * default port used is 1572
     * @param hostName
     * @return
     * @throws UnknownHostException
     * @throws Exception 
     */
    public static NetworkServerControl createDerbyServer(String hostName) throws UnknownHostException, Exception {
        return createDerbyServer(hostName, 1527);
    }

    /**
     * default port used is 1572
     * @param hostName
     * @return 
     */
    public NetworkServerControl createAndStartDerbyServer(String hostName) {
        return createAndStartDerbyServer(hostName, 1527);
    }

    /**
     * Start a Network Server This method will launch a separate thread and start Network Server. 
     * This method may return before the server is ready to accept connections. Use the ping method to verify that the server has started.
     * @param hostName
     * @param portNumber
     * @return 
     */
    public static NetworkServerControl createAndStartDerbyServer(String hostName, int portNumber) {
        try {
            NetworkServerControl server = createDerbyServer(hostName, portNumber);
            server.start(null);
            return server;
        } catch (UnknownHostException ex) {
            LoggingUtil.e(DBConnectionUtil.class, ex.getMessage());
        } catch (Exception ex) {
            LoggingUtil.e(DBConnectionUtil.class, ex.getMessage());
        }
        return null;
    }//end method

    public static boolean closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
            return true;
        } catch (SQLException ex) {
            LoggingUtil.e(DBConnectionUtil.class, "Error in closing connection: " + ex.getMessage());
            return false;
        }
    }

}//end class
