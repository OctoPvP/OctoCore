package net.octopvp.octocore.paper.objects;

import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;

import java.util.Collection;
import java.util.UUID;

public interface IPunishData {
    Collection<Punishment> getPunishments();

    Collection<Alt> getAlts();

    String getName();

    UUID getUniqueId();

    String getAddress();

    boolean isBanned();

    boolean isIPBanned();

    boolean isMuted();

    boolean isBlacklisted();

    boolean isWarned();
}
