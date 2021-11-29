package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import net.octopvp.octocore.common.object.Settings;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseHelper;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.common.util.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsManager extends Manager {
    @Getter private static SettingsManager instance;
    @Getter private static Settings settings = OctoCore.getSettings();
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
    public void disable() {}

    public void reload(){
        settings.getSettingsMap().clear();
        ResultSet rs = null;
        try {
            OctoCore.getConnection().prepareStatement(DatabaseHelper.CREATE_DISABLED_COMMANDS_TABLE.getSql());
            rs = OctoCore.getConnection().prepareStatement(DatabaseHelper.GET_BLAKLIST_WORDS.getSql()).executeQuery();
            while (rs.next()){
                settings.set(rs.getString("key"),rs.getString("value"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Logger.debug("Settings:");
        settings.getSettingsMap().forEach((k,v)-> Logger.debug(" - " + k + " | " + v));
    }
}
