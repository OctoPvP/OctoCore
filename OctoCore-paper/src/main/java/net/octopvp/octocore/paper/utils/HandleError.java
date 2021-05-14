package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorHandling;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HandleError {
    public static void handlePlayerError(ErrorData data, Player p,Exception e,boolean kick){
        data.addException(e);
        data.addData("Player",p.getName());
        try{
            data.addData("PdataJSON", PlayerManager.serializeProfileToJson(PlayerManager.getProfile(p.getUniqueId())));
        } catch (Exception exception) {
            exception.printStackTrace();
            data.addData("","Error adding pdata json. Exception has also been added.");
        }
        String[] a = ErrorHandling.handleError(data);
        p.sendMessage(Lang.ERROR_LOGGED.getMsg(a[0]));
        if(kick){
            p.kickPlayer(Lang.ERROR_LOGGED.getMsg(a[0]));
        }
    }
    public static void handlePlayerErrorNoPdata(ErrorData data, Player p,Exception e,boolean kick){
        data.addException(e);
        data.addData("Player",p.getName());
        String[] a = ErrorHandling.handleError(data);
        p.sendMessage(Lang.ERROR_LOGGED.getMsg(a[0]));
        if(kick){
            p.kickPlayer(ChatColor.stripColor(Lang.ERROR_LOGGED.getMsg(a[0])));
        }
    }
}
