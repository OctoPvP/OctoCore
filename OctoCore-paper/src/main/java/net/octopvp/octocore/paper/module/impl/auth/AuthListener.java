package net.octopvp.octocore.paper.module.impl.auth;

import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class AuthListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event){
        if(AuthModule.isAuthed(event.getPlayer()))
            return;
        else event.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(AuthModule.isAuthed(event.getPlayer()))
            return;
        if(event.getMessage().toLowerCase().startsWith("/2fa")){
        }else {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(AsyncPlayerChatEvent event){
        if(AuthModule.isAuthed(event.getPlayer()))
            return;
        else {
            event.getPlayer().sendMessage(Lang.PLEASE_AUTH.getMsg());
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerLoginEvent event){
        if(AuthModule.getTriesLeft().get(event.getPlayer().getUniqueId()) == null && !AuthModule.isAuthed(event.getPlayer())){
            AuthModule.getTriesLeft().put(event.getPlayer().getUniqueId(),5);
            AuthModule.handleJoin(event.getPlayer());
        }
    }
}
