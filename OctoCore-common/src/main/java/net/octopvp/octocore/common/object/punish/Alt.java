package net.octopvp.octocore.common.object.punish;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPlayerData;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.util.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor
public class Alt implements IPunishData {
    private final UUID uniqueId;
    private final String name;
    private final transient IPunishData punishData;

    private String displayName;

    public TextComponent getDisplayName() {
        return Component.text(this.name);
    }

    public Alt updateDisplayName() {
        this.displayName = this.getNameColor() + this.name;
        return this;
    }

    public @NotNull ChatColor getNameColor() {
        if (punishData != null && punishData.isBlacklisted()) return ChatColor.DARK_RED;
        if (punishData != null && punishData.isBanned()) return ChatColor.DARK_RED;

        if (OctoCoreCommon.getInstance().getServerManager().isOnline(uniqueId)) {
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
    public String getLastKnownAddress() {
        return punishData.getLastKnownAddress();
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


    public static List<Alt> removeDuplicates(List<Alt> alts, IPlayerData iPlayerData) {
        alts.removeIf(alt -> alt.getName().equalsIgnoreCase(iPlayerData.getName()) || alt.getUniqueId().equals(iPlayerData.getUniqueId()));
        List<Alt> newAlts = new ArrayList<>();
        alts.forEach(alt -> {
            if (newAlts.stream().filter(current -> current.getName().equalsIgnoreCase(alt.getName())).findFirst().orElse(null) == null) {
                newAlts.add(alt);
            }
        });
        return newAlts;
    }

    @Override
    public UUID getUuid() {
        return uniqueId;
    }
}

