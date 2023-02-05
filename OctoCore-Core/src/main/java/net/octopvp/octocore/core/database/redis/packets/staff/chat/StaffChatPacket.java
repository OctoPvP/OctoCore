package net.octopvp.octocore.core.database.redis.packets.staff.chat;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.JDAManager;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaffChatPacket extends ChatPacket {
    public StaffChatPacket(String name, String coloredName, String server, String message, UUID uuid) {
        super(name, coloredName, server, message, uuid, System.currentTimeMillis());
    }
    public StaffChatPacket() {}
    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.STAFF_CHAT_FORMAT.getMsg(coloredName, server, message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.STAFFCHAT)) {
                player.sendMessage(msg);
            }
        }
        if (OctoCore.isMaster()) {
            JDAManager.sendDiscordSC(name, server, message);
        }
    }
}
