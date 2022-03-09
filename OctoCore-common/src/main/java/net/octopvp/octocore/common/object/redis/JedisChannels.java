package net.octopvp.octocore.common.object.redis;

public enum JedisChannels {
    OCTOCORE("octocore");
    private final String channel;

    JedisChannels(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
