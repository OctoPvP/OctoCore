package net.octopvp.octocore.common.object.punish;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPunishData {
    Collection<IPunishment> getPunishments();

    Collection<Alt> getAlts();

    String getName();

    UUID getUniqueId();

    String getAddress();

    boolean isBanned();

    boolean isIPBanned();

    boolean isMuted();

    boolean isIPMuted();

    boolean isBlacklisted();

    boolean isWarned();

    IPunishment getActiveBan();

    IPunishment getActiveMute();

    IPunishment getActiveBlacklist();

    List<IPunishment> getPunishments(PunishmentType type);
}
