package net.octopvp.octocore.paper.database;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.utils.Logger;

import java.util.HashMap;


public enum DatabaseHelper {
    CREATE_BLACKLIST_WORD_TABLE("CREATE TABLE IF NOT EXISTS swearwords(word varchar(255),report bit(1));"),
    GET_BLAKLIST_WORDS("SELECT * FROM `swearwords`;"),
    DISABLE_COMMAND("INSERT INTO disabled_commands VALUES(%1,%2,%3,%4);"),
    CREATE_DISABLED_COMMANDS_TABLE("CREATE TABLE IF NOT EXISTS disabled_commands(cmd varchar(255),DisabledOn Bigint,DisabledUntil Bigint,DisabledServer varchar(255),DisabledBy varchar(36));"),
    ADD_ERROR_DATA("INSERT INTO errordata VALUES(%1,%2)"),
    GET_SETTINGS("SELECT * FROM `settings`;"),
    CREATE_SETTINGS_TABLE("CREATE TABLE IF NOT EXISTS settings(k varchar(255),v varchar(255));"),
    GET_PLACEHOLDERS("SELECT * FROM `placeholders`;"),
    CREATE_PLACEHOLDERS_TABLE("CREATE TABLE IF NOT EXISTS placeholders(placeholder varchar(255),replace varchar(255));"),
    GET_DISABLED_COMMANDS("SELECT * FROM `disabled_commands`;");
    private String sql;
    DatabaseHelper(String sql){
        this.sql = sql;
    }
    public String getSql(String... values){
        return StringUtils.replacePlaceholders(sql,values);
    }
}
