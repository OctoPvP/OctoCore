package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.master.master.redis.LightningRedisPacket;

@NoArgsConstructor
@AllArgsConstructor
public class UndoPunishmentPacket extends LightningRedisPacket {

    private PunishmentType type;
    //private String senderDisplay;
    private String coloredName;
    //private String sender;
    private String target, reason;
    private boolean silent;

    @Override
    public void receive(JsonObject data) {

    }
}
