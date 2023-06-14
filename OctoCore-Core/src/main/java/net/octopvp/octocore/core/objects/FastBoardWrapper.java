package net.octopvp.octocore.core.objects;

import fr.mrmicky.fastboard.FastBoardBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.core.module.impl.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;

@Getter
@Setter
@AllArgsConstructor
/**
 * A wrapper around {@link FastBoardBase} so that we can use the library without
 * copying the code into our own project for our custom fields.
 * Makes updating easier.
 */
public class FastBoardWrapper<T> {
    private FastBoardBase<?> fastBoard;
    private ScoreboardHandler<?> handler;

    public Player getPlayer() {
        return fastBoard.getPlayer();
    }
}
