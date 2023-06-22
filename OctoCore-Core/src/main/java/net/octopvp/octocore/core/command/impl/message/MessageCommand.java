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

    @Command(name = "ignore", description = "Ignore a player", usage = "<add/remove/list>")
    @PlayerOnly
    public void executeIgnore(@Sender Player player, String[] args, CommandInfo ci) {
        if (args.length < 1) {
            throw new InvalidArgsException(ci);
        }
        PlayerData data = PlayerManager.getInstance().getData(player);
        if (data == null) {
            throw new MessageException(Lang.ERROR.toString());
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (data.getMessageSettings().getIgnoreList().size() >= MessageSettings.MAX_IGNORE_SIZE) {
                player.sendMessage(Lang.IGNORE_LIST_FULL.toString());
                return;
            }
            if (args.length < 2) {
                throw new InvalidArgsException(ci);
            }
            if (args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(Lang.CANNOT_IGNORE_SELF.toString());
                return;
            }
            Tasks.runAsync(() -> {
                // OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                OfflineHelpers.OfflineInfo target = OfflineHelpers.getOfflineInfo(args[1]);
                if (PlayerManager.getInstance().doesDocumentExistByName(target.getName())) {
                    data.getMessageSettings().getIgnoreList().put(target.getUuid(), target.getDisplayName());
                    player.sendMessage(Lang.SUCCESS_IGNORE.getMsg(target.getDisplayName()));
                } else {
                    player.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
                }
            });
            return;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            Tasks.runAsync(() -> {
                OfflineHelpers.OfflineInfo target = OfflineHelpers.getOfflineInfo(args[1]);
                if (!data.getMessageSettings().isIgnoring(target.getUuid())) {
                    player.sendMessage(Lang.NOT_IGNORED.toString());
                    return;
                }
                // data.getMessageSettings().getIgnoreList().removeIf(name -> name.equalsIgnoreCase(args[1]));
                data.getMessageSettings().getIgnoreList().remove(target.getUuid());
                player.sendMessage(Lang.SUCCESS_UNIGNORE.getMsg(args[1]));
                return;
            });
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (data.getMessageSettings().getIgnoreList().size() > 0) {
                player.sendMessage(CC.SEPARATOR);
                player.sendMessage(Lang.IGNORE_LIST_HEADER.getMsg());
                String[] list = data.getMessageSettings().getIgnoreList().values().toArray(new String[0]);
                Arrays.sort(list);
                for (String s : list) {
                    // TextComponent component = new Clickable()
                    //        .add("&7 - &e" + s, CC.YELLOW + "Click to un-ignore this player!", "/ignore remove " + s);
                    Component component = Component.text(" - ", NamedTextColor.GRAY)
                            .append(Component.text(s, NamedTextColor.YELLOW)
                                    .clickEvent(ClickEvent.runCommand("/ignore remove " + s))
                                    .hoverEvent(HoverEvent.showText(Component.text("Click to un-ignore this player!", NamedTextColor.YELLOW))));
                    player.sendMessage(component);
                }
                player.sendMessage(CC.SEPARATOR);
            } else {
                player.sendMessage(Lang.NOT_IGNORING_ANYONE.toString());
            }
            return;
        }
        throw new InvalidArgsException(ci);
    }
}
