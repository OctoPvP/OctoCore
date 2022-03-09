package net.octopvp.octocore.paper.setup;

import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SetupVault implements Setup {
    @Override
    public void setup(OctoCore plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            VaultManager.setChatHookEnabled(setupChat());
            if (VaultManager.isChatHookEnabled())
                Logger.info("Setup Vault Chat hook");
        }
    }

    @Override
    public void disable(OctoCore plugin) {

    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null)
            return false;

        VaultManager.setChat(rsp.getProvider());
        return VaultManager.getChat() != null;
    }
}
