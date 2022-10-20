package net.octopvp.octocore.master.component.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JetbrainsHubAPI {
    @Value("${jetbrains.hub.url}")
    private String url;

    public String getBaseURL() {
        if (!url.endsWith("/hub")) {
            url += "/hub";
        }
        return url;
    }

    public Set<String> getRoles(String user) {
        return new HashSet<>();
    }

    public String getUserHubID() {
        String url = getBaseURL() + "";
        return url;
    }
}
