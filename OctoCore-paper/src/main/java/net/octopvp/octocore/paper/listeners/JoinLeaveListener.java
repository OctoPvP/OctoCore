package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.LCPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.manager.TabManager;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.database.DatabaseHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

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
        Logger.debug(DatabaseHelper.GET_PROFILE.getSql(e.getPlayer().getUniqueId().toString()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(OctoCorePaper.getInstance(), new Runnable() {
            @Override
            public void run() {
                PlayerManager.processJoin(e.getPlayer().getUniqueId());
                TabManager.onJoin(e.getPlayer());
                LunarClientAPI.getInstance().giveAllStaffModules(e.getPlayer());
                //LunarClientAPI.getInstance().sendPacket(e.getPlayer(), new LCPacket().)
            }
        },20l);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e){

        //FIXME
        e.getPlayer().setLevel(0);
        e.getPlayer().setExp(0f);

        PlayerManager.processLeave(e.getPlayer());
    }
    public static void init(){}
}