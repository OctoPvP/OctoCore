package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.hooks.CitizensHook;
import net.octopvp.octocore.core.hooks.Hook;
import net.octopvp.octocore.core.hooks.ProtocolLibHook;

public class SetupHooks implements Setup {
    private final Hook[] hooks = new Hook[]{new CitizensHook(), new ProtocolLibHook()};

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
