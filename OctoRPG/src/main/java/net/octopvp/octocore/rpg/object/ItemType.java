package net.octopvp.octocore.rpg.object;

public enum ItemType {
    WEAPON,
    HEAD,
    CHEST,
    LEGS,
    FEET,
    FOOD,
    BLOCK,
    TOOL,
    AMULET,
    SHIELD,
    INTERNAL,
    MISC;

    public boolean isArmor() {
        return this == HEAD || this == CHEST || this == LEGS || this == FEET;
    }
}
