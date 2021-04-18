package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.manager.ChatManager;
import net.octopvp.octocore.paper.manager.FilterManager;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.utils.msg.Chat;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void messageListener(AsyncPlayerChatEvent e){
        //spam prot
        if(PlayerManager.getProfile(e.getPlayer().getUniqueId()).getLastMessage() == e.getMessage()) {
            Chat.sendMessage(e.getPlayer(),Lang.PLEASE_DONT_SPAM);
            e.setCancelled(true);
        }
        if(FilterManager.containsUnicode(e.getMessage()) && !e.getPlayer().hasPermission(Permission.USE_UNICODE_CHAT.getNode())){
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.NOT_ALLOWED_TO_USE_UNICODE.getMsg());
        }
        PlayerManager.getProfile(e.getPlayer().getUniqueId()).setLastMessage(e.getMessage());
        if(e.getPlayer().hasPermission(Permission.USE_COLOR_CHAT.getNode()))
            e.setFormat(ChatManager.formatChat(e.getPlayer().getUniqueId(),e.getPlayer().getDisplayName(),FilterManager.process(e.getMessage(),e.getPlayer()),true));
        else e.setFormat(ChatManager.formatChat(e.getPlayer().getUniqueId(),e.getPlayer().getDisplayName(),FilterManager.process(e.getMessage(),e.getPlayer()),false));
    }
}
