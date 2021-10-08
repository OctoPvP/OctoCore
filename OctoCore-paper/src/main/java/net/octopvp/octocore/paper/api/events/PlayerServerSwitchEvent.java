package net.octopvp.octocore.paper.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.api.SimpleEvent;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerServerSwitchEvent extends SimpleEvent {
    private final UUID uuid;
    private final String to,from;
}
