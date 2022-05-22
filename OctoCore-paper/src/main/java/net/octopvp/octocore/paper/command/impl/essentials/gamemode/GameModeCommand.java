package net.octopvp.octocore.paper.command.impl.essentials.gamemode;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameModeCommand {
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

    @Command(name = "gamemode", aliases = {"gm"}, usage = "<gamemode> [player]")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.INVALID_ARGS;
        }
        if (args.length >= 1) {
            GameMode gameMode = gameModes.get(args[0]);
            if (gameMode == null) {
                sender.sendMessage(CC.RED + args[0] + " is not a valid gamemode");
                return CommandResult.INVALID_ARGS;
            }
            if (!sender.hasPermission("octocore.command.gamemode." + gameMode.name().toLowerCase()))
                return CommandResult.NO_PERMS;
            if (args.length == 2) {
                Player target;
                try {
                    target = Bukkit.getPlayer(args[1]);
                } catch (Exception e) {
                    return CommandResult.INVALID_PLAYER;
                }
                target.sendMessage(Lang.GAMEMODE.getMsg(gameMode.name()));
                target.setGameMode(gameMode);
                sender.sendMessage(CC.GREEN + "Set " + target.getName() + "'s gamemode to " + gameMode.name());
                return CommandResult.SUCCESS;
            }
            sender.sendMessage(Lang.GAMEMODE.getMsg(gameMode.name()));
            sender.getPlayer().setGameMode(gameMode);
        }
        return CommandResult.SUCCESS;
    }

    @Completer(name = "gamemode", aliases = {"gm"})
    public List<String> tabComplete(Sender sender, String[] args) {
        if (args.length == 0) {
            return Lists.newArrayList("c", "s", "sp", "a", "creative", "survival", "spectator", "adventure", "0", "1", "2", "3");
        } else return new ArrayList<>();
        //return PlayerManager.getOnlinePlayersString();
    }
}
