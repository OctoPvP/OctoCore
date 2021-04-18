package net.octopvp.octocore.paper.manager;

import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.setup.SetupVault;

public class VaultManager implements Manager{
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
    public void init(OctoCorePaper plugin) {
        new SetupVault().setup(plugin);
    }

    @Override
    public void disable(OctoCorePaper plugin) {

    }
}
