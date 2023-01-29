package net.octopvp.octocore.common.object.punish;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.octopvp.octocore.common.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PunishmentType {

    BAN, BLACKLIST,
    MUTE,
    KICK,
    WARN;

    @Getter
    /**
     * Immutable list of all punishment types as strings
     */
    private static final List<String> names;

    static {
        names = ImmutableList.copyOf(Arrays.stream(values()).map(Enum::name).collect(Collectors.toList()));
    }

    public String getFriendlyName() {
        return StringUtils.capatalizeFirst(name().toLowerCase());
    }

    public boolean hasDuration() {
        return this != KICK;
    }
}
