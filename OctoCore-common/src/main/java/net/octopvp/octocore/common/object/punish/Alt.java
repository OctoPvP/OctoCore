package net.octopvp.octocore.common.object.punish;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.OctoCoreCommon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Alt implements IPunishData {

    private final UUID uniqueId;
    private final String name;
    private final transient IPunishData punishData;

    private String displayName;

    public String getDisplayName() {
        return this.getNameColor() + this.name;
    }

    public Alt updateDisplayName() {
        this.displayName = this.getNameColor() + this.name;
        return this;
    }

    public ChatColor getNameColor() {
        if (punishData != null && punishData.isBlacklisted()) return ChatColor.DARK_RED;
        if (punishData != null && punishData.isBanned()) return ChatColor.DARK_RED;

        if (OctoCoreCommon.getInfo().isOnline(uniqueId)) {
            return ChatColor.RED;
        } else {
            return ChatColor.GREEN;
        }
    }

    @Override
    public Collection<IPunishment> getPunishments() {
        return punishData.getPunishments();
    }

    @Override
    public Collection<Alt> getAlts() {
        return punishData.getAlts();
    }

    @Override
    public String getAddress() {
        return punishData.getAddress();
    }

    @Override
    public boolean isBanned() {
        return punishData != null && punishData.isBanned();
    }

    @Override
    public boolean isBlacklisted() {
        return punishData != null && punishData.isBlacklisted();
    }

    @Override
    public boolean isWarned() {
        return punishData != null && punishData.isWarned();
    }

    @Override
    public IPunishment getActiveBan() {
        return punishData != null ? punishData.getActiveBan() : null;
    }

    @Override
    public IPunishment getActiveMute() {
        return punishData != null ? punishData.getActiveMute() : null;
    }

    @Override
    public IPunishment getActiveBlacklist() {
        return punishData != null ? punishData.getActiveBlacklist() : null;
    }

    @Override
    public List<IPunishment> getPunishments(PunishmentType type) {
        return punishData != null ? punishData.getPunishments(type) : new ArrayList<>();
    }

    @Override
    public boolean isMuted() {
        return punishData != null && punishData.isMuted();
    }

    @Override
    public boolean isIPMuted() {
        return punishData != null && punishData.isIPMuted();
    }

    @Override
    public boolean isIPBanned() {
        return punishData != null && punishData.isIPBanned();
    }
}

