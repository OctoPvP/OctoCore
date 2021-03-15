package net.octopvp.octocore.paper.manager;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.setup.SetupVault;

public class VaultManager implements Manager{
    @Getter
    @Setter
    private static boolean isChatHookEnabled;
    @Getter
    @Setter
    private static Chat chat = null;
    @Override
    public void init(OctoCorePaper plugin) {
        new SetupVault().setup(plugin);
    }
}
