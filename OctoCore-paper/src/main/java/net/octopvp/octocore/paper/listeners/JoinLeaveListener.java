package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.ScoreBoardManager;
import net.octopvp.octocore.paper.manager.impl.TabManager;
import net.octopvp.octocore.paper.manager.impl.autoinit.BookManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED){
            //FIXME
            //PlayerManager.loadPData(e.getUniqueId());
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e){
        PlayerManager.processLeave(e.getPlayer());
        TabManager.onLeave(e.getPlayer());
    }
    @EventHandler
    public void onGlobalPDestroyEvent(GlobalPlayerDestroyEvent e){}
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){ //TODO join vanished
        PlayerManager.loadPData(event.getPlayer().getUniqueId(),event.getPlayer().getName());
        PlayerManager.processJoin(event.getPlayer().getUniqueId(),event.getPlayer().getAddress().getHostString());
        Tasks.runAsyncLater(()->{
            if (event.getPlayer() == null || !event.getPlayer().isOnline()){
                return;
            }
            PlayerData profile = PlayerManager.getProfile(event.getPlayer().getUniqueId());
            Player player = event.getPlayer();
            long time = profile.getWorldTime().getTime();
            if (time == -1)
                player.resetPlayerTime();
            else player.setPlayerTime(time, false);
            TabManager.onJoin(player);
            if(player.hasPermission(Permission.STAFF_MODULES.getNode()))
                LunarClientAPI.getInstance().giveAllStaffModules(player);
            Tasks.run(()-> {
                ScoreBoardManager.handleJoin(event.getPlayer());
                if (OctoCore.getServerType() == ServerType.HUB || OctoCore.getServerType() == ServerType.MASTER){
                    ViaAPI viaAPI = Via.getAPI();
                    int version = viaAPI.getPlayerVersion(event.getPlayer());
                    Logger.debug("Player Version: " + version);
                    if (version != 47 && version != -1){
                        BookManager.showUnsupportedVerBook(event.getPlayer());
                    }
                }

            });
        },20l);
    }
    public static void init(){}
}