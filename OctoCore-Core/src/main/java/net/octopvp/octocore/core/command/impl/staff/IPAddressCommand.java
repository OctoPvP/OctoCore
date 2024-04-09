package net.octopvp.octocore.core.command.impl.staff;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.DefaultNumber;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bukkit.command.CommandSender;

public class IPAddressCommand {
    @Command(name = "ipaddress", description = "Get the IP address of a player")
    @Permission(Permissions.ADMIN)
    public void ipaddress(CommandSender sender, OfflineHelpers.OfflineInfo player, @DefaultNumber(20) int limit) {
        ImmutableList<String> addresses = PlayerManager.getInstance().getAddresses(player.getUuid());
        // sender.sendMessage(CC.AQUA + "Last " + CC.GOLD + limit + CC.AQUA + " addresses of " + player.getFormattedName(false, null, false) + CC.GRAY + ":");
        Component c = MiniMessage.miniMessage().deserialize(
                "<aqua>Last <gold><limit/><aqua> addresses of <green><player/><gray>:",
                Placeholder.component("player", Component.text(player.getDisplayName())),
                Placeholder.component("limit", Component.text(limit))
        );
        sender.sendMessage(c);
        addresses.stream().limit(limit).forEach(address -> {
            Component component = MiniMessage.miniMessage().deserialize(
                    "<gray> - <white><hover:show_text:\"Click to copy\"><click:copy_to_clipboard:" + address + "><address/>",
                    Placeholder.component("address", Component.text(address))
            );
            sender.sendMessage(component);
        });
    }
}
