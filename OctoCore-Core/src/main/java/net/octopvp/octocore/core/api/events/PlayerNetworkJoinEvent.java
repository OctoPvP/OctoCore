package net.octopvp.octocore.core.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.core.api.SimpleEvent;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerNetworkJoinEvent extends SimpleEvent {
    private final UUID uuid;
    private final String server;
}
