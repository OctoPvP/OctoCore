package net.octopvp.octocore.paper.database.redis.object;

public enum JedisAction {
    SERVER_DATA, PLAYER_DATA, PLAYER_MESSAGE, REPORT_SAVE,
    /**
     * Should be run on master server only (litebans) until we replace litebans
     */
    EXECUTE_PUNISHMENT
    , SERVER_COMMAND, GRANTS_UPDATE,
    SERVER_ONLINE, SERVER_OFFLINE,
    UPDATE_SQL_FILTER_DB,UPDATE_SQL_DISABLED_COMMANDS_DB,
    STAFF_CHAT,STAFF_CONNECT, STAFF_SWITCH, STAFF_DISCONNECT,
    ADMIN_CHAT,
    SEND_DISCORD_MESSAGE
    ;
}
