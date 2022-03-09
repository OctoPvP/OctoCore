package net.octopvp.octocore.common.object;

import lombok.Getter;

public enum WorldTime {
    DAY(0l), NIGHT(20000l), SUNSET(12500l), DEFAULT(-1);
    @Getter
    private long time;

    WorldTime(long time) {
        this.time = time;
    }
}
