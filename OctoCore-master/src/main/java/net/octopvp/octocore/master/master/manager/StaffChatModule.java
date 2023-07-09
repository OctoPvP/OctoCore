package net.octopvp.octocore.master.master.manager;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.java.Log;
import net.octopvp.octocore.common.object.FixedList;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@Log
public class StaffChatModule {
    @Getter
    private static StaffChatModule instance;

    @Getter
    private final List<Function<ChatPacket, Void>> updateCallbacks = new ArrayList<>();

    @PostConstruct
    public void init() {
        instance = this;
    }

    @Getter
    private final Map<Class<? extends ChatPacket>, FixedList<ChatPacket>> messages = new HashMap<>(); // Store last 100 messages

    public void onMessageSent(ChatPacket packet) {
        log.info("Received staff chat message from " + packet.getName() + " on " + packet.getServer() + ": " + packet.getMessage());
        messages.computeIfAbsent(packet.getClass(), k -> new FixedList<>(100)).add(packet);
        updateCallbacks.forEach(callback -> callback.apply(packet));
    }
}
