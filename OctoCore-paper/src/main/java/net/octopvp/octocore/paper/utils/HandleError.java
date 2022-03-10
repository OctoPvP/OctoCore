package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorHandling;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.entity.Player;

public class HandleError {
    public static String handlePlayerError(ErrorData data, Player p, Exception e, boolean kick) {
        data.addException(e);
        data.addData("Player", p.getName());
        try {
            data.addData("PdataJSON", PlayerManager.serializeProfileToJson(PlayerManager.getProfile(p.getUniqueId())));
        } catch (Exception exception) {
            exception.printStackTrace();
            data.addDescription("Error adding pdata json. Exception has also been added.");
        }
        String[] a = ErrorHandling.handleError(data);
        p.sendMessage(Lang.ERROR_LOGGED.getMsg(a[0]));
        if (kick) {
            Tasks.run(() -> {
                p.kickPlayer(Lang.ERROR_LOGGED.getMsg(a[0]));
            });
        }
        return a[0];
    }

    public static String handlePlayerErrorNoPdata(ErrorData data, Player p, Exception e, boolean kick) {
        data.addException(e);
        data.addData("Player", p.getName());
        String[] a = ErrorHandling.handleError(data);
        p.sendMessage(Lang.ERROR_LOGGED.getMsg(a[0]));
        if (kick) {
            Tasks.run(() -> {
                p.kickPlayer(Lang.ERROR_LOGGED.getMsg(a[0]));
            });
        }
        return a[0];
    }
}
