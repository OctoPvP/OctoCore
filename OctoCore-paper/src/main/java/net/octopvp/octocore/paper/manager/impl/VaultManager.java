package net.octopvp.octocore.paper.manager.impl;

import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.setup.SetupVault;

public class VaultManager extends Manager {
    private static boolean isChatHookEnabled;
    private static Chat chat = null;

    public static boolean isChatHookEnabled() {
        return VaultManager.isChatHookEnabled;
    }

    public static Chat getChat() {
        return VaultManager.chat;
    }

    public static void setChatHookEnabled(boolean isChatHookEnabled) {
        VaultManager.isChatHookEnabled = isChatHookEnabled;
    }

    public static void setChat(Chat chat) {
        VaultManager.chat = chat;
    }

    @Override
    public void init(OctoCore plugin) {
        new SetupVault().setup(plugin);
    }

    @Override
    public void disable() {

    }
}
