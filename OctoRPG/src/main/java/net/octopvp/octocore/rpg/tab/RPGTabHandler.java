package net.octopvp.octocore.rpg.tab;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.TabManager;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;

public class RPGTabHandler {
    public static void init() {
        // Register this RPG stats provider to OctoCore's generic tab extension API
        TabManager.getFooterExtensions().add(RPGTabHandler::getStatsFooter);
    }

    private static String getStatsFooter(Player player) {
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data == null) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("Level: ").append(data.getLevel()).append(" XP: ").append(data.getXp()).append("\n");
        sb.append("Strength: ").append(data.getStrengthAfterCalc()).append(" Agility: ").append(data.getAgilityAfterCalc()).append("\n");
        sb.append("Vitality: ").append(data.getVitalityAfterCalc()).append(" Resilience: ").append(data.getResilianceAfterCalc()).append("\n");
        sb.append("Intelligence: ").append(data.getIntelligenceAfterCalc()).append(" Karma: ").append(data.getKarmaAfterCalc());
        
        return sb.toString();
    }
}
