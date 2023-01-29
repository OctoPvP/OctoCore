package net.octopvp.octocore.master.master.object;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPlayerData;
import net.octopvp.octocore.common.object.punish.BasePunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.master.master.OctoCoreMaster;
import net.octopvp.octocore.master.master.redis.impl.ExecutePunishmentPacket;
import net.octopvp.octocore.master.models.User;
import org.bson.Document;

import java.util.UUID;

public class MasterPunishment extends BasePunishment {
    public MasterPunishment(Document document) {
        super(document);
    }

    public MasterPunishment(IPlayerData data, PunishmentType type) {
        super(data, type);
    }

    public MasterPunishment(PunishmentType type, String name, UUID uuid) {
        super(type, name, uuid);
    }

    public void execute(User user) {
        String senderStr;
        if (user == null) {
            senderStr = "Master";
        } else {
            senderStr = user.getMinecraftName() + " (WEB)";
        }

        new ExecutePunishmentPacket(senderStr, "", user == null ? "Master" : user.getMinecraftName(), name, getReason(), getDurationTime(), getNiceDuration(), getNiceExpire(),
                isPermanent(), targetId, isSilent(), addedByName, OctoCoreCommon.getInstance().getServerImplementation().getServerName(), punishmentType,
                IPRelative, OctoCoreMaster.getGson().toJson(this)
        ).send();
    }
}
