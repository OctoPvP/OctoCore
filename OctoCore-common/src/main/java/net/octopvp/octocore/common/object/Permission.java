package net.octopvp.octocore.common.object;

public enum Permission  {
    NOTHING(""),

    ADMIN("octocore.admin"),

    COMMAND_NICK("octocore.command.nick"),
    COMMAND_UNNICK( "octocore.command.unnick"),
    COMMAND_NICK_OTHERS("octocore.command.nick.others"),
    COMMAND_UNNICK_OTHERS("octocore.command.unnick.others"),
    COMMAND_PERMISSION_INFO("octocore.command.perminfo"),
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
    REFRESH_SKIN("octocore.command.refreshskin"),

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
    DELETE_RANK("octocore.command.deleterank"),
    EDIT_RANK("octocore.command.editrank"),


    PUNISHMENT_BASE("octocore.punish."),
    PUNISHMENT_BASE_COMMAND("octocore.punish.command."),
    PUNISHMENT_SEE_SILENT(PUNISHMENT_BASE + "see.silent"),
    PUNISHMENT_UNBLACKLIST(PUNISHMENT_BASE + "unblacklist"),
    PUNISHMENT_UNBAN(PUNISHMENT_BASE + "unblacklist"),
    PUNISHMENT_UNMUTE(PUNISHMENT_BASE + "unblacklist"),
    PUNISHMENT_STAFFROLLBACK(PUNISHMENT_BASE_COMMAND + "staffrollback"),
    PUNISHMENT_STAFF_HISTORY(PUNISHMENT_BASE_COMMAND + "staffhistory"),
    PUNISHMENT_HISTORY(PUNISHMENT_BASE_COMMAND + "history"),

    PUNISHMENT_KICK(PUNISHMENT_BASE_COMMAND + "kick"),
    PUNISHMENT_BAN(PUNISHMENT_BASE_COMMAND + "ban"),
    PUNISHMENT_IPBAN(PUNISHMENT_BASE_COMMAND + "ipban"),
    PUNISHMENT_BLACKLIST(PUNISHMENT_BASE_COMMAND + "blacklist"),
    PUNISHMENT_WARN(PUNISHMENT_BASE_COMMAND + "warn"),
    PUNISHMENT_MUTE(PUNISHMENT_BASE_COMMAND + "mute"),
    PUNISHMENT_SEE_JOIN_ALERT(PUNISHMENT_BASE + "joinalert"),

    TAG_ADMIN_MENU("octocore.command.tagadmin"),

    TEST("test");
    private final String node;

    Permission(String node) {
        this.node = node;
    }

    final public String getNode() {
        return this.node;
    }

    @Override
    public String toString() {
        return getNode();
    }
}
