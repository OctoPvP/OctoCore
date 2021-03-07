package net.octopvp.octocore.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

@Getter
@Setter
public class ConnectionPoolManager {


    private HikariDataSource dataSource;

    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;

    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;
    private String testQuery;
    private Logger logger;

    public ConnectionPoolManager(String hostname, String port, String database, String username, String password, int minimumConnections, int maximumConnections, long connectionTimeout, String testQuery, Logger logger) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.minimumConnections = minimumConnections;
        this.maximumConnections = maximumConnections;
        this.connectionTimeout = connectionTimeout;
        this.testQuery = testQuery;
        this.logger = logger;
        setupPool();
    }

    private void setupPool() {
        logger.info("Setting up Hikari Pool");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        hostname +
                        ":" +
                        port +
                        "/" +
                        database
        );
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setConnectionTestQuery(testQuery);
        logger.info("Setting up Hikari Datasource");
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource(){
        return dataSource;
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}