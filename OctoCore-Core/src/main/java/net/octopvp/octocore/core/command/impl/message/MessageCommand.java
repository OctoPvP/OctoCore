package net.octopvp.octocore.core.command.impl.message;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.command.annotation.OnlineOnly;
import net.octopvp.octocore.core.database.redis.packets.player.MessagePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MessageCommand {
    @Command(name = "message", aliases = {"msg", "w", "m", "tell", "t"})
    @PlayerOnly
    @Cooldown(1)
    public CommandResult execute(@Sender Player sender, @Name("player") @OnlineOnly(network = true) PlayerData target, @JoinStrings @Name("message") String message) {
        PlayerData senderData = PlayerManager.getInstance().getData(sender.getUniqueId());
        if (target == null || (target.isVanished() && target.getVanishPriority() > senderData.getVanishPriority())) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        return execMsg(sender, message, target, senderData);
    }

    @Command(name = "reply", aliases = "r")
    @PlayerOnly
    @Cooldown(1)
    public CommandResult executeReply(@Sender Player sender, @JoinStrings @Name("message") String message) {
        if (message.isEmpty()) {
            return CommandResult.INVALID_ARGS;
        }
        PlayerData data = PlayerManager.getInstance().getData(sender.getUniqueId());
        if (data == null) {
            return CommandResult.ERROR;
        }
        if (data.getLastMessaged() == null) {
            sender.sendMessage(Lang.NO_LAST_MSG.toString());
            return CommandResult.SUCCESS;
        }
        UUID targetId = data.getLastMessaged();
        OnlinePlayer target = OctoCoreCommon.getInstance().getServerManager().getOnlinePlayer(targetId);
        if (target == null || (target.isVanished() && target.getVanishPriority() > data.getVanishPriority())) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        return execMsg(sender, message, PlayerManager.getInstance().getDataEvenIfOffline(targetId, false), PlayerManager.getInstance().getData(sender.getUniqueId()));
    }

    @NotNull
    private CommandResult execMsg(Player sender, String message, PlayerData target, PlayerData senderPlayer) {
        boolean ignoreBypass = sender.hasPermission(Permissions.IGNORE_BYPASS);
        if (senderPlayer.getUuid().equals(target.getUuid())) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SELF.toString());
            return CommandResult.SUCCESS;
        }
        if (target.getMessageSettings().isIgnoring(senderPlayer.getUniqueId()) && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_IGNORED.toString());
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.getMessageSettings().isIgnoring(target.getUniqueId()) && !ignoreBypass) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SENDER_IGNORED.toString());
            return CommandResult.SUCCESS;
        }
        if (target.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_DISABLED.toString());
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.SELF_MSG_DISABLED.toString());
            return CommandResult.SUCCESS;
        }
        new MessagePacket(message, senderPlayer.getFormattedName(false, sender, true), target.getFormattedName(false, Bukkit.getPlayer(target.getUniqueId()), true), senderPlayer.getUuid(), target.getUuid()).send();
        return CommandResult.SUCCESS;
    }
}
