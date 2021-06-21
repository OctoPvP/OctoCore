package net.octopvp.octocore.paper.manager;

import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.Disable;
import net.octopvp.octocore.paper.setup.SetupManager;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Manager {
    OctoCore plugin = OctoCore.getInstance();
    @Getter
    private static FileConfiguration config = OctoCore.getInstance().getConfig();
    public abstract void init(OctoCore plugin);
    public abstract void disable();
    public Manager(){
        if (this.getClass().isAnnotationPresent(Disable.class))
            return;
        this.init(plugin);
        //SetupManager.instance.getManagers().add(this);
    }
}
