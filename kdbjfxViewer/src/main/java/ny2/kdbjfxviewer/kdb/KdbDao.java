package ny2.kdbjfxviewer.kdb;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QConnection;
import com.exxeleron.qjava.QException;

@Repository
public class KdbDao {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // connection parameter
    @Value("${kdb.tp.host}")
    private String host;

    @Value("${kdb.tp.port}")
    private int port;

    @Value("${kdb.tp.username}")
    private String username;

    @Value("${kdb.tp.password}")
    private String password;

    /** select用のConnection */
    private QConnection connection;


    // //////////////////////////////////////
    // Field - Query
    // //////////////////////////////////////




    // //////////////////////////////////////
    // Constructor / setup
    // //////////////////////////////////////

    public KdbDao() {
        logger.info("Create instance.");
    }

    @PostConstruct
    public void init() {
        logger.info("PostConstruct instance.");

        // 回数が少ないのでConnectionは都度作成する
        // createConnectionForSelect();
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Queryを実行します
     *
     * @param query
     * @param parameters
     * @return
     */
    public Object query(String query, Object... parameters) {
        QConnection connection = createConnection();
        try {
            Object response = connection.sync(query, parameters);
            return response;
        } catch (QException | IOException e) {
            logger.error("Error in query.", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
        }
    }


    /**
     * Query to RT
     * @param port
     * @param query
     * @param parameters
     * @return
     */
    private Object executeQuery(int port, String query, final Object... parameters) {
        QBasicConnection con = openConnection(host, port, username, password);
        try {
            Object response = con.sync(query, parameters);
            return response;
        } catch (Exception e) {
            logger.error("Error in query. " + query, e);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    // //////////////////////////////////////
    // Method - Connection
    // //////////////////////////////////////

    private QConnection createConnection() {
        // すでに接続済みであればcloseする
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                // Do nothing.
            }
        }

        connection = openConnection(host, port, username, password);
        return connection;
    }

    private QBasicConnection openConnection(String host, int port, String username, String password) {
        QBasicConnection connection = null;
        try {
            connection = new QBasicConnection(host, port, username, password);
            connection.open();
        } catch (QException | IOException e) {
            logger.error("Cannnot create connection. host=" + host + "port=" + port, e);
        }
        return connection;
    }

    private void closeConnection(QConnection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (connection.isConnected()) {
                connection.close();
            }
        } catch (IOException e) {
            logger.error("Error in closing connection.", e);
        }
    }


    // //////////////////////////////////////
    // Test
    // //////////////////////////////////////

}
