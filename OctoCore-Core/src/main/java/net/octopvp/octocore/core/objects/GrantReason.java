package net.octopvp.octocore.core.objects;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.core.utils.Skulls;
import org.bukkit.inventory.ItemStack;

public enum GrantReason {
    PROMOTION("Promotion", ItemBuilder.skull().texture(Skulls.GREEN_ARROW_UP_BASE_64).name(Component.text("Promotion").color(NamedTextColor.GREEN)).build()),
    FAMOUS("Famous", ItemBuilder.skull().texture(Skulls.YOUTUBE_BASE_64).name(Component.text("Famous").color(NamedTextColor.DARK_PURPLE)).build()),
    STORE("Store Problem", ItemBuilder.skull().texture(Skulls.GREEN_DOLLAR_SKULL_BASE_64).name(Component.text("Store Problem").color(NamedTextColor.AQUA)).build()),
    DEMOTION("Demotion", ItemBuilder.skull().texture(Skulls.RED_ARROW_DOWN_BASE_64).name(Component.text("Demotion").color(NamedTextColor.RED)).build());
    private final String reason;
    private final ItemStack material;

    GrantReason(String reason, ItemStack m) {
        this.material = m;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return reason;
    }

    public ItemStack getMaterial() {
        return material;
    }
}
