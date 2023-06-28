package net.octopvp.octocore.core.module.impl.scoreboard;

import com.beust.jcommander.internal.Lists;
import fr.mrmicky.fastboard.FastBoard;
import fr.mrmicky.fastboard.FastBoardBase;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DefaultStringScoreboardHandler implements ScoreboardHandler<String> {
    boolean state = false;

    @Override
    public String getTitle(Player player, FastBoardBase<String> board) {
        return CC.AQUA + CC.B + "OctoMC " + CC.GRAY + CC.SPLITTER + CC.WHITE + " " + OctoCore.getServerType();
    }

    @Override
    public List<String> getEntries(Player player, FastBoardBase<String> board) {
        state = !state;
        PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
        if (playerData == null)
            return new ArrayList<>();
        String name = playerData.getCurrentColor() + CC.strip(player.getDisplayName());
        if (name.endsWith("\u00A7"))
            name = name.substring(0, name.length() - 1);
        //Logger.debug("Name: " + name);
        /*
        return new EntryBuilder()
                .next(CC.SCOREBOARD_SEPARATOR)
                .blank()
                .next(CC.AQUA + "Your Name" + CC.GRAY + ": " + CC.GREEN + name) //Note - this somehow sets the player's nametag -> .next(CC.AQUA + "Your Name: " + CC.GREEN + player.getName())
                .next(CC.AQUA + "Rank" + CC.GRAY + ": " + CC.GREEN + playerData.getCurrentPrefix())
                .next(CC.AQUA + "Online" + CC.GRAY + ": " + CC.GREEN + OctoCore.getInstance().getServerManager().getGlobalPlayers().size())
                .blank()
                .next(CC.SCOREBOARD_SEPARATOR)
                .next(CC.SCOREBOARD_IP_SEPARATOR + (a ? CC.AQUA : CC.GREEN) + " " + OctoCore.getInstance().getConfig().getString("server-ip") + " " + CC.SCOREBOARD_IP_SEPARATOR)
                .build();
         */
        List<String> entries = Lists.newArrayList();
        entries.add(CC.SCOREBOARD_SEPARATOR);
        entries.add("");
        entries.add(CC.AQUA + "Your Name" + CC.GRAY + ": " + CC.GREEN + name);
        entries.add(CC.AQUA + "Rank" + CC.GRAY + ": " + CC.GREEN + playerData.getCurrentPrefix());
        entries.add(CC.AQUA + "Online" + CC.GRAY + ": " + CC.GREEN + OctoCore.getInstance().getServerManager().getGlobalPlayers().size());
        entries.add("");
        entries.add(CC.SCOREBOARD_SEPARATOR);
        int maxWidth = 32;
        // entries.add(CC.SCOREBOARD_IP_SEPARATOR + (state ? CC.AQUA : CC.GREEN) + " " + OctoCore.getInstance().getConfig().getString("server-ip") + " " + CC.SCOREBOARD_IP_SEPARATOR);
        String serverIp = OctoCore.getInstance().getConfig().getString("server-ip");
        // center
        int spaces = ((maxWidth - serverIp.length()) / 2);
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < spaces; i++)
            ip.append(" ");
        ip.append((state ? CC.AQUA : CC.GREEN) + serverIp);
        for (int i = 0; i < spaces; i++)
            ip.append(" ");
        entries.add(ip.toString());
        return entries;
    }

    @Override
    public FastBoardBase<String> instantiateBoard(Player player) {
        return new FastBoard(player);
    }
}
