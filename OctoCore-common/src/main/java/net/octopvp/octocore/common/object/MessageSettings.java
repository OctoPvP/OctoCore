package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MessageSettings {
    private boolean messagesOff = false;
    private UUID lastMessage;
    private List<String> ignoreList = new ArrayList<>();
    private boolean soundsEnabled = true;
    private boolean globalChat = true;
    private boolean chatMention = true;

    public boolean isIgnoring(String name) {
        if (name == null) return false;
        return this.ignoreList.stream().anyMatch(u -> u.equalsIgnoreCase(name));
    }
}
