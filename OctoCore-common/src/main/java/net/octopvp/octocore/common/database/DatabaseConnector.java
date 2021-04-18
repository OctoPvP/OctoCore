package net.octopvp.octocore.common.database;

import java.sql.Connection;

public class DatabaseConnector {
    private String url,username,password,db;
    private Connection connection;
    public DatabaseConnector(String url, String username, String password, String db){
        this.url = url;
        this.username = username;
        this.password = password;
        this.db = db;
    }
    public void connect(){

    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDb() {
        return this.db;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
