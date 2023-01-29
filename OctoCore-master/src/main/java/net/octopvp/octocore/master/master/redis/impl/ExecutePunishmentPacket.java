package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class ExecutePunishmentPacket extends RedisPacket {
    private String sender;
    //private JsonBuilder data;
    private String coloredName;
    private String senderName;
    private String name;
    private String reason;
    private long duration;
    private String niceDuration;
    private String niceExpire;
    private boolean permanent;
    private UUID uuid;
    private boolean silent;
    private String addedByName;
    private String server;
    private PunishmentType type;
    private boolean IPRelative;
    private String punishment;

    @Override
    public void onReceive(JsonObject data) {
        System.out.println("Received ExecutePunishmentPacket");
    }
}
