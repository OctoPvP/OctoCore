package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WonkyChestCommand {
    @Command(name = "wonkychest")
    public CommandResult execute(Sender sender, String[] args) {
        new WonkyMenu().open(sender);
        return CommandResult.SUCCESS;
    }

    private class WonkyMenu extends Menu {
        public WonkyMenu() {
            setCancel(false);
        }

        @Override
        public List<Button> getButtons(Player player) {
            return new ArrayList<>();
        }

        @Override
        public String getName(Player player) {
            return "lmfao";
        }

        @Override
        public int getInventorySize(List<Button> buttons) {
            return 81;
        }
    }
}
