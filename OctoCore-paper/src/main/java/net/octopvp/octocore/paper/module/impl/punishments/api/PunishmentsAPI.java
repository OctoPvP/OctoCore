package net.octopvp.octocore.paper.module.impl.punishments.api;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PunishmentsAPI {
    private final OctoCore plugin = OctoCore.getInstance();


    public boolean isUserLoaded(UUID uuid) {
        return getPlayerData(uuid) != null;
    }

    public PunishPlayerData loadUser(UUID uuid, String name) throws UnexpectedException {
        if (isUserLoaded(uuid)) {
            throw new UnexpectedException("User is already loaded!");
        }
        PunishModule.getInstance().getProfileManager().createPlayerData(uuid, name);
        PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(uuid).load();
        PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(uuid).getPunishData().load();
        return getPlayerData(uuid);
    }

    public PunishPlayerData getPlayerData(UUID uuid) {
        return PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(uuid);
    }

    public boolean isBanned(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isBanned();
    }

    public boolean isIPBanned(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isIPBanned();
    }

    public boolean isMuted(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isMuted();
    }

    public boolean isBlacklisted(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isBlacklisted();
    }

    public Set<Punishment> getPunishments(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().getPunishments();
    }

    public Set<Punishment> getPunishments(UUID uuid, PunishmentType punishmentType) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == punishmentType).collect(Collectors.toSet());
    }

    public List<Alt> getAlts(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAlts();
    }

    public List<Alt> getPotentialAlts(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPotentialAlts();
    }

    public String getAddress(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAddress();
    }

    public List<String> getAddresses(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAddresses();
    }
}
