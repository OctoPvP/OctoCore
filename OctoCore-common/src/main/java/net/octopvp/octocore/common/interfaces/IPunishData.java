package net.octopvp.octocore.common.interfaces;

import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishmentType;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface IPunishData extends IPlayerData {
    Collection<IPunishment> getPunishments();

    Collection<Alt> getAlts();

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
