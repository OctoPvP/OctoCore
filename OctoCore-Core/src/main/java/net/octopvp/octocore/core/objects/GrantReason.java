package net.octopvp.octocore.core.objects;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.Skulls;
import org.bukkit.inventory.ItemStack;

public enum GrantReason {
    PROMOTION("Promotion", ItemBuilder.skull().texture(Skulls.GREEN_ARROW_UP_BASE_64).name(CC.GREEN + "Promotion").build()),
    FAMOUS("Famous", ItemBuilder.skull().texture(Skulls.YOUTUBE_BASE_64).name(CC.PURPLE + "Famous").build()),
    STORE("Store Problem", ItemBuilder.skull().texture(Skulls.GREEN_DOLLAR_SKULL_BASE_64).name(CC.AQUA + "Store Problem").build()),
    DEMOTION("Demotion", ItemBuilder.skull().texture(Skulls.RED_ARROW_DOWN_BASE_64).name(CC.RED + "Demotion").build());
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
