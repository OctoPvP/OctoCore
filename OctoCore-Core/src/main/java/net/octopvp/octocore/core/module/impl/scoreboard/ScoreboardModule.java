package net.octopvp.octocore.core.module.impl.scoreboard;

import fr.mrmicky.fastboard.FastBoardBase;
import fr.mrmicky.fastboard.adventure.FastBoard;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.module.Module;
import net.octopvp.octocore.core.objects.FastBoardWrapper;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public final class ScoreboardModule implements Module, Listener {
    @Getter
    private static ScoreboardModule instance;
    private static final Map<UUID, FastBoardWrapper<?>> scoreboardMap = new ConcurrentHashMap<>();
    private static boolean enableDefault = true;
    @Getter
    @Setter
    private Consumer<Player> joinHandler = player -> {
        if (enableDefault) setPlayerScoreboard(player, OctoCore.getInstance().getServerImplementation().getScoreboardHandler());
    };

    public void setPlayerScoreboard(Player player, ScoreboardHandler<?> handler) {
        FastBoardWrapper<?> existing = scoreboardMap.remove(player.getUniqueId());
        if (existing != null) {
            existing.getFastBoard().delete();
        }
        if (handler == null) return;
        
        FastBoardWrapper<?> board = new FastBoardWrapper<>(handler.instantiateBoard(player), handler);
        scoreboardMap.put(player.getUniqueId(), board);
    }

    @Override
    public void onEnable(OctoCore plugin) {
        instance = this;
        enableDefault = plugin.getConfig().getBoolean("scoreboard.enable-default", true);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Tasks.runTimer(() -> {
            for (FastBoardWrapper<?> wrapper : scoreboardMap.values()) {
                // wrapper.getHandler().update(wrapper.getFastBoard(), wrapper.getPlayer());
                if (wrapper.getFastBoard() instanceof FastBoard) { // Adventure component FB
                    FastBoardBase<Component> adventureBoard = (FastBoardBase<Component>) wrapper.getFastBoard();
                    ScoreboardHandler<Component> handler = (ScoreboardHandler<Component>) wrapper.getHandler();
                    handler.update(adventureBoard, wrapper.getPlayer());
                } else if (wrapper.getFastBoard() instanceof fr.mrmicky.fastboard.FastBoard) { // String FB
                    FastBoardBase<String> stringBoard = (FastBoardBase<String>) wrapper.getFastBoard();
                    ScoreboardHandler<String> handler = (ScoreboardHandler<String>) wrapper.getHandler();
                    handler.update(stringBoard, wrapper.getPlayer());
                } else {
                    Logger.error("Unknown FastBoard type: " + wrapper.getFastBoard().getClass().getName() + " for player " + wrapper.getPlayer().getName());
                }
            }
        }, 0, 20);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoardWrapper<?> board = scoreboardMap.remove(player.getUniqueId());

        if (board != null) {
            board.getFastBoard().delete();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (joinHandler != null) {
            joinHandler.accept(event.getPlayer());
        }
    }

    @Override
    public void onDisable(OctoCore plugin) {
        for (FastBoardWrapper<?> wrapper : scoreboardMap.values()) {
            wrapper.getFastBoard().delete();
        }
        scoreboardMap.clear();
    }
}
