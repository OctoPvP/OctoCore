package net.octopvp.octocore.paper.objects.litebans;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class BanObject {
    private String player, bannedby, expires, reason;
    private boolean active;
    private long id,until,time;

    public String getActiveString(){
        return (active == true ? CC.GREEN + "Yes" : CC.RED + "No");
    }
}
