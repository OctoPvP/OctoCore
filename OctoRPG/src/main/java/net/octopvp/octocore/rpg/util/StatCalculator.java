package net.octopvp.octocore.rpg.util;

import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;

public class StatCalculator {
    public static int calculateMana(int level, int resilience) {
        return (int) ((20 + (2 * level)) * (1 + 0.01 * (resilience)));
    }

    public static double calculateHealth(int level, int vitality) {
        return ((20 + (2 * level)) * (1 + 0.01 * (vitality)));
    }

    public static float calculateSpeed(int level, int agility) {
        float speed = (float) (((20 + (2 * agility)) * (1 + 0.01 * (level))) / 100);
        if (speed > 1)
            speed = 1f;
        return speed;
    }

    public static int calculateManaToRegen(int level, int baseMana) {
        return (int) (baseMana * 0.05);
    }

    public static int xpNeededForNextLevel(int level) {
        return (level * 1000) * 2;
    }

    public static double calculateDamage(Player player, double damage) {
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (data == null) return damage;
        return calculateDamage(data.getStrengthAfterCalc(), damage);
    }

    public static double calculateDamage(int strength, double damage) {
        if (strength == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        double dblStr = strength + 0.0d;
        return (5 + damage) * (1 + (dblStr / 100));
    }

    public int xpNeededForLevel(int n) {
        return (n * (n + 1)) * 1000;
    }
}
