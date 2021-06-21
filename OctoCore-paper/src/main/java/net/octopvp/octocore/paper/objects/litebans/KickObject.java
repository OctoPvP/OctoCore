package net.octopvp.octocore.paper.objects.litebans;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KickObject {
    private String player, bannedby, expires, reason;
    private boolean active;
}
