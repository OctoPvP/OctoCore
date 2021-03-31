package net.octopvp.octocore.paper.hooks;

import net.octopvp.octocore.common.rank.LuckpermsManager;

public class LuckPermsHook implements Hook{
    @Override
    public void onEnable() {
        LuckpermsManager.init();
    }

    @Override
    public void onDisable() {}
}
