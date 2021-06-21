package net.octopvp.octocore.paper.objects.litebans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MuteObject {
    private String player, bannedby, expires, reason;
    private boolean active;
}
