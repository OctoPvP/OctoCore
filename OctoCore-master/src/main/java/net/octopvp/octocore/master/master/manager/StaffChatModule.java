package net.octopvp.octocore.master.master.manager;

import lombok.Getter;
import lombok.extern.java.Log;
import net.octopvp.octocore.common.object.FixedList;
import net.octopvp.octocore.master.master.redis.impl.StaffChatPacket;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Log
public class StaffChatModule {
    @Getter
    private static StaffChatModule instance;

    @Getter
    private List<Function<StaffChatPacket, Void>> updateCallbacks = new ArrayList<>();

    @PostConstruct
    public void init() {
        instance = this;
    }

    @Getter
    private FixedList<StaffChatPacket> messages = new FixedList<>(100); // Store last 100 messages
    public void onMessageSent(StaffChatPacket packet) {
        log.info("Received staff chat message from " + packet.getName() + " on " + packet.getServer() + ": " + packet.getMessage());
        messages.add(packet);
        for (Function<StaffChatPacket, Void> callback : updateCallbacks) {
            callback.apply(packet);
        }
    }
}
