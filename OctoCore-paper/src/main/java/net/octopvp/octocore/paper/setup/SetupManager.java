package net.octopvp.octocore.paper.setup;

import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.*;
import org.bukkit.Material;

public class SetupManager implements Setup {
    Manager[] manager = new Manager[]{
            new AuthManager(),
            new FilterManager(),
            new NickManager(),
            new PlayerManager(),
            new TabManager(),
            new VaultManager(),
            new PluginMsgManager(),
            new DatabaseManager(),
            new ServerManager()
    };
    @Override
    public void setup(OctoCorePaper plugin) {
        LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl",plugin.getConfig().getInt("cooldown.pearl.time"), Material.ENDER_PEARL));
        for (Manager manager1 : manager) {
            manager1.init(plugin);
        }
    }

    @Override
    public void disable(OctoCorePaper plugin) {
        for (Manager manager1 : manager) {
            manager1.disable(plugin);
        }
    }
}
