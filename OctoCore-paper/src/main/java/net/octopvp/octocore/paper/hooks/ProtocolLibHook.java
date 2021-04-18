package net.octopvp.octocore.paper.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class ProtocolLibHook implements Hook{
    private static ProtocolManager protocolManager;

    public static ProtocolManager getProtocolManager() {
        return ProtocolLibHook.protocolManager;
    }

    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {

    }
}
