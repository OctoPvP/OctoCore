package net.octopvp.octocore.master.master.redis;

import net.octopvp.octocore.common.redis.packets.ServerDataPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisPackets {

    public RedisPackets() {
        LoggerFactory.getLogger(RedisPackets.class).info("Loading RedisPackets...");
        ServerDataPacket.setImplementation(connectedServer -> {
            Logger logger = LoggerFactory
                    .getLogger(ServerDataPacket.class);
            if (!connectedServer.isSafelyStopped()) { //removed because of time
                logger.info(connectedServer.getServerName() + " removed because it crashed(?)");
            } else {
                logger.info(connectedServer.getServerName() + " removed because it was safely stopped.");
            }
        });
    }
}
