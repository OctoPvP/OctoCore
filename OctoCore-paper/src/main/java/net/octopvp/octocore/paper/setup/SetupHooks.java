package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.hooks.CitizensHook;
import net.octopvp.octocore.paper.hooks.Hook;
import net.octopvp.octocore.paper.hooks.LuckPermsHook;
import net.octopvp.octocore.paper.hooks.ProtocolLibHook;

public class SetupHooks implements Setup{
    private Hook[] hooks = new Hook[]{new CitizensHook(),new LuckPermsHook(),new ProtocolLibHook()};

    @Override
    public void setup(OctoCorePaper plugin) {
        for (Hook hook : hooks) {
            hook.onEnable();
        }
    }

    @Override
    public void disable(OctoCorePaper plugin) {
        for(Hook hook : hooks){
            hook.onDisable();
        }
    }
}
