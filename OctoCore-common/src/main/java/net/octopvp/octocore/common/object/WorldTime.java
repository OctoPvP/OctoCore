package net.octopvp.octocore.common.object;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum WorldTime {
    DEFAULT(-1),
    SUNRISE(22550),
    DAY(0l),
    SUNSET(12500l),
    NIGHT(20000l);
    @Getter
    private final long time;

    WorldTime(long time) {
        this.time = time;
    }

    public String getFormattedName() {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
