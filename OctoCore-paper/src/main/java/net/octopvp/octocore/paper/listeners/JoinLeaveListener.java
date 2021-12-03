package net.octopvp.octocore.paper.listeners;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import jdk.jfr.internal.tool.Main;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.listeners.redis.MainRedisHandler;
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
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class JoinLeaveListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED){
            PlayerManager.loadPData(e.getUniqueId(),e.getName(),true);
            if (PlayerManager.getData(e.getUniqueId()) == null)
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e){
        PlayerManager.processLeave(e.getPlayer());
        TabManager.onLeave(e.getPlayer());
        MainRedisHandler.getSaving().remove(e.getPlayer().getUniqueId());
        unfreezePlayer(e.getPlayer());
    }
    @EventHandler
    public void onGlobalPDestroyEvent(GlobalPlayerDestroyEvent e){}
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event){
        PlayerData data = PlayerManager.getData(event.getPlayer());
        if (data == null)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, new DisconnectReason("An error occurred while loading your data.\nPlease contact an administrator if this keeps happening!.").toString());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event){ //TODO join vanished
        if (event.getPlayer() == null || !event.getPlayer().isOnline()){
            return;
        }
        PlayerManager.processJoin(event.getPlayer().getUniqueId(),event.getPlayer().getAddress().getHostString());
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (MainRedisHandler.getSaving().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (MainRedisHandler.getSaving().contains(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    public static void freezePlayer(Player player) {
        Tasks.runSync(()->{
            player.setWalkSpeed(0.0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,10000,128,true,false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 128,true,false));
        });
    }

    public static void unfreezePlayer(Player player) {
        Tasks.runSync(()->{
            player.setWalkSpeed(0.2F);
            player.removePotionEffect(PotionEffectType.JUMP);
            player.removePotionEffect(PotionEffectType.SPEED);
        });
    }

    public static void init(){}
}