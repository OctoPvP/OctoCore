package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.command.impl.GetUUID;
import net.octopvp.octocore.paper.command.impl.IntentionalError;
import net.octopvp.octocore.paper.command.impl.TestCommand;
import net.octopvp.octocore.paper.command.impl.essentials.ListCommand;
import net.octopvp.octocore.paper.command.impl.essentials.PingCommand;
import net.octopvp.octocore.paper.command.impl.raffle.RaffleCommand;
import net.octopvp.octocore.paper.command.impl.staff.NickCommand;
import net.octopvp.octocore.paper.command.impl.staff.UnNickCommand;
import net.octopvp.octocore.paper.command.impl.staff.troll.TrollCommand;
import net.octopvp.octocore.paper.command.impl.utils.Debug;
import net.octopvp.octocore.paper.command.impl.utils.SysInfo;

import java.util.ArrayList;

public class SetupCommands implements Setup{
    public static ArrayList<Object> commands = new ArrayList<>();
    public void setup(OctoCore plugin) {
        CommandFramework cmd = OctoCore.getCommandFramework();

        commands.add(new TestCommand());
        commands.add(new NickCommand());
        commands.add(new UnNickCommand());
        commands.add(new GetUUID());
        commands.add(new SysInfo());
        commands.add(new Debug());
        commands.add(new TrollCommand());
        commands.add(new IntentionalError());
        commands.add(new RaffleCommand());
        commands.add(new ListCommand());
        commands.add(new PingCommand());

        //ClassUtils.getClassesInPackage(OctoCorePaper.getInstance(),"net.octopvp.octocore.paper.command.impl").forEach(cmd::registerCommands);
        commands.forEach(cmd::registerCommands);
        //CommandManager.registerCommand("test", new TestCommand("test", true));
        //setupExecutor(plugin);
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}