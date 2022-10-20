package net.octopvp.octocore.common.object.redis.packet;

import net.octopvp.aetheriacore.common.AetheriaCoreCommon;

public abstract class BridgePacket extends RedisPacket {
    @Override
    public void send() {
        AetheriaCoreCommon.getInstance().getRedisManager().writeBridge(this);
    }
}
