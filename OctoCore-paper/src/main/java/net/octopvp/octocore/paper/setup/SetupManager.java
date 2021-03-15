package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupManager implements Setup {
    Manager[] manager = new Manager[]{new AuthManager(), new FilterManager(), new NickManager(), new PlayerManager(),new TabManager(),new VaultManager()};
    List<Manager> managers = Arrays.asList(manager);
    @Override
    public void setup(OctoCorePaper plugin) {
        managers.forEach(m ->{
            m.init(plugin);
        });
    }
}
