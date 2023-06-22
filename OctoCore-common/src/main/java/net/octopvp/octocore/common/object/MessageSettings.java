package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class MessageSettings {
    private UUID lastMessage;
    private Map<UUID, String> ignoreList = new HashMap<>();
    private boolean soundsEnabled = true;
    private boolean globalChat = true;
    private boolean messagesOff = false;

    public boolean isIgnoring(UUID player) {
        if (player == null) return false;
        return this.ignoreList.containsKey(player);
    }
}
