package net.octopvp.octocore.paper.hooks;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;

public class ProtocolLibHook implements Hook{
    @Getter
    private static ProtocolManager protocolManager;
    @Override
    public void onEnable() {
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {

    }
}
