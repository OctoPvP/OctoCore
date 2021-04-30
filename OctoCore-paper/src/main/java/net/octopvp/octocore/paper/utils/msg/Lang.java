package net.octopvp.octocore.paper.utils.msg;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.Logger;

public enum Lang {
    TEST("%1"),
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
    NOT_ALLOWED_TO_USE_UNICODE(CC.RED + "You are not allowed to use unicode characters."),
    COMMAND_COOLDOWN(CC.RED + "You can't execute this command for another " + CC.GOLD + "%1 " + CC.RED + "seconds."),
    COMMAND_SEEN_NOT_ONLINE(CC.SEPARATOR + CC.NL + CC.NL +  CC.AQUA + "%1 was last seen on" + CC.NL + CC.AQUA + "%2" + CC.NL + CC.NL + CC.SEPARATOR),
    EXECUTING_REQUESTED_COMMAND(CC.AQUA + "Executing command /%1 requested by &2"),
    STAFF_CHAT_FORMAT(CC.GOLD + CC.B + "StaffChat " + CC.D_GRAY + CC.ARROW + CC.GRAY + " %1 " + CC.GRAY + "(%2)" + CC.WHITE + ": %3"),
    //StaffChat » [Owner] Badbird5907 (hub1): asdf
    ADMIN_CHAT_FORMAT(CC.RED + CC.B + "AdminChat " + CC.D_GRAY + CC.ARROW + CC.GRAY + " %1 " + CC.GRAY + "(%2)" + CC.WHITE + ": %3"),
    ADMIN_ALERTS(CC.RED + CC.B + "Admin " + CC.GRAY + CC.ARROW + CC.WHITE + " %1"),
    STAFF_ALERTS(CC.GOLD + CC.B + "Staff " + CC.GRAY + CC.ARROW + CC.WHITE + " %1"),
    ERROR(CC.RED + "There was an error! Please open a bug report with this id:" + CC.GRAY + " %1"),
    PDATA_NOT_LOADING(CC.RED + "Your playerdata hasn't loaded yet (if you recently joined)! " +
            "this shouldn't be happening! try relogging! if the problem persists, please open a bug report!"),
    NO_RAFFLE_RUNNING(CC.RED + "There is no raffle running currently!"),
    RAFFLE_ALREADY_RUNNING(CC.RED + "There is already a raffle running!"),
    ALREADY_IN_RAFFLE(CC.RED + "You are already in this raffle!"),
    TOO_MANY_PLAYERS_LEFT_FOR_RAFFLE(CC.RED + "Unfortunately, too many players went offline for the raffle to continue. Sorry!"),
    WON_RAFFLE("%1 "+ CC.GREEN + " has won the raffle! With a total of " +
            "" + CC.GRAY + "%2" + CC.GREEN + " players!" + CC.R + CC.GOLD + CC.B + " GG!"),
    RAFFLE_STARTED(CC.GREEN + CC.B + "Raffle! do /raffle enter to enter!" + CC.NL + CC.GOLD + "Reward: %1"+ CC.NL + CC.GOLD + "From: %2")

    ;
    private final String msg;
    Lang(String msg){
        this.msg = msg;
    }
    public String getMsg(final String... placeholders){
        return StringUtils.replacePlaceholders(msg,placeholders);
    }
}
