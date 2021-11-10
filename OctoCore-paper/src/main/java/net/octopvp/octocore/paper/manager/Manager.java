package net.octopvp.octocore.paper.manager;

import lombok.Getter;
import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Manager {
    OctoCore plugin = OctoCore.getInstance();
    @Getter
    private static final FileConfiguration config = OctoCore.getInstance().getConfig();
    public abstract void init(OctoCore plugin);
    public abstract void disable();
    boolean disabled = false;
    public Manager(){
        if (this.getClass().isAnnotationPresent(Disable.class)) {
            disabled = true;
            return;
        }
        this.init(plugin);
        //SetupManager.instance.getManagers().add(this);
    }
}
