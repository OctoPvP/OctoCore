package net.octopvp.octocore.common.object;

public enum ServerType {
    PRACTICE("Practice"),HUB("Hub"),EVENT("Event"),FACTIONS("Factions"),KITPVP("KitPvP"),DEV("Beta"),MASTER("Master"),OTHER("Other"),LIMBO("Limbo");
    String name;
    ServerType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
