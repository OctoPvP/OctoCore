package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.utils.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SetupCommands implements Setup {
    public static ArrayList<Object> commands = new ArrayList<>();

    public void setup(OctoCore plugin) {
        CommandFramework cmd = OctoCore.getCommandFramework();

        /*
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
        commands.forEach(cmd::registerCommands);
        CommandManager.registerCommand("test", new TestCommand("test", true));
        setupExecutor(plugin);
         */

        ReflectionUtils.getClassesInPackage(OctoCore.getInstance(), "net.octopvp.octocore.paper.command.impl").forEach(clazz -> {
            if (BaseCommand.class.isAssignableFrom(clazz) && clazz.getSuperclass() == BaseCommand.class) {
                try {
                    Constructor constructor = clazz.getDeclaredConstructor();
                    constructor.newInstance();
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                cmd.registerCommands(clazz);
            }
        });
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
