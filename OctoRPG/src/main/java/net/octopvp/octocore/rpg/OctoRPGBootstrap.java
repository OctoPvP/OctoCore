package net.octopvp.octocore.rpg;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class OctoRPGBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            Map<String, String> enchants = Map.ofEntries(
                // Major / Lost
                Map.entry("fire_aspect", "Fire Aspect"),
                Map.entry("seraph", "Seraph"),
                Map.entry("vampire", "Vampire"),
                Map.entry("flametongue", "Flametongue"),
                Map.entry("frostbrand", "Frostbrand"),
                Map.entry("shock", "Shock"),
                Map.entry("moonlight", "Moonlight"),
                
                // Minor
                Map.entry("accuracy", "Accuracy"),
                Map.entry("balance", "Balance"),
                Map.entry("comfort", "Comfort"),
                Map.entry("lightness", "Lightness"),
                Map.entry("bane", "Bane"),
                Map.entry("brilliance", "Brilliance"),
                Map.entry("catscratch", "Catscratch"),
                Map.entry("vorpal", "Vorpal"),
                Map.entry("dragon_slayer", "Dragon Slayer"),
                Map.entry("wither", "Wither"),
                Map.entry("inventory_expansion", "Inventory Expansion"),
                
                // Special
                Map.entry("plus", "Plus"),
                Map.entry("prosperity", "Prosperity"),
                Map.entry("healing_water", "Healing Water"),
                Map.entry("critical_resistance", "Critical Resistance"),
                Map.entry("auto_shield", "Auto-Shield")
            );

            for (Map.Entry<String, String> entry : enchants.entrySet()) {
                String id = entry.getKey();
                String name = entry.getValue();
                
                event.registry().register(
                        TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("octorpg", id)),
                        builder -> {
                            EnchantmentRegistryEntry.Builder enchantmentBuilder = (EnchantmentRegistryEntry.Builder) builder;
                            enchantmentBuilder.description(Component.text(name))
                                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON))
                                .anvilCost(1)
                                .maxLevel(10)
                                .weight(10)
                                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .activeSlots(EquipmentSlotGroup.ANY);
                        }
                );
            }
        }));
    }
}
