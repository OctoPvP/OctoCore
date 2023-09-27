package net.octopvp.octocore.velocity.objects;

import com.google.gson.Gson;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.object.redis.JedisSettings;

import java.io.File;
import java.nio.file.Files;

@Getter
@Data
public class VelocityConfiguration {
    private String[] motd;
    private JedisSettings redis;
    @Data
    public static class Protocol {
        private boolean enabled;
        private String version;
    }


    @SneakyThrows
    public static VelocityConfiguration load(Gson gson, File configFile) {
        String json = new String(Files.readAllBytes(configFile.toPath()));
        return gson.fromJson(json, VelocityConfiguration.class);
    }

}
