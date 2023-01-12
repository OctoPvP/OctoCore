package net.octopvp.octocore.core.module.impl.punishments.util;


import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.IPlayerData;
import net.octopvp.octocore.common.object.punish.BasePunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.player.ExecutePunishmentPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
@Setter
public class Punishment extends BasePunishment {
    public Punishment(Document document) {
        super(document);
    }

    public Punishment(IPlayerData data, PunishmentType type) {
        super(data, type);
    }

    public Punishment(PunishmentType type, String name, UUID uuid) {
        super(type, name, uuid);
    }

    public void execute(CommandSender sender) {
        String senderStr, coloredName;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            senderStr = player.getDisplayName();
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            coloredName = playerData.getHighestRank().getColor() + playerData.getName();
        } else {
            senderStr = "Console";
            coloredName = null;
        }

        new ExecutePunishmentPacket(senderStr, coloredName, sender.getName(), name, getReason(), getDurationTime(), getNiceDuration(), getNiceExpire(),
                isPermanent(), targetId, isSilent(), addedByName, OctoCoreCommon.getInstance().getServerImplementation().getServerName(), punishmentType,
                IPRelative, OctoCore.getGson().toJson(this)
        ).send();
    }

}
