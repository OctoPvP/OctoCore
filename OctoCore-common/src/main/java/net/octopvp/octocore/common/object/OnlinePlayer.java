package net.octopvp.octocore.common.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.redis.packets.PlayerMessagePacket;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OnlinePlayer {
    private UUID uuid;
    private String name, address, server;

    public static OnlinePlayer createDummyData() {
        return new OnlinePlayer(UUID.randomUUID(), "Player" + (int) (Math.random() * 1000), "127.0.0.1", "dummy");
    }

    public void sendMessage(String message) {
        new PlayerMessagePacket(uuid, message).send();
    }
}
