package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import net.octopvp.commander.bukkit.providers.PlayerProvider;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.annotation.CreateData;
import net.octopvp.octocore.core.command.annotation.OnlineOnly;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerDataProvider implements Provider<PlayerData> {
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}(-|)[0-9a-f]{4}(-|)[0-9a-f]{4}(-|)[0-9a-f]{4}(-|)[0-9a-f]{12}");

    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (context.getCommandInfo().getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            BukkitCommandSender sender = (BukkitCommandSender) context.getCommandSender();
            if (!sender.isPlayer()) {
                throw new CommandException("This command is player only!");
            }
            Player player = sender.getPlayer();
            return PlayerManager.getInstance().getData(player);
        }
        if (args.size() == 0) {
            if (parameterInfo.getParameter().isAnnotationPresent(DefaultSelf.class))
                return PlayerManager.getInstance().getData(((BukkitCommandSender) context.getCommandSender()).getPlayer());
            return null;
        }
        String arg = args.pop();
        // OfflinePlayer player = Bukkit.getOfflinePlayer(arg);
        OfflineHelpers.OfflineInfo player = OfflineHelpers.getOfflineInfo(arg);
        PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
        if (data == null && parameterInfo.getParameter().isAnnotationPresent(OnlineOnly.class)) {
            OnlineOnly onlineOnly = parameterInfo.getParameter().getAnnotation(OnlineOnly.class);
            if (!onlineOnly.network()) {
                throw new CommandException("Player is not online.");
            }
            OnlinePlayer onlinePlayer = OctoCore.getInstance().getServerManager().getOnlinePlayer(player.getUniqueId());
            if (onlinePlayer == null) {
                throw new CommandException("Player is not online.");
            }
            data = PlayerManager.getInstance().getDataEvenIfOffline(onlinePlayer.getUuid(), false);
        }
        if (data != null) return data;
        PlayerData d = PlayerManager.getInstance().getDataEvenIfOffline(player.getUniqueId(), parameterInfo.getParameter().isAnnotationPresent(CreateData.class));
        if (d == null) throw new CommandException("Player not found.");
        return d;
    }

    @Override
    public PlayerData provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return null;
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return PlayerProvider.suggest(sender);
    }

    @Override
    public boolean failOnException() {
        return true;
    }

    @Override
    public boolean failOnExceptionIgnoreOptional() {
        return true;
    }
}
