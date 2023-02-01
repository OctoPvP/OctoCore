package net.octopvp.octocore.common.interfaces;

import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.common.util.DurationFormatUtils;

import java.util.UUID;

public interface IPunishment {
    PunishmentType getType();

    void setType(PunishmentType type);

    default PunishmentType getPunishmentType() {
        return getType();
    }

    boolean isActive();

    void setActive(boolean active);

    boolean isPermanent();

    void setPermanent(boolean permanent);

    boolean isSilent();

    void setSilent(boolean silent);

    boolean isRemovedSilent();

    void setRemovedSilent(boolean removedSilent);

    boolean isLast();

    void setLast(boolean last);

    boolean isIPRelative();

    void setIPRelative(boolean ipRelative);

    boolean isTemporary();

    boolean hasExpired();

    void setAddedOnWebPanel(boolean addedOnWebPanel);
    boolean isAddedOnWebPanel();
    void setWebPanelId(String webPanelId);
    String getWebPanelId();
    void setWebPanelName(String webPanelName);
    String getWebPanelName();
    void setRemovedOnWebPanel(boolean removedOnWebPanel);
    boolean isRemovedOnWebPanel();
    void setRemovedOnWebPanelId(String removedOnWebPanelId);
    String getRemovedOnWebPanelId();
    void setRemovedOnWebPanelName(String removedOnWebPanelId);
    String getRemovedOnWebPanelName();


    long getAddedAt();

    void setAddedAt(long addedAt);

    long getDurationTime();

    void setDurationTime(long durationTime);

    long getWhenRemoved();

    void setWhenRemoved(long whenRemoved);

    String getReason();

    void setReason(String reason);

    String getRemovedBy();

    void setRemovedBy(String removedBy);
    void setRemovedById(UUID removedBy);
    UUID getRemovedById();

    String getEnteredDuration();

    void setEnteredDuration(String enteredDuration);

    String getRemovedFor();

    void setRemovedFor(String removedFor);

    String getAddedByName();

    void setAddedByName(String addedByName);

    String getName();

    void setName(String name);

    String getTargetAddress();

    void setTargetAddress(String targetAddress);

    UUID getAddedBy();

    void setAddedBy(UUID addedBy);

    UUID getId();

    void setId(UUID id);

    UUID getTargetId();

    void setTargetId(UUID targetId);

    void save(boolean replace); //Other servers calling this may need to get server with player to reload punishments

    void save();

    default String getNiceDuration() {
        if (isPermanent() || getDurationTime() <= 0L) return "Permanent";

        //return DurationFormatUtils.formatDurationWords(DateUtils.handleParseTime(getEnteredDuration()), true, true);
        long duration = getDurationTime() - getAddedAt();

        return DurationFormatUtils.formatDurationWords(duration, true, true);
    }

    default String  getNiceExpire() {
        if (isPermanent()) return "Never";
        if (hasExpired()) return "Expired";
        if (getDurationTime() == -5L) return "";

        return DateUtils.formatDateDiff(this.getDurationTime());
    }

    long getRemoveTimestamp();

    String getStatusText();
}
