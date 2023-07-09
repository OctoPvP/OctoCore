package net.octopvp.octocore.common.interfaces;

import java.io.IOException;
import java.util.Properties;

public interface BuildInfo {
    String getCommitShort();

    String getCommit();

    String getCommitDate();

    String getBranch();

    int getBuildNumber();

    String getBuildDate();

    class DefaultBuildInfo implements BuildInfo {
        private static BuildInfo instance;
        private static final Properties properties = new Properties();

        public DefaultBuildInfo() {
            try {
                properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static BuildInfo get() {
            if (instance == null) {
                instance = new DefaultBuildInfo();
            }
            return instance;
        }

        @Override
        public String getCommitShort() {
            return properties.getProperty("git.commit.id.abbrev");
        }

        @Override
        public String getCommit() {
            return properties.getProperty("git.commit.id");
        }

        @Override
        public String getCommitDate() {
            return properties.getProperty("git.commit.date");
        }

        @Override
        public String getBranch() {
            return properties.getProperty("git.branch");
        }

        @Override
        public int getBuildNumber() {
            return Integer.parseInt(properties.getProperty("build.number"));
        }

        @Override
        public String getBuildDate() {
            return properties.getProperty("build.date");
        }
    }
}
