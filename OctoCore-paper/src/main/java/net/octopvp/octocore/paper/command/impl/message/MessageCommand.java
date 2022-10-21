package net.octopvp.octocore.paper.command.impl.message;

import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Name;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.player.MessagePacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageCommand {
    @Command(name = "message", aliases = {"msg", "w", "m", "tell", "t"})
    @PlayerOnly
    public CommandResult execute(Sender sender, @Name("player") GlobalPlayer target, @JoinStrings String message) {
        GlobalPlayer senderPlayer = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(sender.getUUID());

        if (target == null) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        boolean ignoreBypass = sender.hasPermission(Permissions.IGNORE_BYPASS);

        if (senderPlayer.getUuid().equals(target.getUuid())) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SELF);
            return CommandResult.SUCCESS;
        }
        if (target.isIgnoring(senderPlayer.getName()) && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_IGNORED);
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.isIgnoring(target.getName()) && !ignoreBypass) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SENDER_IGNORED);
            return CommandResult.SUCCESS;
        }
        if (target.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_DISABLED);
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.SELF_MSG_DISABLED);
            return CommandResult.SUCCESS;
        }

        new MessagePacket(message, senderPlayer.getColoredName(), target.getColoredName(), senderPlayer.getUuid(), target.getUuid()).send();

        return CommandResult.SUCCESS;
    }

    @Command(name = "reply", aliases = "r")
    @PlayerOnly
    public CommandResult executeReply(Sender sender, String[] args) {
        if (args.length < 1) {
            return CommandResult.INVALID_ARGS;
        }
        PlayerData data = PlayerManager.getInstance().getData(sender.getUUID());
        if (data == null) {
            return CommandResult.ERROR;
        }
        if (data.getLastMessaged() == null) {
            sender.sendMessage(Lang.NO_LAST_MSG);
            return CommandResult.SUCCESS;
        }
        UUID targetId = data.getLastMessaged();
        GlobalPlayer target = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(targetId);
        if (target == null) {
            return CommandResult.PLAYER_NOT_FOUND;
        }
        GlobalPlayer senderPlayer = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(sender.getUUID());
        boolean ignoreBypass = sender.hasPermission(Permissions.IGNORE_BYPASS);
        if (senderPlayer.getUuid().equals(target.getUuid())) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SELF);
            return CommandResult.SUCCESS;
        }
        if (target.isIgnoring(senderPlayer.getName()) && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_IGNORED);
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.isIgnoring(target.getName()) && !ignoreBypass) {
            sender.sendMessage(Lang.CANNOT_MESSAGE_SENDER_IGNORED);
            return CommandResult.SUCCESS;
        }
        if (target.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.MSG_DISABLED);
            return CommandResult.SUCCESS;
        }
        if (senderPlayer.getMessageSettings().isMessagesOff() && !ignoreBypass) {
            sender.sendMessage(Lang.SELF_MSG_DISABLED);
            return CommandResult.SUCCESS;
        }
        String message = String.join(" ", args);
        new MessagePacket(message, senderPlayer.getColoredName(), target.getColoredName(), senderPlayer.getUuid(), target.getUuid()).send();
        return CommandResult.SUCCESS;
    }

    @Command(name = "ignore", description = "Ignore a player", usage = "<add/remove/list>")
    @PlayerOnly
    public CommandResult executeIgnore(Sender sender, String[] args) {
        if (args.length < 1) {
            return CommandResult.INVALID_ARGS;
        }
        PlayerData data = PlayerManager.getInstance().getData(sender.getUUID());
        Player player = sender.getPlayer();
        if (data == null) {
            return CommandResult.ERROR;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                return CommandResult.INVALID_ARGS;
            }
            if (args[1].equalsIgnoreCase(player.getName())) {
                sender.sendMessage(Lang.CANNOT_IGNORE_SELF);
                return CommandResult.SUCCESS;
            }
            Tasks.runAsync(() -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (PlayerManager.getInstance().doesDocumentExistByName(target.getName())) {
                    data.getMessageSettings().getIgnoreList().add(target.getName());
                    sender.sendMessage(Lang.SUCCESS_IGNORE.getMsg(target.getName()));
                } else {
                    sender.sendMessage(Lang.PLAYER_NOT_FOUND);
                }
            });
            return CommandResult.SUCCESS;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (!data.getMessageSettings().isIgnoring(args[1])) {
                sender.sendMessage(Lang.NOT_IGNORED);
                return CommandResult.SUCCESS;
            }
            data.getMessageSettings().getIgnoreList().removeIf(name -> name.equalsIgnoreCase(args[1]));
            player.sendMessage(Lang.SUCCESS_UNIGNORE.getMsg(args[1]));
            return CommandResult.SUCCESS;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (data.getMessageSettings().getIgnoreList().size() > 0) {
                sender.sendMessage(CC.SEPARATOR);
                sender.sendMessage(Lang.IGNORE_LIST_HEADER.getMsg());
                for (String s : data.getMessageSettings().getIgnoreList()) {
                    TextComponent component = new Clickable()
                            .add("&7 - &e" + s, CC.YELLOW + "Click to un-ignore this player!", "/ignore remove " + s);
                    player.spigot().sendMessage(component);
                }
                sender.sendMessage(CC.SEPARATOR);
            } else {
                player.sendMessage(Lang.NOT_IGNORING_ANYONE.toString());
            }
            return CommandResult.SUCCESS;
        }
        return CommandResult.INVALID_ARGS;
    }

}
