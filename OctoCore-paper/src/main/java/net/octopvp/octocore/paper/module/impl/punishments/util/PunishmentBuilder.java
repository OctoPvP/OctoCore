package net.octopvp.octocore.paper.module.impl.punishments.util;

import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PunishmentBuilder {
    private final Punishment punishment;

    public PunishmentBuilder(PlayerData data, PunishmentType type) {
        this.punishment = new Punishment(data, type);
    }

    public PunishmentBuilder(OfflinePlayer target, PunishmentType type) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null!");
        }
        PlayerData targetData = PlayerManager.getInstance().getOfflineData(target.getUniqueId());
        if (targetData == null) {
            throw new IllegalArgumentException("Target data cannot be null!");
        }
        this.punishment = new Punishment(targetData, type);
    }

    public PunishmentBuilder(UUID target, PunishmentType type) {
        this(Bukkit.getOfflinePlayer(target), type);
    }

    public PunishmentBuilder setActive(boolean active) {
        this.punishment.setActive(active);
        return this;
    }

    public PunishmentBuilder setPermanent(boolean permanent) {
        this.punishment.setPermanent(permanent);
        return this;
    }

    public PunishmentBuilder setSilent(boolean silent) {
        this.punishment.setSilent(silent);
        return this;
    }

    public PunishmentBuilder setRemovedSilent(boolean silent) {
        this.punishment.setRemovedSilent(silent);
        return this;
    }

    public PunishmentBuilder setLast(boolean last) {
        this.punishment.setLast(last);
        return this;
    }

    public PunishmentBuilder setIPRelative(boolean ip) {
        this.punishment.setIPRelative(ip);
        return this;
    }

    public PunishmentBuilder setAddedAt(long addedAt) {
        this.punishment.setAddedAt(addedAt);
        return this;
    }

    public PunishmentBuilder setDurationTime(long duration) {
        this.punishment.setDurationTime(duration);
        return this;
    }

    public PunishmentBuilder setWhenRemoved(long when) {
        this.punishment.setWhenRemoved(when);
        return this;
    }

    public PunishmentBuilder setReason(String reason) {
        this.punishment.setReason(reason);
        return this;
    }

    public PunishmentBuilder setRemovedBy(String by) {
        this.punishment.setRemovedBy(by);
        return this;
    }

    public PunishmentBuilder setEnteredDuration(String duration) {
        this.punishment.setEnteredDuration(duration);
        return this;
    }

    public PunishmentBuilder setRemovedFor(String f) {
        this.punishment.setRemovedFor(f);
        return this;
    }

    public PunishmentBuilder setAddedByName(String name) {
        this.punishment.setAddedByName(name);
        return this;
    }

    public PunishmentBuilder setName(String name) {
        this.punishment.setName(name);
        return this;
    }

    public PunishmentBuilder setAddedBy(UUID uuid) {
        this.punishment.setAddedBy(uuid);
        return this;
    }

}
