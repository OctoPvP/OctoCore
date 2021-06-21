package net.octopvp.octocore.paper.database.redis.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JedisSettings {

    private String address;
    private int port;
    private String password = null;
    private boolean auth;

    public boolean hasPassword() {
        return this.password != null && !this.password.equals("");
    }
}
