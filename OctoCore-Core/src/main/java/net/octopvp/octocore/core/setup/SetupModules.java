package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.Module;
import net.octopvp.octocore.core.module.impl.auth.AuthModule;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardModule;

public class SetupModules implements Setup {
    Module[] modules = new Module[]{new AuthModule(), new PunishModule(), new ScoreboardModule()};

    @Override
    public void setup(OctoCore plugin) {
        for (Module module : modules) {
            module.onEnable(plugin);
        }
    }

    @Override
    public void disable(OctoCore plugin) {
        for (Module module : modules) {
            module.onDisable(plugin);
        }
    }
}
