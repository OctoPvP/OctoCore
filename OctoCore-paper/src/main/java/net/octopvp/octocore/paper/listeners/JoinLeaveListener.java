package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketEmoteBroadcast;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.manager.TabManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {
    /*
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreProcess(AsyncPlayerPreLoginEvent e){
        if(e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;
    }
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerLoginEvent e){
        //Logger.debug(DatabaseHelper.GET_PROFILE.getSql(e.getPlayer().getUniqueId().toString()));
        Bukkit.getScheduler().scheduleAsyncDelayedTask(OctoCorePaper.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerManager.processJoin(e.getPlayer().getUniqueId());
                TabManager.onJoin(e.getPlayer());
                //TODO remove
                LunarClientAPI.getInstance().giveAllStaffModules(e.getPlayer());
                //Post-Login modules
            }
        },20l);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e){
        PlayerManager.processLeave(e.getPlayer());
    }
    public static void init(){}
}