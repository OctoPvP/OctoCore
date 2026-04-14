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
            Map<String, String> legacyEnchants = Map.of(
                    "lifesteal", "Lifesteal",
                    "bleed", "Bleed",
                    "brilliance", "Brilliance",
                    "homing", "Homing",
                    "luminosity", "Luminosity",
                    "surefooted", "Surefooted"
            );

            for (Map.Entry<String, String> entry : legacyEnchants.entrySet()) {
                String id = entry.getKey();
                String name = entry.getValue();
                
                event.registry().register(
                        TypedKey.create(RegistryKey.ENCHANTMENT, Key.key("minecraft", id)),
                        builder -> {
                            EnchantmentRegistryEntry.Builder enchantmentBuilder = (EnchantmentRegistryEntry.Builder) builder;
                            enchantmentBuilder.description(Component.text(name))
                                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON))
                                .anvilCost(1)
                                .maxLevel(5)
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
