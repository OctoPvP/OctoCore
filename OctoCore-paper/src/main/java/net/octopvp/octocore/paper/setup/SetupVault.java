package net.octopvp.octocore.paper.setup;

import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.VaultManager;
import net.octopvp.octocore.paper.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SetupVault implements Setup{
    @Override
    public void setup(OctoCorePaper plugin) {
        if(Bukkit.getPluginManager().isPluginEnabled("Vault")){
            VaultManager.setChatHookEnabled(setupChat());
            if(VaultManager.isChatHookEnabled())
                Logger.info("Setup Vault Chat hook");
            else Logger.info("Couldn't setup vault chat hook. Defaulting to LuckPerms chat hook.");
        }
    }

    @Override
    public void disable(OctoCorePaper plugin) {

    }

    private boolean setupChat(){
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        VaultManager.setChat(rsp.getProvider());
        return VaultManager.getChat() != null;
    }
}
