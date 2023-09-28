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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@Getter
@Data
public class VelocityConfiguration {
    private JedisSettings redis;
    private HashMap<String, PingConfig> motds;
    private String defaultMotd;
    private boolean randomMotd;

    @Data
    public static class PingConfig {
        private String[] motd;
        private Protocol protocol;
        private ServerPing.Players players;
        private String favicon;

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
                    protocol == null ? fallback.getVersion() :
                            new ServerPing.Version(
                                    protocol.protocol,
                                    protocol.version
                            ),
                    players,
                    generateMotd(),
                    Files.exists(faviconPath) ? Favicon.create(faviconPath) : null
            );
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
