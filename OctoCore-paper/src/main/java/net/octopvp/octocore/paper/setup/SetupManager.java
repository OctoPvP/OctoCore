package net.octopvp.octocore.paper.setup;

import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupManager implements Setup {
    Manager[] manager = new Manager[]{
            new AuthManager(),
            new FilterManager(),
            new NickManager(),
            new PlayerManager(),
            new TabManager(),
            new VaultManager(),
            new PluginMsgManager(),
            new DatabaseManager(),};
    List<Manager> managers = Arrays.asList(manager);
    @Override
    public void setup(OctoCorePaper plugin) {
        LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl",plugin.getConfig().getInt("cooldown.pearl.time"), Material.ENDER_PEARL));

        managers.forEach(m ->{
            m.init(plugin);
        });
    }

    @Override
    public void disable(OctoCorePaper plugin) { }
}
