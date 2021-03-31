package net.octopvp.octocore.paper.utils.permission;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@Getter
public enum Permission implements PermissionString {
    NOTHING(""),
    COMMAND_NICK("octocore.command.nick"),
    COMMAND_UNNICK( "octocore.command.unnick"),
    COMMAND_NICK_OTHERS("octocore.command.nick.others"),
    COMMAND_UNNICK_OTHERS("octocore.command.unnick.others"),
    USE_UNICODE_CHAT("octocore.chat.unicode"),
    USE_COLOR_CHAT("octocore.chat.color"),
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
