package net.octopvp.octocore.master.payload.response;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class UserInfoResponse {
    private boolean success = true;
    private String id, username, token;
    private List<String> roles;
}
