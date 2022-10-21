package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PunishedJoinPacket extends RedisPacket {
    private String type, name, expire, addedByName;
    private boolean more;

    public PunishedJoinPacket(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public PunishedJoinPacket(String type, String name, String expire, String addedByName) {
        this.type = type;
        this.name = name;
        this.more = true;
        this.expire = expire;
        this.addedByName = addedByName;
    }

    public PunishedJoinPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        Clickable clickable;
        if (more) {
            clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type), Lang.PUNISH_JOIN_ALERT_HOVER.getMsg(expire, addedByName), "/history " + name);
        } else clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.PUNISHMENT_SEE_JOIN_ALERT)) {
                clickable.sendToPlayer(player);
            }
        }
    }
}
