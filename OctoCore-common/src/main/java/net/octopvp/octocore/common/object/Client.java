package net.octopvp.octocore.common.object;

public enum Client {
    LUNAR("Lunar Client"),
    BADLION("Badlion Client"),
    CHEATBREAKER("CheatBreaker"),
    COSMIC("Cosmic Client"),
    LOUNGE("PvP Lounge"),
    FORGE("Forge"),
    OCTOPVP_STAFF_CLIENT("OctoPvP Staff Client"),
    OTHER("Other");
    private final String friendlyName;

    Client(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

}
