package net.octopvp.octocore.paper.command;

import lombok.Getter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.PermissionString;

@Getter
public enum CommandResult implements PermissionString {
    SUCCESS(""),
    ERROR(CC.RED + "There was an error while processing that command!"),
    INVALID_ARGS(CC.RED + "Invalid Arguments!"),
    PLAYER_NOT_FOUND(CC.RED + "That player can't be found!"),
    INVALID_PLAYER(PLAYER_NOT_FOUND.getMsg()),
    OTHER(""),
    NO_PERMS(Lang.NO_PERMISSION.getMsg());
    private String msg;
    CommandResult(String s){
        this.msg = s;
    }

    @Override
    public String getNode() {
        return msg;
    }
}
