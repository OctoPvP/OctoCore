package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RPGQuestCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        if (args.length < 2) {
            stack.getSender().sendMessage(CC.RED + "Usage: /rpgquest <player> <reset|complete> [questId]");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            stack.getSender().sendMessage(CC.RED + "Player not found.");
            return;
        }

        RPGPlayerData data = RPGPlayerManager.getInstance().getData(target);
        if (data == null) {
            stack.getSender().sendMessage(CC.RED + "Player data not found.");
            return;
        }

        String action = args[1].toLowerCase();
        if (action.equals("reset")) {
            data.getCompletedQuests().clear();
            data.getInProgressQuests().clear();
            stack.getSender().sendMessage(CC.GREEN + "Reset all quests for " + target.getName());
        } else if (action.equals("complete")) {
            if (args.length < 3) {
                stack.getSender().sendMessage(CC.RED + "Usage: /rpgquest <player> complete <questId>");
                return;
            }
            try {
                long questId = Long.parseLong(args[2]);
                data.setQuestCompleted(questId);
                stack.getSender().sendMessage(CC.GREEN + "Completed quest " + questId + " for " + target.getName());
            } catch (NumberFormatException e) {
                stack.getSender().sendMessage(CC.RED + "Invalid quest ID.");
            }
        } else {
            stack.getSender().sendMessage(CC.RED + "Unknown action: " + action);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return List.of("reset", "complete").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
