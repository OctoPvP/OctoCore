package net.octopvp.octocore.paper.scoreboard;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.scoreboard.common.EntryBuilder;
import net.octopvp.octocore.paper.module.impl.scoreboard.type.Entry;
import net.octopvp.octocore.paper.module.impl.scoreboard.type.ScoreboardHandler;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.List;

public class DefaultScoreboardHandler implements ScoreboardHandler {
    boolean a = false;
    int i = 0;
    @Override
    public String getTitle(Player player) {
        return CC.AQUA + CC.B + "OctoPvP " + CC.GRAY + CC.SPLITTER + CC.WHITE + " " + OctoCore.getServerType();
    }

    @Override
    public List<Entry> getEntries(Player player) {
        i++;
        if (i == 2){
            i = 0;
            a = !a;
        }
        PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
        return new EntryBuilder()
                .next(CC.SCOREBOARD_SEPARATOR)
                .blank()
                .next(CC.AQUA + "Your Name: " + CC.GREEN + playerData.getCurrentColor() + player.getDisplayName()) //Note - this somehow sets the player's nametag -> .next(CC.AQUA + "Your Name: " + CC.GREEN + player.getName())
                .next(CC.AQUA + "Rank: " + playerData.getCurrentPrefix())
                .next(CC.AQUA + "Online: " + CC.GREEN + OctoCore.getServerManager().getGlobalPlayers().size())
                .blank()
                .next(CC.SCOREBOARD_SEPARATOR)
                .next(CC.SCOREBOARD_IP_SEPARATOR + (a ? CC.AQUA : CC.GREEN) + " " + OctoCore.getInstance().getConfig().getString("server-ip") + " " + CC.SCOREBOARD_IP_SEPARATOR)
                .build();
    }
}
