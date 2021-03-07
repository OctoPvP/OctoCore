package net.octopvp.octocore.paper.utils.cooldown;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Cooldown {
    public static void createCooldown(String k) {
        if (cooldown.containsKey(k))
            throw new IllegalArgumentException("Cooldown already exists.");
        cooldown.put(k, new HashMap<>());
    }

    public static HashMap<UUID, Long> getCooldownMap(String k) {
        if (cooldown.containsKey(k))
            return cooldown.get(k);
        return null;
    }

    public static void addCooldown(String k, Player p, int seconds) {
        if (!cooldown.containsKey(k))
            throw new IllegalArgumentException(String.valueOf(k) + " does not exist");
        long next = System.currentTimeMillis() + seconds * 1000L;
        ((HashMap<UUID, Long>)cooldown.get(k)).put(p.getUniqueId(), Long.valueOf(next));
    }

    public static boolean isOnCooldown(String k, Player p) {
        return (cooldown.containsKey(k) && ((HashMap)cooldown.get(k)).containsKey(p.getUniqueId()) &&
                System.currentTimeMillis() <= ((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue());
    }

    public static int getCooldownForPlayerInt(String k, Player p) {
        return (int)(((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue() -
                System.currentTimeMillis()) / 1000;
    }

    public static long getCooldownForPlayerLong(String k, Player p) {
        return (int)(((Long)((HashMap)cooldown.get(k)).get(p.getUniqueId())).longValue() -
                System.currentTimeMillis());
    }

    public static void removeCooldown(String k, Player p) {
        if (!cooldown.containsKey(k))
            throw new IllegalArgumentException(String.valueOf(k) + " does not exist");
        ((HashMap)cooldown.get(k)).remove(p.getUniqueId());
    }

    private static HashMap<String, HashMap<UUID, Long>> cooldown = new HashMap<>();
}
