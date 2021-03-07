package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.paper.OctoCorePaper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class NickManager implements Manager{
    @Override
    public void init(OctoCorePaper plugin) {

    }
    public static boolean isNicked(UUID uuid){
        try {
            PreparedStatement ps = OctoCorePaper.getConnection().prepareStatement("SELECT * FROM ");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
