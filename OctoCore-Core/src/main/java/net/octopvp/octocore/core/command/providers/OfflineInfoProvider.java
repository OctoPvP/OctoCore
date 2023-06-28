package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class OfflineInfoProvider implements Provider<OfflineHelpers.OfflineInfo> {
    @Override
    public OfflineHelpers.OfflineInfo provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return OfflineHelpers.getOfflineInfo(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        if (sender instanceof BukkitCommandSender) {
            CommandSender cs = ((BukkitCommandSender) sender).getSender();
            if (cs instanceof Player) {
                Player player = (Player) cs;
                return Bukkit.getOnlinePlayers().stream().filter(player::canSee)
                        .map(Player::getName).collect(Collectors.toList());
            }
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }
}
