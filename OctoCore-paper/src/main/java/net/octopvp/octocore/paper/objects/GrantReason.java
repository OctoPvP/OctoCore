package net.octopvp.octocore.paper.objects;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Skulls;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GrantReason {
    PROMOTION("Promotion",new ItemBuilder(Material.SKULL_ITEM).name(CC.GREEN + "Promotion").toSkullBuilder().base64Skin(Skulls.GREEN_ARROW_UP_BASE_64).buildSkull()),FAMOUS("Famous",
            new ItemBuilder(Material.SKULL_ITEM).name(CC.PURPLE + "Famous").toSkullBuilder().base64Skin(Skulls.YOUTUBE_BASE_64).buildSkull()
            ),STORE("Store Problem",new ItemBuilder(Material.SKULL_ITEM).name(CC.AQUA + "Store Problem").toSkullBuilder().base64Skin(Skulls.GREEN_DOLLAR_SKULL_BASE_64).buildSkull()),
    DEMOTION("Demotion",new ItemBuilder(Material.SKULL_ITEM).name(CC.RED + "Demotion").toSkullBuilder().base64Skin(Skulls.RED_ARROW_DOWN_BASE_64).buildSkull());
    private String reason;
    private ItemStack material;
    GrantReason(String reason, ItemStack m){
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
