package net.octopvp.octocore.core.command.impl.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.exception.MessageException;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.MessageSettings;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.database.redis.packets.player.MessagePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.UUID;

public class MessageCommand {
    @Command(name = "message", aliases = {"msg", "w", "m", "tell", "t"})
    @PlayerOnly
    public CommandResult execute(@Sender Player sender, @Name("player") GlobalPlayer target, @JoinStrings String message) {
        GlobalPlayer senderPlayer = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(sender.getUniqueId());

        if (target == null) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        return execMsg(sender, message, target, senderPlayer);
    }

    @Command(name = "reply", aliases = "r")
    @PlayerOnly
    public CommandResult executeReply(@Sender Player sender, @JoinStrings String message) {
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
        GlobalPlayer target = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(targetId);
        if (target == null) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        GlobalPlayer senderPlayer = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(sender.getUniqueId());
        return execMsg(sender, message, target, senderPlayer);
    }

    @NotNull
    private CommandResult execMsg(@Sender Player sender, @JoinStrings String message, GlobalPlayer target, GlobalPlayer senderPlayer) {
        boolean ignoreBypass = sender.hasPermission(Permissions.IGNORE_BYPASS);
        if (senderPlayer.getUuid().equals(target.getUuid())) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SELF.toString());
            return CommandResult.SUCCESS;
        }
        if (target.isIgnoring(senderPlayer.getUniqueId()) && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_IGNORED.toString());
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.isIgnoring(target.getUniqueId()) && !ignoreBypass) {
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
        new MessagePacket(message, senderPlayer.getColoredName(), target.getColoredName(), senderPlayer.getUuid(), target.getUuid()).send();
        return CommandResult.SUCCESS;
    }
}
