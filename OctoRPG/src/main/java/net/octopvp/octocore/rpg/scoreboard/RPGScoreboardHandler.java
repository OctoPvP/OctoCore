package net.octopvp.octocore.rpg.scoreboard;

import fr.mrmicky.fastboard.FastBoardBase;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RPGScoreboardHandler implements ScoreboardHandler<Component> {
    public static final Component SCOREBOARD_SEPERATOR = Component.text("--------------------", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH),
            SCOREBOARD_IP_SEPERATOR = Component.text("---", NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH);
    
    private final String serverIp = "rpg_dev"; // Changed from dev1 to rpg_dev as requested

    @Override
    public Component getTitle(Player player, FastBoardBase<Component> board) {
        String title = OctoCore.getInstance().getConfig().getString("scoreboard.title", "<gradient:aqua:blue>Aetheria</gradient>");
        return MiniMessage.miniMessage().deserialize(title)
                .append(Component.text(" | ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD, false))
                .append(Component.text("rpg_dev", NamedTextColor.WHITE));
    }

    private boolean state = false;

    @Override
    public List<Component> getEntries(Player player, FastBoardBase<Component> board) {
        state = !state;
        List<Component> componentList = new ArrayList<>();
        RPGPlayerData rpgData = RPGPlayerManager.getInstance().getData(player);
        
        componentList.add(SCOREBOARD_SEPERATOR);
        componentList.add(Component.empty());

        // Current Quest
        String questDisplay = "None";
        if (rpgData != null && !rpgData.getInProgressQuests().isEmpty()) {
            // Just get the first one for now as a simple display
            Map.Entry<Long, Long> entry = rpgData.getInProgressQuests().entrySet().iterator().next();
            questDisplay = "Quest #" + entry.getKey();
        }
        
        componentList.add(Component.text("Quest", NamedTextColor.AQUA)
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(questDisplay, NamedTextColor.GREEN)));
        
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
