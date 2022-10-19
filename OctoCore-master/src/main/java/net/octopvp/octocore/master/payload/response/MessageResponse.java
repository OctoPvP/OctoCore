package net.octopvp.octocore.master.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageResponse {
    private boolean success;
    private String message;
}
