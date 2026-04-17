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
import java.util.stream.Stream;

public class RPGStatsCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        if (args.length < 2) {
            stack.getSender().sendMessage(CC.RED + "Usage: /rpgstats <player> <stat|clear> [value]");
            stack.getSender().sendMessage(CC.RED + "Stats: level, vitality, resilience, strength, agility, intelligence, karma, mana, xp, attribute_points, ability_points, skill_points, class_points, boags_eaten");
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

        if (action.equals("clear")) {
            data.setLevel(1);
            data.setBaseVitality(0);
            data.setBaseResilience(0);
            data.setBaseStrength(0);
            data.setBaseAgility(0);
            data.setBaseIntelligence(0);
            data.setBaseKarma(0);
            data.setXp(0);
            data.setAttributePoints(0);
            data.setAbilityPoints(0);
            data.setSkillPoints(0);
            data.setClassPoints(0);
            data.setBoagsEaten(0);
            data.setCurrentManaLeft(0); // Will be recalculated in update
            
            data.update();
            RPGPlayerManager.getInstance().saveData(data);
            stack.getSender().sendMessage(CC.GREEN + "Reset all stats for " + target.getName() + " to base values.");
            return;
        }

        if (args.length < 3) {
            stack.getSender().sendMessage(CC.RED + "Usage: /rpgstats <player> <stat> <value>");
            return;
        }

        int value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            stack.getSender().sendMessage(CC.RED + "Invalid value.");
            return;
        }

        switch (action) {
            case "level" -> data.setLevel(value);
            case "vitality" -> data.setBaseVitality(value);
            case "resilience" -> data.setBaseResilience(value);
            case "strength" -> data.setBaseStrength(value);
            case "agility" -> data.setBaseAgility(value);
            case "intelligence" -> data.setBaseIntelligence(value);
            case "karma" -> data.setBaseKarma(value);
            case "mana" -> data.setCurrentManaLeft(value);
            case "xp" -> data.setXp(value);
            case "attribute_points" -> data.setAttributePoints(value);
            case "ability_points" -> data.setAbilityPoints(value);
            case "skill_points" -> data.setSkillPoints(value);
            case "class_points" -> data.setClassPoints(value);
            case "boags_eaten" -> data.setBoagsEaten(value);
            default -> {
                stack.getSender().sendMessage(CC.RED + "Unknown stat or action: " + action);
                return;
            }
        }

        data.update();
        RPGPlayerManager.getInstance().saveData(data);
        stack.getSender().sendMessage(CC.GREEN + "Set " + action + " of " + target.getName() + " to " + value);
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            List<String> options = new java.util.ArrayList<>(List.of("level", "vitality", "resilience", "strength", "agility", "intelligence", "karma", "mana", "xp", "attribute_points", "ability_points", "skill_points", "class_points", "boags_eaten", "clear"));
            return options.stream()
                    .filter(stat -> stat.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
