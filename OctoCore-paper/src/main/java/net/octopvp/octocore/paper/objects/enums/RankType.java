package net.octopvp.octocore.paper.objects.enums;

import net.octopvp.octocore.common.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RankType {
    DEFAULT, HIDDEN, DONATOR, STAFF;

    public static String toMessage() {
        return StringUtils.getStringFromList(Stream.of(RankType.values()).map(RankType::toString)
                .map(String::toLowerCase).map(StringUtils::convertFirstUpperCase).collect(Collectors.toList()));
    }
}
