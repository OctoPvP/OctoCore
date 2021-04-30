package net.octopvp.octocore.paper.database.redis.object;

public enum JedisChannels {
    OCTOCORE("OctoCore");
    private String channel;
    JedisChannels(String channel){
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
