package net.octopvp.octocore.core.manager.impl;

import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.setup.SetupVault;

public class VaultManager extends Manager {
    private static boolean isChatHookEnabled;
    private static Chat chat = null;

    public static boolean isChatHookEnabled() {
        return VaultManager.isChatHookEnabled;
    }

    public static void setChatHookEnabled(boolean isChatHookEnabled) {
        VaultManager.isChatHookEnabled = isChatHookEnabled;
    }

    public static Chat getChat() {
        return VaultManager.chat;
    }

    public static void setChat(Chat chat) {
        VaultManager.chat = chat;
    }

    @Override
    public void init(OctoCore plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            Logger.info("Vault not found, disabling VaultManager...");
            return;
        }
        new SetupVault().setup(plugin);
    }

    @Override
    public void disable() {

    }
}
