package net.octopvp.octocore.paper.module.impl.punishments.utilities.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.DateUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Setter
public class PunishHistory {
    private final String findFor;
    private final PunishmentType punishmentType;

    private long addedAt = -1L;
    private long duration = -1L;
    private boolean permanent = false;
    private String executor, target, enteredDuration;
    private String reason;
    private boolean active;
    private boolean last;
    private boolean silent;

    public String getNiceDuration() {
        if (this.permanent) return "Permanent";
        if (this.duration == -5L) return "";

        return DurationFormatUtils.formatDurationWords(DateUtils.handleParseTime(this.enteredDuration), true, true);
    }

    public String getNiceExpire() {
        if (this.permanent) return "Never";
        if (hasExpired()) return "Expired";
        if (this.duration == -5L) return "";

        return DateUtils.formatDateDiff(this.getDuration());
    }

    public boolean hasExpired() {
        if (!isActive()) return true;
        if (isPermanent()) return false;
        if (!isLast()) return true;

        return System.currentTimeMillis() >= duration;
    }

    public static List<PunishHistory> getPunishments(PlayerData playerData, PunishmentType punishmentType, boolean activeOnly) {
        if (activeOnly) {
            return playerData.getPunishmentsExecuted().stream().filter(punishHistory -> punishHistory.getPunishmentType() == punishmentType).filter(punishHistory -> !punishHistory.hasExpired()).collect(Collectors.toList());
        }
        return playerData.getPunishmentsExecuted().stream().filter(punishHistory -> punishHistory.getPunishmentType() == punishmentType).collect(Collectors.toList());
    }
}
