package net.octopvp.octocore.core.command;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.msg.Lang;

public enum CommandResult {
    SUCCESS(""),
    ERROR(CC.RED + "There was an error while processing that command!"),
    INVALID_ARGS(""),
    PLAYER_NOT_FOUND(CC.RED + "That player can't be found!"),
    INVALID_PLAYER(PLAYER_NOT_FOUND.getMsg()),
    ERROR_FETCHING_FROM_MOJANG(CC.RED + "Could not find that player from the Mojang API!"),
    OTHER(""),
    MOJANG_ERROR(CC.RED + "Could not contact the Mojang API! Please Try Again Later."),
    PLAYER_ONLY(Lang.PLAYER_ONLY.getMsg()),
    NO_PERMS(Lang.NO_PERMISSION.getMsg());
    private final String msg;

    CommandResult(String s) {
        this.msg = s;
    }

    public String getMsg(String... str) {
        return StringUtils.replacePlaceholders(msg, str);
    }
}
