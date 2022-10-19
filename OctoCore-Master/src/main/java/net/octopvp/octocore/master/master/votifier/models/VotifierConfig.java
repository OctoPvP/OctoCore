package net.octopvp.aetheriacoremaster.master.votifier.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class VotifierConfig {
    /*
    {
  "host": "%ip%",
  "port": 8192,
  "disable-v1-protocol": false,
  "tokens": [
    {
      "default": "%default_token%"
    }
  ]
}
     */
    private String host = "%ip%";
    private String bind = "0.0.0.0";
    private int port = 8192;
    private boolean disablev1 = false;
    private HashMap<String, String> tokens = new HashMap<>();

    private static final File CONFIG_FILE = new File("votifier.json");

    @Getter
    private static boolean firstTime = false;
    public static VotifierConfig load() {
        if (CONFIG_FILE.exists()) {
            try {
                String json = new String(Files.readAllBytes(CONFIG_FILE.toPath()));
                return new Gson().fromJson(json, VotifierConfig.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            firstTime = true;
            VotifierConfig cfg =  new VotifierConfig();
            cfg.save();
            return cfg;
        }
    }

    public void save() {
        String json = AetheriaCoreCommon.getInstance().getGson().toJson(this);
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.createNewFile();
            }
            PrintStream ps = new PrintStream(CONFIG_FILE);
            ps.print(json);
            ps.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
