package net.octopvp.octocore.common;

import oshi.SystemInfo;

public class HardwareUtils {
    private static SystemInfo systemInfo;

    public static void init() {
        systemInfo = new SystemInfo();
    }

    public static SystemInfo getSystemInfo() {
        return HardwareUtils.systemInfo;
    }

    public static long getMaxMemoryAllocated() {
        return JVMUtils.getMaxMemoryAllocated();
    }
}
