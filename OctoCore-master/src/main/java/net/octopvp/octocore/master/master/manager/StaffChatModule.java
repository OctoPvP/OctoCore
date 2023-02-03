package net.octopvp.octocore.master.master.manager;

import lombok.Getter;
import net.octopvp.octocore.common.object.FixedList;
import net.octopvp.octocore.master.master.redis.impl.StaffChatPacket;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaffChatModule {
    @Getter
    private static StaffChatModule instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    private FixedList<StaffChatPacket> messages = new FixedList<>(100); // Store last 100 messages
    public void onMessageSent(StaffChatPacket packet) {
        messages.add(packet);
    }
}
