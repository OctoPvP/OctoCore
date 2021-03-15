package net.octopvp.octocore.common;

import lombok.Getter;
import oshi.SystemInfo;

public class HardwareUtils {
    @Getter
    private static SystemInfo systemInfo;
    public static void init(){
        systemInfo = new SystemInfo();
    }
}
