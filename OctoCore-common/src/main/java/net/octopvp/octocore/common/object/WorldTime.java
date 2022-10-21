package net.octopvp.octocore.common.object;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum WorldTime {
    DEFAULT(-1),
    SUNRISE(22550),
    DAY(0L),
    SUNSET(12500L),
    NIGHT(20000L);
    @Getter
    private final long time;

    WorldTime(long time) {
        this.time = time;
    }

    public String getFormattedName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
