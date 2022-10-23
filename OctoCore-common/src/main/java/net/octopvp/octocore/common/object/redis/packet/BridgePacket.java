package net.octopvp.octocore.common.object.redis.packet;

import net.octopvp.octocore.common.OctoCoreCommon;

public abstract class BridgePacket extends RedisPacket {
    @Override
    public void send() {
        OctoCoreCommon.getInstance().getRedisManager().writeBridge(this);
    }
}
