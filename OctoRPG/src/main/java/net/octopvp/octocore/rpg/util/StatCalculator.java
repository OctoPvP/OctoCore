package net.octopvp.octocore.rpg.util;

import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;

public class StatCalculator {
    /**
     * Calculates the level scaling factor.
     * Level 1 = 1.0 (100% efficiency)
     * Level 50 = ~1.7x efficiency
     * Level 100 = 2.0x efficiency
     */
    public static double getLevelScaling(int level) {
        return 1.0 + (Math.sqrt(level) - 1) * 0.11;
    }

    public static int calculateMana(int level, int resilience) {
        // (Base 20 + 2 per resilience) * Level Scaling
        double scaling = getLevelScaling(level);
        return (int) ((20 + (2 * resilience)) * scaling);
    }

    public static double calculateHealth(int level, int vitality) {
        // (Base 20 + 2 per vitality) * Level Scaling
        double scaling = getLevelScaling(level);
        return (20 + (2 * vitality)) * scaling;
    }

    public static float calculateSpeed(int level, int agility) {
        // (Base 0.2 + 0.001 per agility) * Level Scaling
        // Level 100 (2.0x) with 0 agility = 0.4 speed (~11.2 m/s)
        // Level 100 (2.0x) with 100 agility = 0.6 speed (~16.8 m/s)
        double scaling = getLevelScaling(level);
        float speed = (float) ((0.2 + (0.001 * agility)) * scaling);
        if (speed > 1.0f) speed = 1.0f;
        return speed;
    }

    public static int calculateManaToRegen(int level, int intelligence, int baseMana) {
        // Base 5% regen, increased by intelligence (2% per point) scaled by level
        double scaling = getLevelScaling(level);
        double regenPercent = 0.05 * (1 + (intelligence * 0.02 * scaling));
        return (int) (baseMana * regenPercent);
    }

    public static int xpNeededForNextLevel(int level) {
        return (level * 1000) * 2;
    }

    public static double calculateDamage(Player player, double damage) {
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player.getUniqueId());
        if (data == null) return damage;
        return calculateDamage(data.getLevel(), data.getStrengthAfterCalc(), damage);
    }

    public static double calculateDamage(int level, int strength, double damage) {
        if (strength == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        
        // Base damage + weapon damage, increased by strength (1% per point) scaled by level
        double scaling = getLevelScaling(level);
        return (5 + damage) * (1 + (strength * 0.01 * scaling));
    }

    public int xpNeededForLevel(int n) {
        return (n * (n + 1)) * 1000;
    }
}
