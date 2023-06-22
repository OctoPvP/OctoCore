package net.octopvp.octocore.common.util;

import java.util.UUID;
import java.util.function.Function;

public class BedrockUtils {
    public static Function<UUID, Boolean> isBedrockPlayer = uuid -> uuid.getMostSignificantBits() == 0;

    public static boolean isBedrockPlayer(UUID uuid) {
        return isBedrockPlayer.apply(uuid);
    }
}
