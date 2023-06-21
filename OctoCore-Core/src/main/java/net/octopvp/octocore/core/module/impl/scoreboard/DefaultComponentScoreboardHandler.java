package net.octopvp.octocore.core.module.impl.scoreboard;

import com.beust.jcommander.internal.Lists;
import fr.mrmicky.fastboard.FastBoardBase;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DefaultComponentScoreboardHandler implements ScoreboardHandler<Component> {
    /*

    {
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
                .next(CC.AQUA + "Your Name" + CC.GRAY + ": " + CC.GREEN + name) //Note - this somehow sets the player's nametag -> .next(CC.AQUA + "Your Name: " + CC.GREEN + player.getName())
                .next(CC.AQUA + "Rank" + CC.GRAY + ": " + CC.GREEN + playerData.getCurrentPrefix())
                .next(CC.AQUA + "Online" + CC.GRAY + ": " + CC.GREEN + OctoCore.getInstance().getServerManager().getGlobalPlayers().size())
                .blank()
                .next(CC.SCOREBOARD_SEPARATOR)
                .next(CC.SCOREBOARD_IP_SEPARATOR + (a ? CC.AQUA : CC.GREEN) + " " + OctoCore.getInstance().getConfig().getString("server-ip") + " " + CC.SCOREBOARD_IP_SEPARATOR)
                .build();
    }
}

     */
    private static final Component SCOREBOARD_SEPERATOR = Component.text("--------------------", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH),
            SCOREBOARD_IP_SEPERATOR = Component.text("---", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH);
    private static String serverIp = OctoCore.getInstance().getConfig().getString("server-ip");
    @Override
    public Component getTitle(Player player, FastBoardBase<Component> board) {
        return Component.text("OctoMC", NamedTextColor.AQUA, TextDecoration.BOLD)
                .append(Component.text(" | ", NamedTextColor.GRAY))
                .append(Component.text(OctoCore.getServerType().getName(), NamedTextColor.WHITE));
    }

    boolean state = false;

    @Override
    public List<Component> getEntries(Player player, FastBoardBase<Component> board) {
        state = !state;
        List<Component> componentList = new ArrayList<>();
        PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
        if (playerData == null)
            return Lists.newArrayList();
        String name = playerData.getCurrentColor() + CC.strip(player.getDisplayName()); // TODO - Componentize it
        if (name.endsWith("\u00A7"))
            name = name.substring(0, name.length() - 1);
        Component nameComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(name);

        /*
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
        componentList.add(SCOREBOARD_SEPERATOR);
        componentList.add(Component.empty());
        componentList.add(Component.text("Your Name", NamedTextColor.AQUA).append(Component.text(": ", NamedTextColor.GRAY)).append(nameComponent));
        componentList.add(Component.text("Rank", NamedTextColor.AQUA).append(Component.text(": ", NamedTextColor.GRAY)).append(Component.text(playerData.getCurrentPrefix(), NamedTextColor.GREEN)));
        componentList.add(Component.text("Online", NamedTextColor.AQUA).append(Component.text(": ", NamedTextColor.GRAY)).append(Component.text(OctoCore.getInstance().getServerManager().getGlobalPlayers().size(), NamedTextColor.GREEN)));
        componentList.add(Component.empty());
        componentList.add(SCOREBOARD_SEPERATOR);
        Component ip = Component.empty()
                .append(SCOREBOARD_IP_SEPERATOR)
                .append(Component.space())
                .append(state ?
                        Component.text(serverIp, NamedTextColor.AQUA) :
                        Component.text(serverIp, NamedTextColor.GREEN))
                .append(Component.space())
                .append(SCOREBOARD_IP_SEPERATOR);
        componentList.add(ip);
        return componentList;
    }

    @Override
    public FastBoardBase<Component> instantiateBoard(Player player) {
        return new FastBoard(player);
    }
}
