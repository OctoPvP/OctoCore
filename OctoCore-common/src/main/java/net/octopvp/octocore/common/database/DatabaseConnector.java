package net.octopvp.octocore.common.database;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;

@Getter
@Setter
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
}
