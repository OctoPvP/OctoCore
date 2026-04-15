package net.octopvp.octocore.rpg.object;

import net.octopvp.octocore.common.util.CC;

public enum EnchantmentRarity {
    COMMON(CC.WHITE),
    UNCOMMON(CC.GREEN),
    RARE(CC.BLUE),
    EPIC(CC.PURPLE),
    LEGENDARY(CC.GOLD),
    MYTHIC(CC.PINK);

    private final String color;

    EnchantmentRarity(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
