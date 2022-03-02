package net.octopvp.octocore.paper.listeners.redis;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class MainRedisHandler {
    @Getter
    private static final Set saving = new HashSet();

}
