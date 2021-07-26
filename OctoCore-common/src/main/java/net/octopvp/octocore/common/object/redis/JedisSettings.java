package net.octopvp.octocore.common.object.redis;

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

    @Override
    public String toString() {
        return "JedisSettings{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", password='" + password + '\'' +
                ", auth=" + auth +
                '}';
    }
}
