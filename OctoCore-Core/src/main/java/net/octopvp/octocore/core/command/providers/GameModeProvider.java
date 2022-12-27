package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

public class GameModeProvider implements Provider<GameMode> {
    private static final HashMap<String, GameMode> gameModes = new HashMap<>();

    static {
        gameModes.put("c", GameMode.CREATIVE);
        gameModes.put("creative", GameMode.CREATIVE);
        gameModes.put("s", GameMode.SURVIVAL);
        gameModes.put("survival", GameMode.SURVIVAL);
        gameModes.put("sp", GameMode.SPECTATOR);
        gameModes.put("spectator", GameMode.SPECTATOR);
        gameModes.put("a", GameMode.ADVENTURE);
        gameModes.put("adventure", GameMode.ADVENTURE);
        gameModes.put("1", GameMode.CREATIVE);
        gameModes.put("0", GameMode.SURVIVAL);
        gameModes.put("2", GameMode.ADVENTURE);
        gameModes.put("3", GameMode.SPECTATOR);
    }

    @Override
    public GameMode provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        GameMode gm = gameModes.get(args.pop().toLowerCase());
        if (gm == null) {
            throw new InvalidArgsException("That is not a valid gamemode!");
        }
        return gm;
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return Arrays.asList("c", "s", "sp", "a", "creative", "survival", "spectator", "adventure", "0", "1", "2", "3");
    }
}
