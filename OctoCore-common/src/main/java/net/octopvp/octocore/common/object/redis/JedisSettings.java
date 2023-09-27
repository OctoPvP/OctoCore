package net.octopvp.octocore.common.object.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JedisSettings {

    private String address;
    private int port;
    private String password = null;
    private boolean auth;

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
