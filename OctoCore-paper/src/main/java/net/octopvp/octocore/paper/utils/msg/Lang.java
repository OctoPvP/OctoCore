package net.octopvp.octocore.paper.utils.msg;

import lombok.Getter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.Logger;

@Getter
public enum Lang {
    NO_PERMISSION(CC.RED + "You have no permission!"),
    PLAYER_ONLY(CC.RED + "This command is player only!"),
    UNHANDLED_COMMAND(CC.RED + "This command is not handled! Please open a bug report!"),
    NICK_SUCCESS(CC.GREEN + "You successfully nicked as %1"),
    NICK_OTHER_SUCCESS(CC.GREEN + "You successfully nicked %1 as %2"),
    NOT_NICKED(CC.RED + "You are not nicked!"),
    OTHER_NOT_NICKED(CC.RED + "%1 is not nicked!"),
    NICK_RESET(CC.GREEN + "Your nick has been successfully reset!"),
    UNNICK_SUCCESS(CC.GREEN + "Successfully unnicked %1"),
    PLEASE_DONT_SPAM(CC.RED + "Please don't spam!"),
    NOT_ALLOWED_TO_USE_UNICODE(CC.RED + "You are not allowed to use unicode characters.")
    ;
    private String msg;
    Lang(String msg){
        this.msg = msg;
    }
    public String getMsg(final String... values){
        String returned = msg;
        String finalreturn = returned;
        int i = 0;
        for (String value : values) {
            i++;
            Logger.debug(i + " | " + value);
            finalreturn.replace("%" + i,value + "");
            Logger.debug(finalreturn);
        }
        final String a = finalreturn;
        return a;
    }
}
