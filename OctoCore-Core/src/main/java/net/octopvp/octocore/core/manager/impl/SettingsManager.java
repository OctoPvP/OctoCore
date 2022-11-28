package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import net.octopvp.octocore.common.object.Settings;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;

public class SettingsManager extends Manager {
    @Getter
    private static final Settings settings = OctoCore.getSettings();
    @Getter
    private static SettingsManager instance;

    @Override
    public void init(OctoCore plugin) {
        /*
        this.instance = this;
        ResultSet rs;
        try {
            OctoCore.getConnection().prepareStatement(DatabaseHelper.CREATE_DISABLED_COMMANDS_TABLE.getSql());
            rs = OctoCore.getConnection().prepareStatement(DatabaseHelper.GET_SETTINGS.getSql()).executeQuery();
            while (rs.next()){
                settings.set(rs.getString("key"),rs.getString("value"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Logger.debug("Settings:");
        settings.getSettingsMap().forEach((k,v)-> Logger.debug(" - " + k + " | " + v));

         */
    }

    @Override
    public void disable() {
    }

    public void reload() {

    }
}
