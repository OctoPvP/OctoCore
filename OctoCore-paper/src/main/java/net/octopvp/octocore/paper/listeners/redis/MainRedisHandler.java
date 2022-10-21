package net.octopvp.octocore.paper.listeners.redis;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainRedisHandler {
    @Getter
    private static final Set<UUID> saving = new HashSet<>();

}
