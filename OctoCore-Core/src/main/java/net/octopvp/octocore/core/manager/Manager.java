package net.octopvp.octocore.core.manager;

import lombok.Getter;
import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class Manager {
    @Getter
    private static final FileConfiguration config = OctoCore.getInstance().getConfig();
    OctoCore plugin = OctoCore.getInstance();
    boolean disabled = false;

    public Manager() {
        if (this.getClass().isAnnotationPresent(Disable.class)) {
            disabled = true;
            return;
        }
        this.init(plugin);
        //SetupManager.instance.getManagers().add(this);
    }

    public abstract void init(OctoCore plugin);

    public abstract void disable();
}
