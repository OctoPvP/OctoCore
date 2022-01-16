package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.module.impl.auth.AuthModule;
import net.octopvp.octocore.paper.module.impl.noteblockapi.NoteBlockAPI;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;

public class SetupModules implements Setup{
    Module[] modules = new Module[]{new AuthModule(),new NoteBlockAPI(),new PunishModule()};
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
