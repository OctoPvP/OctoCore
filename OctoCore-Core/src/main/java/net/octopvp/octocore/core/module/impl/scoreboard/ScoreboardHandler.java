package net.octopvp.octocore.core.module.impl.scoreboard;

import fr.mrmicky.fastboard.FastBoardBase;
import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardHandler<T> {
    T getTitle(Player player, FastBoardBase<T> board);

    List<T> getEntries(Player player, FastBoardBase<T> board);

    default void update(FastBoardBase<T> board, Player player) {
        board.updateTitle(getTitle(player, board));
        board.updateLines(getEntries(player, board));
    }

    FastBoardBase<T> instantiateBoard(Player player);
}
