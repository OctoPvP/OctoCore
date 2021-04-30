package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TabManager;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED){
            PlayerManager.processJoin(e.getUniqueId());
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerLoginEvent e){
        //Logger.debug(DatabaseHelper.GET_PROFILE.getSql(e.getPlayer().getUniqueId().toString()));
        TabManager.onJoin(e.getPlayer());
        //TODO remove
        if(e.getPlayer().hasPermission(Permission.STAFF_MODULES.getNode()))
            LunarClientAPI.getInstance().giveAllStaffModules(e.getPlayer());
        //Post-Login modules
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e){
        PlayerManager.processLeave(e.getPlayer());
    }
    public static void init(){}
}