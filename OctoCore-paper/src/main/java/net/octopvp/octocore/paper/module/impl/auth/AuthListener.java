package net.octopvp.octocore.paper.module.impl.auth;

import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.MapInitializeEvent;

public class AuthListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if(AuthModule.isAuthed(event.getPlayer()))
            return;
        else event.setCancelled(true);
    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        if(AuthModule.isAuthed(event.getPlayer())) {
            if(event.getMessage().toLowerCase().startsWith("/stop")){
                //TODO auth again
                return;
            }
            return;
        }
        if(event.getMessage().toLowerCase().startsWith("/2fa")){
        }else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Lang.PLEASE_AUTH.toString());
        }
    }
    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event){
        if(AuthModule.isAuthed(event.getPlayer()))
            return;
        else {
            event.getPlayer().sendMessage(Lang.PLEASE_AUTH.getMsg());
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onJoin(PlayerLoginEvent event){
        if(AuthModule.getTriesLeft().containsKey(event.getPlayer().getUniqueId())){
            AuthModule.handleJoin(event.getPlayer());
        }
        else{
            AuthModule.getTriesLeft().put(event.getPlayer().getUniqueId(),5);
            AuthModule.handleJoin(event.getPlayer());
        }
    }
    @EventHandler
    public void onMap(MapInitializeEvent event){

    }
}
