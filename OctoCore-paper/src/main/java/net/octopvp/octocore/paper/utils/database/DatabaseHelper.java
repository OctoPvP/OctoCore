package net.octopvp.octocore.paper.utils.database;

import net.octopvp.octocore.paper.utils.Logger;

import java.util.HashMap;


public enum DatabaseHelper {
    CREATE_PLAYERDATA_TABLE("CREATE TABLE IF NOT EXISTS PlayerData(UUID varchar(36),Name varchar(255),Xp bigint DEFAULT 1, Coins bigint DEFAULT 0,LastLogon bigint,LastLogoff bigint,Frozen bit DEFAULT 0);"),
    CREATE_STAFFDATA_TABLE("CREATE TABLE IF NOT EXISTS StaffData(UUID varchar(36),Name varchar(255),Rank varchar(255),StaffMode bit,LastServer varchar(255));"),
    CREATE_NICK_TABLE("CREATE TABLE IF NOT EXISTS Nicks(UUID varchar(36), Nick varchar(255),NickedOn bigint, Enabled bit);"),
    CREATE_2FA_TABLE("CREATE TABLE IF NOT EXISTS Auth(UUID varchar(36),Ip varchar(255),Key varchar(36));"),
    ADD_PLAYERDATA("INSERT INTO playerdata VALUES(\"%1\",\"%2\",%3,%4,%5,%6,%7);"),
    //INSERT INTO staffdata VALUES("d44a8cee-acd5-442d-98eb-348606780512","Badbird5907","Owner",0,"KitPvP");
    ADD_STAFFDATA("INSERT INTO staffdata VALUES(\"%1,\"%2\",\"%3\",%4,%5);"),
    GET_PROFILE("SELECT * FROM `playerdata` WHERE `UUID` = \"%1\""),
    GET_BLAKLIST_WORDS("SELECT * FROM `swearwords`;"),
    GET_CACHED_NAME(" ");
    private String sql;
    DatabaseHelper(String sql){
        this.sql = sql;
    }
    public String getSql(String... values){

        String returned = sql;
        String finalreturn = returned;
        int i = 0;
        HashMap<Integer,String> replaced = new HashMap<>();
        for (String value : values) {
            i++;
            replaced.put(i, value);
        }
        for (Integer k : replaced.keySet()) {
            String v = replaced.get(k);
            Logger.debug(k + " | " + v);
            finalreturn.replaceAll("%" + k, v);
            Logger.debug("Replacing %" + k + " With: " + v);
        }
        return finalreturn;
    }
}
