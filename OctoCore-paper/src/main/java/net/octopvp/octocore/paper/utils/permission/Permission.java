package net.octopvp.octocore.paper.utils.permission;

public enum Permission implements PermissionString {
    NOTHING(""),
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
    TEST("test")

    ;
    private final String node;
    private Permission(String node){
        this.node = node;
    }

    final public String getNode() {
        return name();
    }
}
