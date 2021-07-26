package net.octopvp.octocore.common.object;

public enum Permission  {
    NOTHING(""),

    ADMIN("octocore.admin"),

    COMMAND_NICK("octocore.command.nick"),
    COMMAND_UNNICK( "octocore.command.unnick"),
    COMMAND_NICK_OTHERS("octocore.command.nick.others"),
    COMMAND_UNNICK_OTHERS("octocore.command.unnick.others"),
    USE_UNICODE_CHAT("octocore.chat.unicode"),
    USE_COLOR_CHAT("octocore.chat.color"),
    STAFF_MODULES("octocore.lunar.staff"),
    SEEN("octocore.command.seen"),
    CLEAR_CHAT("octocore.command.clearchat"),
    BYPASS_CLEAR_CHAT("octocore.command.clearchat.bypass"),
    LOG_WORLDEDIT("octocore.action.logworldedit"),
    START_RAFFLE("octocore.raffle.start"),
    END_RAFFLE("octocore.raffle.end"),
    ENTER_RAFFLE("octocore.raffle.enter"),
    SYS_INFO("octocore.command.sysinfo"),
    USE_COLOR_NAME("octocore.usecolorname"),
    LIST_PLAYERS("octocore.command.list"),
    PING_COMMAND_OTHER("octocore.command.ping.others"),
    PUNISH_PLAYER("octocore.command.punish"),
    SETUP_2FA("octocore.command.2fa.setup"),
    VIEW_HISTORY("octocore.command.history"),
    FORCE_AUTH("octocore.command.forceauth"),
    VANISH("octocore.action.vanish"),
    TRASH("octocore.command.trash"),

    SEND_JOIN_MESSAGE("octocore.staff.message.send.join"),
    SEND_LEAVE_MESSAGE("octocore.staff.message.send.leave"),
    RECEIVE_JOIN_MESSAGE("octocore.staff.message.receive.join"),
    RECEIVE_LEAVE_MESSAGE("octocore.staff.message.receive.leave"),
    RECEIVE_SWITCH_MESSAGE("octocore.staff.message.receive.switch"),
    SEND_SWITCH_MESSAGE("octocore.staff.message.send.switch"),
    RECEIVE_SERVER_ONLINE_MESSAGE("octocore.staff.message.receive.server.online"),
    RECEIVE_SERVER_OFFLINE_MESSAGE("octocore.staff.message.receive.server.offline"),

    STAFFCHAT("octocore.staff.chat"),
    ADMINCHAT("octocore.admin.chat"),
    ADMIN_ALERT("octocore.admin.alert"),
    STAFF_ALERT("octocore.staff.alert"),

    RECEIVE_AUDIT_WORLDEDIT("octocore.staff.audit.worldedit"),
    RECEIVE_AUDIT_AUTH_FAIL("octocore.staff.audit.authfail"),

    BYPASS_SPAM_PROT("octocore.bypass.spam"),

    ENCHANT("octocore.command.enchant"),
    GIVE("octocore.command.give"),

    CREATIVE("octocore.command.gamemode.creative"),
    SPECTATOR("octocore.command.gamemode.spectator"),
    ADVENTURE("octocore.command.gamemode.adventure"),
    SURVIVAL("octocore.command.gamemode.survival"),

    CALL_GC("octocore.command.callgc"),
    LOOP("octocore.command.loop"),

    KABOOM("octocore.command.fun.kaboom"),

    EXECUTE_ON_ALL_SERVERS("octocore.command.executeall"),
    CONSOLE_EXECUTE("octocore.command.console"),

    GIVE_TAG("octocore.command.givetag"),

    TROLL_DEMO_MENU("troll.demomenu"),

    CUSTOM_COLOR("octocore.customcolor"),

    GRANT("octocore.command.grant"),
    GRANT_ALL("octocore.grant.all"),

    CREATE_RANK("octocore.command.createrank"),

    TEST("test");
    private final String node;
    Permission(String node){
        this.node = node;
    }

    final public String getNode() {
        return name();
    }

    @Override
    public String toString() {
        return getNode();
    }
}
