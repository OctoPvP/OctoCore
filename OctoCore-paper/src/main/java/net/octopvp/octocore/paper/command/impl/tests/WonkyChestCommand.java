package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WonkyChestCommand {
    @Command(name = "wonkychest")
    @Permission(Permissions.ADMIN)
    @PlayerOnly
    public CommandResult execute(Sender sender) {
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
