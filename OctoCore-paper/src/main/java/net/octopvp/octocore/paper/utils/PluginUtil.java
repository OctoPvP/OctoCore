package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;

public class PluginUtil {
    public static boolean loadPlugin(String name1){
        String name = name1;
        if(!name1.endsWith(".jar"))
            name = name1 + ".jar";
        Plugin target = null;
        final File pluginDir = new File("plugins");
        File pluginFile = new File(pluginDir,name);
        if(!pluginFile.isFile()){
            for(File f : pluginDir.listFiles()){
                try {
                    PluginDescriptionFile desc = OctoCore.getInstance().getPluginLoader().getPluginDescription(f);
                    if(desc.getName().equalsIgnoreCase(name1)) {
                        pluginFile = f;
                        break;
                    }
                } catch (InvalidDescriptionException e) {
                    return false;
                }
            }
        }
        try{
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidPluginException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
            return false;
        }
        Logger.debug("Attempting to load plugin: " + name1);
        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);
        return true;
    }
}
