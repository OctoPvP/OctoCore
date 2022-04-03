package net.octopvp.octocore.paper.module.impl.punishments.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.objects.IPunishData;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class Alt implements IPunishData {

    private final UUID uniqueId;
    private final String name;
    private final transient PunishData punishData;

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

        if (ServerManager.getInstance().getGlobalPlayer(this.name) == null) {
            return ChatColor.RED;
        } else {
            return ChatColor.GREEN;
        }
    }

    @Override
    public Collection<Punishment> getPunishments() {
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

