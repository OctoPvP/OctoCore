package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterManager extends Manager {
    private static final Pattern UNICODE_PATTERN = Pattern.compile("[^a-z0-9~!@#$%^&*()_+-={}\\[\\]|:\";'<>?,./\\\\    ]", Pattern.CASE_INSENSITIVE);
    private static final HashMap<String, Boolean> blacklist = new HashMap();

    public static HashMap<String, Boolean> getBlacklist() {
        return FilterManager.blacklist;
    }

    public static String process(String message1, Player player) {
        String message = message1;
        String final_message = message;
        for (String blacklist : FilterManager.getBlacklist().keySet()) {
            if (message.toLowerCase().contains(blacklist.toLowerCase())) {
                if (getBlacklist().get(blacklist))
                    alertMods(player, message);

                StringBuilder sb = new StringBuilder();
                char[] stars = blacklist.toCharArray();
                for (char star : stars) {
                    sb.append("*");
                }
                Logger.debug(sb.toString());
                final_message = final_message.replaceAll("(?i)" + blacklist, sb.toString());
            }
        }
        Logger.debug(final_message);
        return final_message;
    }

    public static void alertMods(Player player, String message) {
        player.sendMessage(CC.SEPARATOR);
        player.sendMessage(CC.RED + "Please do " + CC.UNDERLINE + "NOT" + CC.R + CC.RED + " use homophobic or racial slurs.\nModerators have been alerted, If you continue to use homophobic or racial slurs, you may get " + CC.UNDERLINE + "Muted Or Banned");
        player.sendMessage(CC.SEPARATOR);
    }

    public static boolean containsUnicode(String message) {
        Matcher m = UNICODE_PATTERN.matcher(message);
        return m.find();
    }

    @Override
    public void init(OctoCore plugin) {
        /*
        ResultSet rs = null;
        try {
            rs = OctoCore.getConnection().prepareStatement(DatabaseHelper.GET_BLAKLIST_WORDS.getSql()).executeQuery();
            while(rs.next()){
                blacklist.put(rs.getString("WORD"),(rs.getInt("REPORT") == 1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Logger.warn("Unable to load blacklisted words from database!");
        }
         */
    }

    @Override
    public void disable() {

    }
}
