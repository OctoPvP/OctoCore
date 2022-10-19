package dev.badbird.launchpad.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private List<String> roles;
    private String preferredDomain, preferredSubdomain;

    public JwtResponse(String token, String id, String username, String email, List<String> roles, String preferredDomain, String preferredSubdomain) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.preferredDomain = preferredDomain;
        this.preferredSubdomain = preferredSubdomain;
    }
}
