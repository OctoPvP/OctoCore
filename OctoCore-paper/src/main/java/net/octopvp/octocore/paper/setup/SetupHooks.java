package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.hooks.CitizensHook;
import net.octopvp.octocore.paper.hooks.Hook;
import net.octopvp.octocore.paper.hooks.ProtocolLibHook;

public class SetupHooks implements Setup{
    private Hook[] hooks = new Hook[]{new CitizensHook(),new ProtocolLibHook()};

    @Override
    public void setup(OctoCore plugin) {
        for (Hook hook : hooks) {
            hook.onEnable();
        }
    }

    @Override
    public void disable(OctoCore plugin) {
        for(Hook hook : hooks){
            hook.onDisable();
        }
    }
}
