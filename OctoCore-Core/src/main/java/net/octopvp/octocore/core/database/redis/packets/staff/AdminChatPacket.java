package net.octopvp.octocore.core.database.redis.packets.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.JDAManager;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdminChatPacket extends RedisPacket {
    private String name, server, message;
    private UUID uuid;

    public AdminChatPacket(String name, String server, String message, UUID uuid) {
        this.name = name;
        this.server = server;
        this.message = message;
        this.uuid = uuid;
    }

    public AdminChatPacket(String name, String server, String message) {
        this(name, server, message, null);
    }

    public AdminChatPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.ADMIN_CHAT_FORMAT.getMsg(name, server, message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.ADMINCHAT)) {
                player.sendMessage(msg);
            }
        }
        if (OctoCore.isMaster()) {
            JDAManager.sendDiscordAC(name, server, message);
        }
    }
}
