package net.octopvp.octocore.common.object;

public enum ServerType {
    PRACTICE("Practice"),
    SURVIVAL("Survival"),
    HUB("Hub"),
    EVENT("Event"),
    FACTIONS("Factions"),
    KITPVP("KitPvP"),
    DEV("Beta"),
    OTHER("Other"),
    LIMBO("Limbo");
    String name;

    ServerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean allowCustomTime() {
        // if it is not survival and octocore.enable-custom-time is false
        return this != SURVIVAL && !Boolean.parseBoolean(System.getProperty("octocore.enable-custom-time", "false"));
    }
}
