package net.octopvp.octocore.common;

public class JVMUtils {
    public static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }

    /**
     * Get the max memory allocated to the jvm.
     *
     * @return The max memory allocated to the jvm in mb
     */
    public static long getMaxMemoryAllocated() {
        return Runtime.getRuntime().maxMemory() / 1024L / 1024L;
    }
}
