package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.FilterManager;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.utils.msg.Chat;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.ChatColor;
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
            e.setFormat(PlayerManager.getProfile(e.getPlayer().getUniqueId()).getPrefix() + " " + e.getPlayer().getDisplayName() + CC.R + ": " + ChatColor.translateAlternateColorCodes('&',FilterManager.process(e.getMessage(),e.getPlayer())));
        else e.setFormat(ChatColor.translateAlternateColorCodes('&',PlayerManager.getProfile(e.getPlayer().getUniqueId()).getPrefix() + LuckpermsManager.getMainColor(e.getPlayer().getUniqueId()) + e.getPlayer().getDisplayName()) + CC.R + ": " + FilterManager.process(e.getMessage(),e.getPlayer()));
    }
}
