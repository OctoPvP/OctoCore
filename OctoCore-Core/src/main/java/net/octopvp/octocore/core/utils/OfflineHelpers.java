package net.octopvp.octocore.core.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.util.BedrockUtils;
import net.octopvp.octocore.common.util.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class OfflineHelpers {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class OfflineInfo {
        private String name;
        private UUID uuid;

        public static OfflineInfo from(BedrockUtils.BedrockInfo bedrockInfo) {
            return new OfflineInfo(bedrockInfo.getGamertag(), bedrockInfo.getUUID());
        }

        public boolean isBedrock() {
            return uuid.getMostSignificantBits() == 0;
        }

        public String getDisplayName() {
            return isBedrock() ? BedrockUtils.bedrockPrefix + name : name;
        }

        public String getName() {
            return getDisplayName(); // bedrock players should __always__ have * in front of their name
        }

        public UUID getUniqueId() {
            return uuid;
        }
    }

    @SneakyThrows
    public static UUID getOfflinePlayerUUID(String name) {
        if (name == null) {
            return null;
        }
        if (Bukkit.getPlayer(name) != null) {
            return Bukkit.getPlayer(name).getUniqueId();
        }
        if (name.startsWith(BedrockUtils.bedrockPrefix)) {
            return BedrockUtils.getXUIDAsUUID(name).get();
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null) {
            return null;
        }
        return player.getUniqueId();
    }

    public static OfflineInfo getOfflineInfo(String name) {
        if (name == null) {
            return null;
        }
        if (Utilities.isUUID(name)) {
            return getOfflineInfo(UUID.fromString(name));
        }
        if (name.startsWith(BedrockUtils.bedrockPrefix)) {
            // return new OfflineInfo(name, BedrockUtils.getXUIDAsUUID(name).get());
            BedrockUtils.BedrockInfo bedrockInfo = BedrockUtils.getBedrockInfo(name).join();
            return OfflineInfo.from(bedrockInfo);
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (player == null) {
            return null;
        }
        return new OfflineInfo(player.getName(), player.getUniqueId());
    }

    public static OfflineInfo getOfflineInfo(UUID uuid) {
        if (uuid == null) return null;
        if (BedrockUtils.isBedrockPlayer(uuid)) {
            return OfflineInfo.from(BedrockUtils.getBedrockInfo(uuid.getLeastSignificantBits()).join());
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player == null) {
            return null;
        }
        return new OfflineInfo(player.getName(), player.getUniqueId());
    }
}
