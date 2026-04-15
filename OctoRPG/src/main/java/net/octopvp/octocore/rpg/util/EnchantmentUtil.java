package net.octopvp.octocore.rpg.util;

import net.octopvp.octocore.rpg.object.EnchantmentRarity;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentUtil {
    private static final Map<String, EnchantmentRarity> RARITY_MAP = new HashMap<>();

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
        
        // Custom / Aetheria based ones (using names from wiki search)
        register("accuracy", EnchantmentRarity.COMMON);
        register("balance", EnchantmentRarity.COMMON);
        register("comfort", EnchantmentRarity.COMMON);
        register("lightness", EnchantmentRarity.COMMON);

        // Uncommon
        register("bane", EnchantmentRarity.UNCOMMON);
        register("brilliance", EnchantmentRarity.UNCOMMON);
        register("dragon_slayer", EnchantmentRarity.UNCOMMON);
        register("wither", EnchantmentRarity.UNCOMMON);

        // Rare
        register("vorpal", EnchantmentRarity.RARE);
        register("vampire", EnchantmentRarity.RARE);
        register("seraph", EnchantmentRarity.RARE);

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
    }

    private static void register(String key, EnchantmentRarity rarity) {
        RARITY_MAP.put(key.toLowerCase(), rarity);
    }

    public static EnchantmentRarity getRarity(Enchantment enchantment) {
        String key = enchantment.getKey().getKey().toLowerCase();
        return RARITY_MAP.getOrDefault(key, EnchantmentRarity.COMMON);
    }
}
