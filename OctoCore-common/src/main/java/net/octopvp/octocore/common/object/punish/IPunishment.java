package net.octopvp.octocore.common.object.punish;

import net.octopvp.octocore.common.util.DateUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.UUID;

public interface IPunishment {
    PunishmentType getType();

    default PunishmentType getPunishmentType() {
        return getType();
    }

    boolean isActive();

    boolean isPermanent();

    boolean isSilent();

    boolean isRemovedSilent();

    boolean isLast();

    boolean isIPRelative();

    boolean isTemporary();

    boolean hasExpired();

    long getAddedAt();

    long getDurationTime();

    long getWhenRemoved();

    String getReason();

    String getRemovedBy();

    String getEnteredDuration();

    String getRemovedFor();

    String getAddedByName();

    String getName();

    String getTargetAddress();

    UUID getAddedBy();

    UUID getId();

    UUID getTargetId();


    void setType(PunishmentType type);

    void setActive(boolean active);

    void setPermanent(boolean permanent);

    void setSilent(boolean silent);

    void setRemovedSilent(boolean removedSilent);

    void setLast(boolean last);

    void setIPRelative(boolean IPRelative);

    void setAddedAt(long addedAt);

    void setDurationTime(long durationTime);

    void setWhenRemoved(long whenRemoved);

    void setReason(String reason);

    void setRemovedBy(String removedBy);

    void setEnteredDuration(String enteredDuration);

    void setRemovedFor(String removedFor);

    void setAddedByName(String addedByName);

    void setName(String name);

    void setTargetAddress(String targetAddress);

    void setAddedBy(UUID addedBy);

    void setId(UUID id);

    void setTargetId(UUID targetId);

    void save(boolean replace); //Other servers calling this may need to get server with player to reload punishments

    void save();

    default String getNiceDuration() {
        if (isPermanent()) return "Permanent";
        if (getDurationTime() == -5L) return "";

        return DurationFormatUtils.formatDurationWords(DateUtils.handleParseTime(getEnteredDuration()), true, true);
    }

    default String getNiceExpire() {
        if (isPermanent()) return "Never";
        if (hasExpired()) return "Expired";
        if (getDurationTime() == -5L) return "";

        return DateUtils.formatDateDiff(this.getDurationTime());
    }

}
