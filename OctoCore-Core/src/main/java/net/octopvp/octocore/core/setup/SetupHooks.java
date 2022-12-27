package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.hooks.CitizensHook;
import net.octopvp.octocore.core.hooks.Hook;

public class SetupHooks implements Setup {
    private final Hook[] hooks = new Hook[]{new CitizensHook()};

    @Override
    public void setup(OctoCore plugin) {
        for (Hook hook : hooks) {
            hook.onEnable();
        }
    }

    @Override
    public void disable(OctoCore plugin) {
        for (Hook hook : hooks) {
            hook.onDisable();
        }
    }
}
