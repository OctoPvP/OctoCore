package net.octopvp.octocore.paper.utils.nametag;

import org.bukkit.entity.Player;

public interface IPacketHandler {
    void sendTabListRemovePacket(Player playerToRemove, Player seer);

    void sendTabListAddPacket(Player playerToAdd, GameProfileWrapper newProfile, Player seer);

    void sendEntityDestroyPacket(Player playerToDestroy, Player seer);

    void sendNamedEntitySpawnPacket(Player playerToSpawn, Player seer);

    void sendEntityEquipmentPacket(Player playerToSpawn, Player seer);

    void sendScoreboardRemovePacket(String playerToRemove, Player seer, String team);

    void sendScoreboardAddPacket(String playerToAdd, Player seer, String team);

    GameProfileWrapper getDefaultPlayerProfile(Player player);

    void shutdown();
}
