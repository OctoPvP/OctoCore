package net.octopvp.octocore.core.command.impl.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.commander.annotation.Async;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.MessageSettings;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Command(name = "ignore", description = "Ignore a player")
@PlayerOnly
@Async
public class IgnoreCommand {
    /*
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
     */
    @Command(name = "add")
    public void executeAdd(@Sender Player sender, OfflineHelpers.OfflineInfo target) {
        PlayerData data = PlayerManager.getInstance().getData(sender);
        if (data.getMessageSettings().getIgnoreList().size() >= MessageSettings.MAX_IGNORE_SIZE) {
            sender.sendMessage(Lang.IGNORE_LIST_FULL.toString());
            return;
        }
        if (target.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(Lang.CANNOT_IGNORE_SELF.toString());
            return;
        }
        Tasks.runAsync(() -> {
            // OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            /*
            if (PlayerManager.getInstance().doesDocumentExistByName(target.getName())) {
                data.getMessageSettings().getIgnoreList().put(target.getUuid(), target.getDisplayName());
                sender.sendMessage(Lang.SUCCESS_IGNORE.getMsg(target.getDisplayName()));
            } else {
                sender.sendMessage(Lang.PLAYER_NOT_FOUND.getMsg(target.getDisplayName()));
            }
             */
            // don't leak who's in the db or not
            data.getMessageSettings().getIgnoreList().put(target.getUuid(), target.getDisplayName());
            sender.sendMessage(Lang.SUCCESS_IGNORE.getMsg(target.getDisplayName()));
        });
    }

    @Command(name = "remove")
    public void executeRemove(@Sender Player sender, OfflineHelpers.OfflineInfo target) {
        PlayerData data = PlayerManager.getInstance().getData(sender);
        if (!data.getMessageSettings().isIgnoring(target.getUuid())) {
            sender.sendMessage(Lang.NOT_IGNORED.toString());
            return;
        }
        // data.getMessageSettings().getIgnoreList().removeIf(name -> name.equalsIgnoreCase(args[1]));
        data.getMessageSettings().getIgnoreList().remove(target.getUuid());
        sender.sendMessage(Lang.SUCCESS_UNIGNORE.getMsg(target.getDisplayName()));
    }

    @Command(name = "list")
    public void executeList(@Sender Player sender) {
        PlayerData data = PlayerManager.getInstance().getData(sender);
        if (data.getMessageSettings().getIgnoreList().size() > 0) {
            sender.sendMessage(CC.SEPARATOR);
            sender.sendMessage(Lang.IGNORE_LIST_HEADER.getMsg());
            String[] list = data.getMessageSettings().getIgnoreList().values().toArray(new String[0]);
            Arrays.sort(list);
            for (String s : list) {
                // TextComponent component = new Clickable()
                //        .add("&7 - &e" + s, CC.YELLOW + "Click to un-ignore this player!", "/ignore remove " + s);
                Component component = Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(s, NamedTextColor.YELLOW)
                                .clickEvent(ClickEvent.runCommand("/ignore remove " + s))
                                .hoverEvent(HoverEvent.showText(Component.text("Click to un-ignore this player!", NamedTextColor.YELLOW))));
                sender.sendMessage(component);
            }
            sender.sendMessage(CC.SEPARATOR);
        } else {
            sender.sendMessage(Lang.NOT_IGNORING_ANYONE.toString());
        }
    }
}
