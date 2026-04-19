package net.octopvp.octocore.rpg.util;

import net.octopvp.octocore.rpg.object.EnchantmentRarity;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentUtil {
    private static final Map<String, EnchantmentRarity> RARITY_MAP = new HashMap<>();
    private static final Map<String, String> DESCRIPTION_MAP = new HashMap<>();

    static {
        // Common
        register("sharpness", EnchantmentRarity.COMMON);
        register("smite", EnchantmentRarity.COMMON);
        register("bane_of_arthropods", EnchantmentRarity.COMMON);
        register("knockback", EnchantmentRarity.COMMON);
        register("fire_aspect", EnchantmentRarity.COMMON);
        register("looting", EnchantmentRarity.COMMON);
        register("sweeping_edge", EnchantmentRarity.COMMON);
        register("efficiency", EnchantmentRarity.COMMON);
        register("silk_touch", EnchantmentRarity.COMMON);
        register("unbreaking", EnchantmentRarity.COMMON);
        register("fortune", EnchantmentRarity.COMMON);
        register("luck_of_the_sea", EnchantmentRarity.COMMON);
        register("lure", EnchantmentRarity.COMMON);
        register("mending", EnchantmentRarity.COMMON);
        register("vanishing_curse", EnchantmentRarity.COMMON);
        register("protection", EnchantmentRarity.COMMON);
        register("fire_protection", EnchantmentRarity.COMMON);
        register("feather_falling", EnchantmentRarity.COMMON);
        register("blast_protection", EnchantmentRarity.COMMON);
        register("projectile_protection", EnchantmentRarity.COMMON);
        register("respiration", EnchantmentRarity.COMMON);
        register("aqua_affinity", EnchantmentRarity.COMMON);
        register("thorns", EnchantmentRarity.COMMON);
        register("depth_strider", EnchantmentRarity.COMMON);
        register("frost_walker", EnchantmentRarity.COMMON);
        register("binding_curse", EnchantmentRarity.COMMON);
        register("power", EnchantmentRarity.COMMON);
        register("punch", EnchantmentRarity.COMMON);
        register("flame", EnchantmentRarity.COMMON);
        register("infinity", EnchantmentRarity.COMMON);
        register("loyalty", EnchantmentRarity.COMMON);
        register("impaling", EnchantmentRarity.COMMON);
        register("riptide", EnchantmentRarity.COMMON);
        register("channeling", EnchantmentRarity.COMMON);
        register("multishot", EnchantmentRarity.COMMON);
        register("piercing", EnchantmentRarity.COMMON);
        register("quick_charge", EnchantmentRarity.COMMON);
        
        // Custom / Aetheria based ones
        register("accuracy", EnchantmentRarity.COMMON);
        register("balance", EnchantmentRarity.COMMON);
        register("comfort", EnchantmentRarity.COMMON);
        register("volume", EnchantmentRarity.COMMON);
        register("lightness", EnchantmentRarity.COMMON);

        // Uncommon
        register("bane", EnchantmentRarity.UNCOMMON);
        register("brilliance", EnchantmentRarity.UNCOMMON);
        register("dragon_slayer", EnchantmentRarity.UNCOMMON);
        register("wither", EnchantmentRarity.UNCOMMON);
        register("frostbolt", EnchantmentRarity.UNCOMMON);
        register("catscratch", EnchantmentRarity.UNCOMMON);

        // Rare
        register("vorpal", EnchantmentRarity.RARE);
        register("vampire", EnchantmentRarity.RARE);
        register("seraph", EnchantmentRarity.RARE);
        register("lifesteal", EnchantmentRarity.RARE);

        // Epic
        register("chains", EnchantmentRarity.EPIC);

        // Legendary
        register("glide", EnchantmentRarity.LEGENDARY);
        register("experienced", EnchantmentRarity.LEGENDARY);
        register("multipick", EnchantmentRarity.LEGENDARY);

        // Mythic
        register("wings", EnchantmentRarity.MYTHIC);
        register("titanic_chains", EnchantmentRarity.MYTHIC);
        register("plus", EnchantmentRarity.MYTHIC);

        // Custom Descriptions (Max 20 chars)
        describe("fire_aspect", "Burns enemies.");
        describe("seraph", "Holy damage.");
        describe("vampire", "Steal life.");
        describe("flametongue", "Fire strikes.");
        describe("frostbrand", "Freezing hits.");
        describe("shock", "Lightning hit.");
        describe("moonlight", "Lunar power.");
        describe("accuracy", "Homing arrows.");
        describe("balance", "Balanced hits.");
        describe("comfort", "Soft & cozy.");
        describe("lightness", "Move faster.");
        describe("bane", "Blocks healing.");
        describe("brilliance", "Blinds targets.");
        describe("catscratch", "Increases the power level of any debuffs currently affecting the target by +1 / LVL");
        describe("vorpal", "Decapitate chance.");
        describe("dragon_slayer", "Slay dragons.");
        describe("wither", "Wither rot.");
        describe("inventory_expansion", "More space.");
        describe("plus", "Extra stats.");
        describe("prosperity", "More loot.");
        describe("healing_water", "Heals in water.");
        describe("critical_resistance", "Less crit dmg.");
        describe("auto_shield", "Auto blocks.");
        describe("frostbolt", "Frozen shot.");
        describe("lifesteal", "Heals on hit.");
        describe("chains", "Binds enemies.");
        describe("glide", "Slow fall.");
        describe("experienced", "More XP gain.");
        describe("multipick", "Mines more.");
        describe("wings", "Flight power.");
        describe("titanic_chains", "Heavy binding.");
        describe("bleed", "Enemy bleeds.");
        describe("homing", "Seeking arrows.");
        describe("luminosity", "Bright light.");
        describe("surefooted", "No knockback.");
        describe("volume", "Large potions.");
    }

    private static void register(String key, EnchantmentRarity rarity) {
        RARITY_MAP.put(key.toLowerCase(), rarity);
    }

    private static void describe(String key, String desc) {
        DESCRIPTION_MAP.put(key.toLowerCase(), desc);
    }

    public static String getDescription(Enchantment enchantment) {
        String key = enchantment.getKey().getKey().toLowerCase();
        return DESCRIPTION_MAP.getOrDefault(key, "");
    }

    public static EnchantmentRarity getRarity(Enchantment enchantment) {
        String key = enchantment.getKey().getKey().toLowerCase();
        return RARITY_MAP.getOrDefault(key, EnchantmentRarity.COMMON);
    }

    public static boolean isMajor(Enchantment enchantment) {
        EnchantmentRarity rarity = getRarity(enchantment);
        return rarity == EnchantmentRarity.RARE || rarity == EnchantmentRarity.EPIC || rarity == EnchantmentRarity.LEGENDARY || rarity == EnchantmentRarity.MYTHIC;
    }
}
