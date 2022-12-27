package net.octopvp.octocore.core.objects.scoreboard;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.scoreboard.common.EntryBuilder;
import net.octopvp.octocore.core.module.impl.scoreboard.type.Entry;
import net.octopvp.octocore.core.module.impl.scoreboard.type.ScoreboardHandler;
import net.octopvp.octocore.core.objects.PlayerData;
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
        if (i == 2) {
            i = 0;
            a = !a;
        }
        PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
        if (playerData == null)
            return new EntryBuilder().blank().build();
        String name = playerData.getCurrentColor() + CC.strip(player.getDisplayName());
        if (name.endsWith("\u00A7"))
            name = name.substring(0, name.length() - 1);
        //Logger.debug("Name: " + name);
        return new EntryBuilder()
                .next(CC.SCOREBOARD_SEPARATOR)
                .blank()
                .next(CC.AQUA + "Your Name&7: " + CC.GREEN + name) //Note - this somehow sets the player's nametag -> .next(CC.AQUA + "Your Name: " + CC.GREEN + player.getName())
                .next(CC.AQUA + "Rank&7: " + playerData.getCurrentPrefix())
                .next(CC.AQUA + "Online&7: " + CC.GREEN + OctoCore.getInstance().getServerManager().getGlobalPlayers().size())
                .blank()
                .next(CC.SCOREBOARD_SEPARATOR)
                .next(CC.SCOREBOARD_IP_SEPARATOR + (a ? CC.AQUA : CC.GREEN) + " " + OctoCore.getInstance().getConfig().getString("server-ip") + " " + CC.SCOREBOARD_IP_SEPARATOR)
                .build();
    }
}
