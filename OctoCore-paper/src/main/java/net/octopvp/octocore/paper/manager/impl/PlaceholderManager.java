package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class PlaceholderManager extends Manager {
    private static HashMap<String, String> placeholders = new HashMap<>();

    public static String replacePlaceholders(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String replacePlaceholders(Lang message) {
        return ChatColor.translateAlternateColorCodes('&', message.getMsg());
    }

    @Override
    public void init(OctoCore plugin) {
        /*
        ResultSet rs;
        try {
            OctoCore.getConnection().prepareStatement(DatabaseHelper.CREATE_PLACEHOLDERS_TABLE.getSql());
            rs = OctoCore.getConnection().prepareStatement(DatabaseHelper.GET_PLACEHOLDERS.getSql()).executeQuery();
            while(rs.next()){
                placeholders.put(rs.getString("PLACEHOLDER"),rs.getString("REPLACE"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Logger.warn("Unable to load placeholders from database!");
        }
        Logger.debug("Placeholders: " );
        for (String s : placeholders.keySet()) {
            Logger.debug(" - " + s);
        }

         */
    }

    @Override
    public void disable() {

    }
}
