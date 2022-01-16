package net.octopvp.octocore.common;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.ServerInfo;

@Getter
public class OctoCoreCommon {
    private static ServerInfo info;
    public static void init(ServerInfo info) {
        OctoCoreCommon.info = info;
    }
    public static String getServerName() {
        return info.getServerName();
    }
}
