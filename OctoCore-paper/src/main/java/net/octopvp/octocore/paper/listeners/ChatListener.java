package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.ChatManager;
import net.octopvp.octocore.paper.manager.impl.FilterManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Chat;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void messageListener(AsyncPlayerChatEvent e) {
        PlayerData playerData = PlayerManager.getProfile(e.getPlayer().getUniqueId());

        if(playerData.isStaffChat()){
            if(playerData.isAdminChat()){
                playerData.setAdminChat(false);
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.toString());
            }
            if(e.getPlayer().hasPermission(Permission.STAFFCHAT.getNode())){
                PlayerManager.sendStaffChat(e.getPlayer(),e.getMessage(), OctoCore.getServerName());
                e.setCancelled(true);
                return;
            }else{
                e.getPlayer().sendMessage(Lang.STAFF_CHAT_DISABLED.getMsg());
                playerData.setStaffChat(false);
                return;
            }
        }
        else if(playerData.isAdminChat()){
            if(playerData.isStaffChat()){
                playerData.setStaffChat(false);
                e.getPlayer().sendMessage(Lang.STAFF_CHAT_DISABLED.toString());
            }
            if(e.getPlayer().hasPermission(Permission.ADMINCHAT.getNode())){
                PlayerManager.sendAdminChat(e.getPlayer(),e.getMessage(), OctoCore.getServerName());
                e.setCancelled(true);
                return;
            }else{
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.getMsg());
                playerData.setAdminChat(false);
                return;
            }
        }

        //spam prot
        if (playerData.getLastMessage() == e.getMessage()) {
            if (!e.getPlayer().hasPermission(Permission.BYPASS_SPAM_PROT.getNode())){
                Chat.sendMessage(e.getPlayer(), Lang.PLEASE_DONT_SPAM);
                e.setCancelled(true);
            }
            return;
        }
        if (FilterManager.containsUnicode(e.getMessage()) && !e.getPlayer().hasPermission(Permission.USE_UNICODE_CHAT.getNode())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.NOT_ALLOWED_TO_USE_UNICODE.getMsg());
            return;
        }
        playerData.setLastMessage(e.getMessage());

        String format = ChatManager.formatChat(e.getPlayer().getUniqueId(),e.getPlayer().getDisplayName(),FilterManager.process(e.getMessage(),e.getPlayer()),e.getPlayer().hasPermission(Permission.USE_COLOR_CHAT.getNode()));
        if(format == null){
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.PDATA_DID_NOT_LOAD.getMsg());
        }
        e.setFormat(format);
    }
}
