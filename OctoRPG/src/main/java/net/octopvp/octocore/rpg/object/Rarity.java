package net.octopvp.octocore.rpg.object;

import net.octopvp.octocore.common.util.CC;

public enum Rarity {
    COMMON(CC.WHITE + CC.BOLD + "COMMON", CC.WHITE),
    UNCOMMON(CC.GREEN + CC.BOLD + "UNCOMMON", CC.GREEN),
    RARE(CC.BLUE + CC.BOLD + "RARE", CC.BLUE),
    EPIC(CC.PURPLE + CC.BOLD + "EPIC", CC.PURPLE),
    LEGENDARY(CC.GOLD + CC.BOLD + "LEGENDARY", CC.GOLD),
    MYTHIC(CC.PINK + CC.BOLD + "MYTHIC", CC.PINK),
    TEST(CC.GOLD + CC.BOLD + "TEST", CC.GOLD),
    ADMIN(CC.RED + CC.BOLD + "ADMIN", CC.RED);
    private final String s;
    private final String color;

    Rarity(String s, String color) {
        this.s = s;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return s;
    }
}
