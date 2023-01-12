package net.octopvp.octocore.common;

public enum SubChannels {
    GOTO("GoTo");
    private final String subChannel;

    SubChannels(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getSubChannel() {
        return subChannel;
    }
}
