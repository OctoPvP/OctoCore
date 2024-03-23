package net.octopvp.octocore.velocity.objects;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.octopvp.octocore.common.object.redis.JedisSettings;
//import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@Getter
@Data
public class VelocityConfiguration {
    private JedisSettings redis = new JedisSettings("localhost", 6379, "password", false);
    
    private HashMap<String, PingConfig> motds = new HashMap<>() {
        {
            put("default", new PingConfig());
        }
    };
    private String defaultMotd = "default";
    private boolean randomMotd = false;

    @Data
    public static class PingConfig {
        private String[] motd = { "<green>Line 1", "<red>Line 2" };
        private Protocol protocol = null;
        private ServerPing.Players players = null;
        private String favicon = "server-icon.png";

        @Data
        public static class Protocol {
            private boolean enabled;
            private String version;
            private int protocol;
        }

        public Protocol getProtocol(VelocityConfiguration config) {
            Protocol protocol = this.protocol;
            if (protocol == null) {
                PingConfig pingConfig = config.getMotds().get(config.getDefaultMotd());
                if (pingConfig == null)
                    return null;
                protocol = pingConfig.getProtocol();
            }
            return protocol;
        }

        @SneakyThrows
        public ServerPing generatePing(ServerPing fallback) {
            Protocol protocol = getProtocol();
            Path faviconPath = new File(favicon).toPath();
            return new ServerPing(
                    protocol == null ? fallback.getVersion()
                            : new ServerPing.Version(
                                    protocol.protocol,
                                    protocol.version),
                    players == null ? fallback.getPlayers().orElse(null) : players,
                    generateMotd(),
                    Files.exists(faviconPath) ? Favicon.create(faviconPath) : null);
        }

        private Component generateMotd() {
            return MiniMessage.miniMessage().deserialize(String.join("\n", motd));
        }
    }

    @SneakyThrows
    public static VelocityConfiguration load(Gson gson, File configFile) {
        String json = new String(Files.readAllBytes(configFile.toPath()));
        return gson.fromJson(json, VelocityConfiguration.class);
    }

}
