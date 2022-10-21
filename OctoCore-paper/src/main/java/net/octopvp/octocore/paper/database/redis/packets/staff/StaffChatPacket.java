package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.JDAManager;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaffChatPacket extends RedisPacket {
    private String name, server, message;
    private UUID uuid;

    public StaffChatPacket(String name, String server, String message, UUID uuid) {
        this.name = name;
        this.server = server;
        this.message = message;
        this.uuid = uuid;
    }

    public StaffChatPacket(String name, String server, String message) {
        this(name, server, message, null);
    }

    public StaffChatPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
       String msg = Lang.STAFF_CHAT_FORMAT.getMsg(name, server, message);
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
