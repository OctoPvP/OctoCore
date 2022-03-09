package net.octopvp.octocore.paper.utils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Config extends YamlConfiguration {
    private final String fileName;

    private final JavaPlugin javaPlugin;

    public Config(JavaPlugin javaPlugin, String fileName) {
        this.javaPlugin = javaPlugin;
        this.fileName = fileName;
        createNewFile();
    }

    private void createNewFile() {
        File folder = this.javaPlugin.getDataFolder();
        try {
            File file = new File(folder, this.fileName);
            if (!file.exists()) {
                if (this.javaPlugin.getResource(this.fileName) != null) {
                    this.javaPlugin.saveResource(this.fileName, false);
                } else {
                    save(file);
                }
            } else {
                load(file);
                save(file);
            }
        } catch (Exception exception) {
        }
    }

    public void save() {
        File folder = this.javaPlugin.getDataFolder();
        try {
            save(new File(folder, this.fileName));
        } catch (Exception exception) {
        }
    }
}
