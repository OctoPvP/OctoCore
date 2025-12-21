package net.octopvp.octocore.core.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class Enchantments {
    private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    private static final Map<String, Enchantment> ALIASES = new HashMap<>();

    static {
        // ahngkjabngkjang
        for (Enchantment enchantment : Enchantment.values()) {
            String key = enchantment.getKey().getKey();
            ENCHANTMENTS.put(key, enchantment);
            ENCHANTMENTS.put(enchantment.getKey().toString(), enchantment);
        }

        // 2. REGISTER MANUAL ALIASES (Shortcuts)
        // Damage / Sharpness
        registerAlias("alldamage", "sharpness");
        registerAlias("alldmg", "sharpness");
        registerAlias("sharp", "sharpness");
        registerAlias("dal", "sharpness");

        // Arthropods
        registerAlias("ardmg", "bane_of_arthropods");
        registerAlias("baneofarthropod", "bane_of_arthropods");
        registerAlias("arthropod", "bane_of_arthropods");
        registerAlias("dar", "bane_of_arthropods");

        // Smite
        registerAlias("undeaddamage", "smite");
        registerAlias("du", "smite");

        // Efficiency
        registerAlias("digspeed", "efficiency");
        registerAlias("minespeed", "efficiency");
        registerAlias("cutspeed", "efficiency");
        registerAlias("ds", "efficiency");
        registerAlias("eff", "efficiency");

        // Durability / Unbreaking
        registerAlias("durability", "unbreaking");
        registerAlias("dura", "unbreaking");
        registerAlias("d", "unbreaking");

        // Thorns
        registerAlias("highcrit", "thorns");
        registerAlias("thorn", "thorns");
        registerAlias("highercrit", "thorns");
        registerAlias("t", "thorns");

        // Fire Aspect
        registerAlias("fire", "fire_aspect");
        registerAlias("meleefire", "fire_aspect");
        registerAlias("meleeflame", "fire_aspect");
        registerAlias("fa", "fire_aspect");

        // Knockback
        registerAlias("kback", "knockback");
        registerAlias("kb", "knockback");
        registerAlias("k", "knockback");

        // Fortune
        registerAlias("blockslootbonus", "fortune");
        registerAlias("fort", "fortune");
        registerAlias("lbb", "fortune");

        // Looting
        registerAlias("mobslootbonus", "looting");
        registerAlias("mobloot", "looting");
        registerAlias("lbm", "looting");

        // Respiration
        registerAlias("oxygen", "respiration");
        registerAlias("breathing", "respiration");
        registerAlias("breath", "respiration");
        registerAlias("o", "respiration");

        // Protection
        registerAlias("protection", "protection");
        registerAlias("prot", "protection");
        registerAlias("protect", "protection");
        registerAlias("p", "protection");

        // Blast Protection
        registerAlias("explosionsprotection", "blast_protection");
        registerAlias("explosionprotection", "blast_protection");
        registerAlias("expprot", "blast_protection");
        registerAlias("bprotection", "blast_protection");
        registerAlias("bprotect", "blast_protection");
        registerAlias("blastprotect", "blast_protection");
        registerAlias("pe", "blast_protection");

        // Feather Falling
        registerAlias("fallprotection", "feather_falling");
        registerAlias("fallprot", "feather_falling");
        registerAlias("featherfall", "feather_falling");
        registerAlias("pfa", "feather_falling");

        // Fire Protection
        registerAlias("fireprotection", "fire_protection");
        registerAlias("flameprotection", "fire_protection");
        registerAlias("fireprotect", "fire_protection");
        registerAlias("flameprotect", "fire_protection");
        registerAlias("fireprot", "fire_protection");
        registerAlias("flameprot", "fire_protection");
        registerAlias("pf", "fire_protection");

        // Projectile Protection
        registerAlias("projectileprotection", "projectile_protection");
        registerAlias("projprot", "projectile_protection");
        registerAlias("pp", "projectile_protection");

        // Silk Touch
        registerAlias("silktouch", "silk_touch");
        registerAlias("softtouch", "silk_touch");
        registerAlias("st", "silk_touch");

        // Aqua Affinity
        registerAlias("waterworker", "aqua_affinity");
        registerAlias("watermine", "aqua_affinity");
        registerAlias("ww", "aqua_affinity");

        // Flame
        registerAlias("firearrow", "flame");
        registerAlias("flamearrow", "flame");
        registerAlias("af", "flame");

        // Power
        registerAlias("arrowdamage", "power");
        registerAlias("arrowpower", "power");
        registerAlias("ad", "power");

        // Punch
        registerAlias("arrowknockback", "punch");
        registerAlias("arrowkb", "punch");
        registerAlias("arrowpunch", "punch");
        registerAlias("ak", "punch");

        // Infinity
        registerAlias("infinitearrows", "infinity");
        registerAlias("infarrows", "infinity");
        registerAlias("infinite", "infinity");
        registerAlias("unlimited", "infinity");
        registerAlias("unlimitedarrows", "infinity");
        registerAlias("ai", "infinity");

        // Luck of the Sea
        registerAlias("luck", "luck_of_the_sea");
        registerAlias("rodluck", "luck_of_the_sea");

        // Lure
        registerAlias("rodlure", "lure");

        // Depth Strider
        registerAlias("depth", "depth_strider");
        registerAlias("strider", "depth_strider");

        // Frost Walker
        registerAlias("frost", "frost_walker");
        registerAlias("walker", "frost_walker");

        // Curses & Sweeping
        registerAlias("bindingcurse", "binding_curse");
        registerAlias("bindcurse", "binding_curse");
        registerAlias("bind", "binding_curse");
        registerAlias("vanishingcurse", "vanishing_curse");
        registerAlias("vanishcurse", "vanishing_curse");
        registerAlias("vanish", "vanishing_curse");
        registerAlias("sweepingedge", "sweeping_edge");
        registerAlias("sweepedge", "sweeping_edge");
        registerAlias("sweeping", "sweeping_edge");

        // Trident
        registerAlias("loyal", "loyalty");
        registerAlias("return", "loyalty");
        registerAlias("impale", "impaling");
        registerAlias("oceandamage", "impaling");
        registerAlias("oceandmg", "impaling");
        registerAlias("rip", "riptide");
        registerAlias("tide", "riptide");
        registerAlias("launch", "riptide");
        registerAlias("chanelling", "channeling");
        registerAlias("channel", "channeling");

        // Crossbow
        registerAlias("tripleshot", "multishot");
        registerAlias("quickdraw", "quick_charge");
        registerAlias("fastcharge", "quick_charge");
        registerAlias("fastdraw", "quick_charge");

        // Soul Speed
        registerAlias("soilspeed", "soul_speed");
        registerAlias("sandspeed", "soul_speed");
    }

    private Enchantments() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets an enchantment by name, alias, or NamespacedKey key.
     *
     * @param name The name to search for (e.g. "sharpness", "minecraft:sharpness", "alldmg")
     * @return The found Enchantment, or null if invalid.
     */
    public static Enchantment getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        // 1. Normalize input (lowercase, remove spaces to match "fire_aspect" against "fire aspect")
        String cleanName = name.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        String rawName = name.toLowerCase(Locale.ENGLISH);

        // 2. Check exact cache (Dynamically loaded keys)
        if (ENCHANTMENTS.containsKey(cleanName)) {
            return ENCHANTMENTS.get(cleanName);
        }

        // 3. Check Aliases
        if (ALIASES.containsKey(cleanName)) {
            return ALIASES.get(cleanName);
        }
        if (ALIASES.containsKey(rawName)) {
            return ALIASES.get(rawName);
        }

        try {
            NamespacedKey key = NamespacedKey.minecraft(cleanName);
            Enchantment enchantment = Enchantment.getByKey(key);
            if (enchantment != null) {
                return enchantment;
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid key format
        }

        for (Entry<String, Enchantment> entry : ENCHANTMENTS.entrySet()) {
            if (entry.getKey().replace("_", "").equals(cleanName.replace("_", ""))) {
                return entry.getValue();
            }
        }

        return null;
    }

    /**
     * Helper to register an alias to a valid Minecraft key.
     * Prevents NullPointers if the server version doesn't support the enchantment yet.
     */
    private static void registerAlias(String alias, String minecraftKey) {
        Enchantment target = ENCHANTMENTS.get(minecraftKey.toLowerCase(Locale.ENGLISH));
        if (target != null) {
            ALIASES.put(alias.toLowerCase(Locale.ENGLISH), target);
        }
    }

    public static Set<Entry<String, Enchantment>> entrySet() {
        return ENCHANTMENTS.entrySet();
    }

    public static Set<String> keySet() {
        return ENCHANTMENTS.keySet();
    }

    public static void registerEnchantment(String name, Enchantment enchantment) {
        if (ENCHANTMENTS.containsKey(name) || ALIASES.containsKey(name)) {
            return;
        }
        ENCHANTMENTS.put(name, enchantment);
    }

    public static void registerAlias(String name, Enchantment enchantment) {
        if (ENCHANTMENTS.containsKey(name) || ALIASES.containsKey(name)) {
            return;
        }
        ALIASES.put(name, enchantment);
    }

    public static String[] getEnchantmentNames() {
        List<String> names = new ArrayList<>(ENCHANTMENTS.keySet());
        names.addAll(ALIASES.keySet());
        return names.toArray(new String[0]);
    }
}