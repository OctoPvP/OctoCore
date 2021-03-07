package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.command.impl.GetUUID;
import net.octopvp.octocore.paper.command.impl.staff.NickCommand;
import net.octopvp.octocore.paper.command.impl.TestCommand;
import net.octopvp.octocore.paper.command.impl.staff.UnNickCommand;

import java.util.ArrayList;

public class SetupCommands implements Setup{
    public static ArrayList<BaseCommand> commands = new ArrayList<>();
    public void setup(OctoCorePaper plugin) {
        CommandFramework cmd = OctoCorePaper.getCommandFramework();
        commands.add(new TestCommand());
        commands.add(new NickCommand());
        commands.add(new UnNickCommand());
        commands.add(new GetUUID());
        commands.forEach(c ->{
            cmd.registerCommands(c);
        });
        //CommandManager.registerCommand("test", new TestCommand("test", true));
        //setupExecutor(plugin);
    }
    public static void setupExecutor(OctoCorePaper plugin){
        /*plugin.getCommand("no").setExecutor(new CommandManager());
        CommandManager.commands.forEach((k,v)->{
            plugin.getCommand(k).setExecutor(new CommandManager());
        });
         */
    }
}