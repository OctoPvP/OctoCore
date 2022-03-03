package net.octopvp.octocore.paper.utils.msg;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;

import java.util.ArrayList;

public enum Lang {
    TEST("%1"),
    ERROR(CC.RED + "An error has occurred! Please try again later."),
    ERROR_LOGGED(CC.RED + "There was an error! Please open a bug report with this id:" + CC.GRAY + " %1"),
    NO_PERMISSION(CC.RED + "You have no permission!"),
    LOADING_DATA_FROM_DB(CC.D_GRAY + "Loading data from database, please wait..."),
    COULD_NOT_FIND_DATA(CC.RED + "Could not find that player's data! have they logged in before?"),
    HAVENT_PLAYED_BEFORE(CC.RED + "That player hasn't played before!"),
    PLAYER_ONLY(CC.RED + "This command is player only!"),
    PLAYER_NOT_FOUND(CC.RED + "%1 was not found! Please check your spelling!"),
    UNHANDLED_COMMAND(CC.RED + "This command is not handled! Please open a bug report!"),
    INVALID_TIME_INPUT(CC.RED + "Invalid time duration! Example: &67d 1mo 1y"),
    NICK_SUCCESS(CC.GREEN + "You successfully nicked as %1"),
    NICK_OTHER_SUCCESS(CC.GREEN + "You successfully nicked %1 as %2"),
    NOT_NICKED(CC.RED + "You are not nicked!"),
    OTHER_NOT_NICKED(CC.RED + "%1 is not nicked!"),
    NICK_RESET(CC.GREEN + "Your nick has been successfully reset!"),
    UNNICK_SUCCESS(CC.GREEN + "Successfully unnicked %1"),
    PLEASE_DONT_SPAM(CC.RED + "Please don't spam!"),
    NOT_ALLOWED_TO_USE_UNICODE(CC.RED + "You are not allowed to use unicode characters."),
    COMMAND_COOLDOWN(CC.RED + "You can't execute this command for another " + CC.GOLD + "%1 " + CC.RED + "seconds."),
    COMMAND_SEEN_NOT_ONLINE(CC.SEPARATOR + CC.NL + CC.NL + CC.AQUA + "%1 was last seen on" + CC.NL + CC.AQUA + "%2" + CC.NL + CC.NL + CC.SEPARATOR),
    EXECUTING_REQUESTED_COMMAND(CC.AQUA + "Executing command /%1 requested by &2"),
    STAFF_CHAT_FORMAT(CC.GOLD + CC.B + "StaffChat " + CC.D_GRAY + CC.ARROW_RIGHT + CC.GRAY + " %1 " + CC.GRAY + "(%2)" + CC.WHITE + ": %3"),
    ADMIN_CHAT_FORMAT(CC.RED + CC.B + "AdminChat " + CC.D_GRAY + CC.ARROW_RIGHT + CC.GRAY + " %1 " + CC.GRAY + "(%2)" + CC.WHITE + ": %3"),
    DISCORD_STAFF_CHAT_FORMAT(CC.GOLD + CC.B + "DiscordSC " + CC.D_GRAY + CC.ARROW_RIGHT + CC.GRAY + " %1 " + CC.WHITE + ": %2"),
    DISCORD_ADMIN_CHAT_FORMAT(CC.RED + CC.B + "DiscordAC " + CC.D_GRAY + CC.ARROW_RIGHT + CC.GRAY + " %1 " + CC.WHITE + ": %2"),
    ADMIN_ALERTS(CC.RED + CC.B + "Admin " + CC.D_GRAY + CC.ARROW_RIGHT + CC.WHITE + " %1"),
    STAFF_ALERTS(CC.GOLD + CC.B + "Staff " + CC.D_GRAY + CC.ARROW_RIGHT + CC.WHITE + " %1"),
    STAFF_JOIN_ALERT_FORMAT(CC.GREEN + "%1 joined the network to %2"),
    STAFF_LEAVE_ALERT_FORMAT(CC.RED + "%1 disconnected from %2"),
    STAFF_SWITCH_ALERT_FORMAT(CC.YELLOW + "%1 switched from %2 to %3"),
    STAFF_CHAT_DISABLED(STAFF_ALERTS.getMsg(CC.GOLD + "StaffChat " + CC.RED + "disabled")),
    ADMIN_CHAT_DISABLED(ADMIN_ALERTS.getMsg(CC.GOLD + "AdminChat " + CC.RED + "disabled")),
    STAFF_CHAT_ENABLED(STAFF_ALERTS.getMsg(CC.GOLD + "StaffChat " + CC.GREEN + "enabled")),
    ADMIN_CHAT_ENABLED(ADMIN_ALERTS.getMsg(CC.GOLD + "AdminChat " + CC.GREEN + "enabled")),
    ADMIN_ALERT_GLOBAL_EXECUTE(CC.AQUA + "Global command execute requested by %1 | Command: " + CC.GREEN + " %2"),
    SERVER_ONLINE_FORMAT(CC.GREEN + "Server %1 is now online."),
    SERVER_OFFLINE_FORMAT(CC.RED + "Server %1 is now offline."),
    AUDIT_WORLDEDIT_FORMAT(CC.AQUA + ""),
    PDATA_DID_NOT_LOAD(CC.RED + "Your playerdata did not load! Please try relogging!"),
    NO_RAFFLE_RUNNING(CC.RED + "There is no raffle running currently!"),
    RAFFLE_ALREADY_RUNNING(CC.RED + "There is already a raffle running!"),
    ALREADY_IN_RAFFLE(CC.RED + "You are already in this raffle!"),
    TOO_MANY_PLAYERS_LEFT_FOR_RAFFLE(CC.RED + "Unfortunately, too many players went offline for the raffle to continue. Sorry!"),
    WON_RAFFLE("%1 " + CC.GREEN + " has won the raffle! With a total of " + CC.GRAY + "%2" + CC.GREEN + " players!" + CC.R + CC.GOLD + CC.B + " GG!"),
    RAFFLE_STARTED(CC.GREEN + CC.B + "Raffle! do /raffle enter to enter!" + CC.NL + CC.GOLD + "Reward: %1" + CC.NL + CC.GOLD + "From: %2"),
    LIST_MESSAGE_HEADER(CC.SEPARATOR + CC.NL + CC.WHITE + "There are currently " + CC.GREEN + "%1" + CC.WHITE + " players online " + CC.GRAY + CC.U + "on this instance"),
    LIST_MESSAGE_BODY_ENTRY(CC.GRAY + " - %1"),
    PING_COMMAND_OTHER_PING(CC.GREEN + "%1's ping is: %2"),
    PING_COMMAND_RESPONSE(CC.GREEN + "Your ping is: %1"),
    LOGIN_AUTH_MESSAGE(CC.RED + "You are connecting from a new IP. Please do /2fa <your 2fa code>"),
    AUTH_DENIED(CC.RED + "Wrong auth code! You have " + CC.GOLD + "%1" + CC.RED + " tries left."),
    ALREADY_AUTHED(CC.GREEN + "You're already authenticated!"),
    AUTH_NOT_ENABLED(CC.RED + "You don't have 2fa enabled!"),
    AUTH_WAITING(CC.GREEN + "Please scan the QR code with Authy or GAuth. Click to put the key in chat instead.\n" + CC.GREEN + "Then, Please do /2fa <code>"),
    AUTH_SUCCESS(CC.GREEN + "Successfully Authenticated!"),
    AUTH_NO_NEED_JOIN_SAME_IP(CC.GREEN + "You joined on the same IP as your last authenticated ip so you don't need to authenticate!"),
    PLEASE_AUTH(CC.RED + "This account has 2fa enabled. Please do " + CC.GREEN + "/2fa <code>" + CC.RED + " or log out and ask for assistance from your staff manager."),
    AUTH_SETUP_SUCCESS(CC.GREEN + "2fa successfully setup!"),
    AUTH_SETUP_WAITING(AUTH_WAITING.msg + "\n" + CC.GREEN + "Type \"to cancel the setup process.\""),
    AUTH_SETUP_ABORTED(CC.RED + "Auth setup aborted."),
    AUTH_SETUP_INCORRECT(CC.RED + "That code was incorrect! Please try again!"),
    ARE_YOU_SURE(CC.RED + "Are you sure you want to %1"),
    CONFIRM_CONVERSATION_UNKNOWN_RESPONSE(CC.RED + "Unknown response! Please type in \"yes\" or \"no\""),
    KABOOM(CC.YELLOW + CC.B + "KABOOM!"),
    GAMEMODE(CC.GREEN + "Set your gamemode to: " + CC.AQUA + "%1"),
    BROADCAST_RESPONSE(CC.GREEN + "Successfully broadcast to " + CC.AQUA + "%1" + CC.GREEN + " players over " + CC.AQUA + "%2" + CC.GREEN + " servers."),
    FEATURE_NOT_IMPLEMENTED(CC.RED + "This feature is not implemented currently!"),
    TAG_GIVE_SUCCESS(CC.GREEN + "Successfully gave %1 tag %2."),
    TAG_REMOVE_SUCCESS(CC.GREEN + "Successfully removed tag %1 from %2."),
    GRANT_CANT_GRANT_DEFAULT(CC.translate("&cYou can't grant the default rank!")),
    GRANT_ALREADY_HAS_RANK(CC.translate("&c%1 already has the rank %2!")),
    GRANT_CANNOT_GRANT_HIGHER_RANK(CC.translate("&cYou can't grant a higher rank than your current rank!")),
    GRANT_NO_PERMISSION_TO_GRANT_RANK(CC.translate("&cYou don't have permission to grant that rank!")),
    GRANT_ENTER_DURATION(CC.translate("&aPlease enter the duration that this rank will last for. Eg: perm/permanent or 10d")),
    GRANT_ENTER_REASON(CC.translate("&aPlease enter a reason")),
    GRANT_DURATION_SET(CC.translate("&aDuration Set: &e&1")),
    GRANT_INVALID_TIME(CC.translate("&cInvalid time duration!")),
    GRANT_SERVER_SET(CC.translate("&aSet server to &b") + "%1"),
    GRANT_REASON_SET(CC.translate("&aSet reason to &b") + "%1"),
    GRANT_RANK_NOT_FOUND(CC.RED + "The rank %1 cannot be found!"),
    GRANT_PERM_GRANTED_TO(CC.GREEN + "You have been " + CC.AQUA + "permanently" + CC.GREEN + " granted %1 "),
    GRANT_TEMP_GRANTED_TO(CC.GREEN + "You have been " + CC.AQUA + "temporarily" + CC.GREEN + " granted %1 " + CC.GREEN + "for " + CC.YELLOW + "%2"),
    GRANT_PERM_GRANTED_EXECUTOR(CC.GREEN + "You have " + CC.AQUA + "permanently" + CC.GREEN + " granted %1 " + CC.GREEN + "to %2 for " + CC.YELLOW + "%3"),
    GRANT_TEMP_GRANTED_EXECUTOR(CC.GREEN + "You have " + CC.AQUA + "temporarily" + CC.GREEN + " granted %1 " + CC.GREEN + "to %2 for " + CC.YELLOW + "%3"),
    GRANT_ADMIN_ALERT_PERM(CC.AQUA + "%1 has permanently granted %2 %3" + CC.AQUA + " rank. Reason: %4"),
    GRANT_ADMIN_ALERT_TEMP(CC.AQUA + "%1 has temporarily granted %2 %3" + CC.AQUA + " rank for " + CC.YELLOW + "%4 " + CC.AQUA + ". Reason: %5"),
    GRANT_DATA_COULD_NOT_BE_LOADED(CC.RED + "Could not load the data for %1!"),
    CREATE_RANK_SET_WEIGHT(CC.GREEN + "Please type in the rank weight (integer). Type \"cancel\" to cancel."),
    CREATE_RANK_SET_NAME(CC.GREEN + "Please type in the rank name. Type \"cancel\" to cancel."),
    CREATE_RANK_SET_PREFIX(CC.GREEN + "Please type in the rank prefix, use & color codes, and %1 for changeable rank colors. Type in \"cancel\" to cancel."),
    CUSTOM_SERVER_SCOPE("Please enter your custom server scope in chat. Type \"Cancel\" to cancel."),
    EDIT_PERMISSION_PERMISSION_SET_SUCCESS(CC.GREEN + "Successfully set permission %1!"),
    EDIT_PERMISSION_SET_PERMISSION(CC.GREEN + "Please enter the permission in chat. Type \"Cancel\" to cancel."),
    EDIT_PERMISSION_SET_ALLOWED(CC.GREEN + "Set permission allowed to: %1"),
    WRONG_DATE_FORMAT("&bYou have entered wrong date format. &3Example &7(&b1d&7, &b1h&7, &b1m&7)&b."),

    ALREADY_BANNED("&cThis player is already banned!"),
    NOT_BANNED("&cThis player isn't banned!"),

    BLACKLIST_ALREADY_BLACKLISTED("&cThis player is already blacklisted!"),
    BLACKLIST_NOT_BLACKLISTED("&cThis player isn't blacklisted!"),

    MUTE_ALREADY_MUTED("&cThis user is already muted!"),
    MUTE_NOT_MUTED("&cThis user isn't muted!"),

    MUTE_BEEN_PERM_MUTED("&cYou have been &4permanently&r&c muted &cby &3%1 &bfor &3%4&b."),
    MUTE_BEEN_TEMP_MUTED("&cYou have been &4temporarily&r&c muted &cby &3%1 &bfor &3%3 &bfor &3%5&b."),

    MUTE_CANT_TALK_TEMP("&cYou are currently muted for another &e%1&b."),
    MUTE_CANT_TALK_PERM("&cYou are currently muted forever."),

    WARN_BEEN_PERM_WARNED("&bYou have been &epermanently warned &bby &3%1 &bfor &3%1&b."),
    WARN_BEEN_TEMP_WARNED("&bYou have been &etemporarily warned &bby &3%1 &bfor &3%1 &bfor &3%2&b."),

    JOIN_BANNED("&b%1 &3tried to join but is &bBANNED&b. &7(&b%2&7)"),
    JOIN_BLACKLISTED("&4%1 &ctried to join but is &4BLACKLISTED&c. &7(&c%2&7)"),

    PUNISHMENT_SILENT("&f[&7Silent&f] "),
    PUNISHMENT_UNDO("&c%1&c was un-%2 by %3&c for &7%4"),
    //PUNISHMENT_UNDO("$s%1$m was $sun%2 $mby $s%3 $mfor $v%4"),
    PUNISHMENT_DO("&c%1&c was &4%2&c by %3 for &7%4"),
    //PUNISHMENT_DO("$s%1&c was $s%2 &cby $s%3 &cfor $v%4"),

    //PUNISHMENT_UNBAN("$s%1$m was $sun-blacklisted $mby $s%2 $mfor $v%3"),
    //PUNISHMENT_UNMUTE("$s%1$m was $sunmuted $mby $s%2 $mfor $v%3"),
    //PUNISHMENT_UNBAN_HOVER("$mReason: $s%2"),
    //PUNISHMENT_UNBLACKLIST_HOVER("$mReason: $s%2"),
    //PUNISHMENT_UNMUTE_HOVER("$mReason: $s%2"),
    //PUNISHMENT_UNBLACKLIST("$s%1$m was $sunblacklisted $mby $s%2 $mfor $v%3"),

    MUTE_MESSAGE("&cYou have been &4%1&c muted for &e%2.%3"),
    TEMP_MUTE_ENTRY_MESSAGE("\n&cExpires: &e%1"),

    WARN_MESSAGE("&cYou have been &4%1&c warned for &e%2.%3"),
    TEMP_WARN_ENTRY_MESSAGE("\n&cExpires: &e%1"),

    STAFF_ROLLBACK_WIPING("&aWiping all &2%1&a. Please wait..."),
    STAFF_ROLLBACK_DONT_HAVE_HISTORY("&c%1 doesn't have any &2%2 &aperformed."),
    STAFF_ROLLBACK_WIPED("&mSuccessfully rollbacked %1. &7($s%2 %3s&7) ($s%4 Active&7,&s%5 Expired&7)"),
    PUNISH_INFO_FORMAT(
            CC.SEPARATOR +
                    "\n$m%1's $sHistory" +
                    "\n\n" +
                    "$1Bans&7: &3%2\n" +
                    "$1Mutes&7: &3%3\n" +
                    "$1Blacklists&7: &3%4\n" +
                    "$1Kicks&7: &3%5\n" +
                    "$1Warns&7: &3%6\n" +
                    "\n\n" +
                    "&3Please type &b\"/history warns\"&7, &3to see info on warns!\n" +
                    "&3Please type &b\"/history mutes\"&7, &3to see info on mutes!\n" +
                    CC.SEPARATOR
    ),
    PUNISH_INFO_WARNS_FORMT(
            CC.SEPARATOR,
            "$m%1's $sWarns", "", "%2", CC.SEPARATOR
    ),
    PUNISH_INFO_WARN_ENTRY(CC.GRAY + " " + CC.DOT),

    TOO_MANY_ALTS_KICK_MESSAGE(CC.RED + "You have exceeded the max$v(%1) &camount of alt accounts.\nYou currently have &v%2&c alts!"),
    TEMP("temporarily"),
    PERM("permanently"),
    PUNISH_KICK_TEMP_ENTRY("\n" + CC.RED + "Expires: %1\n" + CC.RED + "Duration: %2"),
    PUNISH_KICK_MESSAGE(
            CC.RED + "You are%1 &r&4&l%2\n\n&c%3 By: %4\n&cReason: %5%6"
    ),
    PERM_ENTRY("\n" + CC.RED + "Duration: permanent"),
    PUNISH_JOIN_ALERT(
            CC.RED + "%1 tried to join but is %2."
    ),
    PUNISH_JOIN_ALERT_HOVER(
            "$mExpires: $s%1",
                  "$mAdded By: $s%2",
                  CC.YELLOW + "Click to vew punishments!"
    )
    ;
    private final String msg;

    Lang(String msg) {
        this.msg = msg;
    }

    Lang(String... msg) {
        StringBuilder sb = new StringBuilder();
        for (String s : msg) {
            sb.append(s).append("\n");
        }
        this.msg = sb.toString();
    }

    public String getMsg(final String... placeholders) {
        return CC.translate(StringUtils.replacePlaceholders(msg, placeholders)
                .replace("$m", CC.MAIN)
                .replace("$s", CC.SECONDARY)
                .replace("$v", CC.VALUE));
    }

    public String getMsg(final Object... placeholders) {
        ArrayList<String> a = new ArrayList<>();
        for (Object placeholder : placeholders) {
            a.add(placeholder + "");
        }
        return CC.translate(StringUtils.replacePlaceholders(msg, a.toArray(new String[0])).replace("%main%", CC.MAIN)
                .replace("$m", CC.MAIN)
                .replace("$s", CC.SECONDARY)
                .replace("$v", CC.VALUE));
    }

    @Override
    public String toString() {
        return getMsg();
    }
}
