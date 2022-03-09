package net.octopvp.octocore.common;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

public class GitInfo {
    @Getter
    private static String
            branch,
            commit;

    static {

        try (InputStream input = GitInfo.class.getResourceAsStream("git.properties")) {
            if (input != null) {
                Properties prop = new Properties();

                // load a properties file
                prop.load(input);

                branch = prop.getProperty("branch");
                commit = prop.getProperty("commit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
