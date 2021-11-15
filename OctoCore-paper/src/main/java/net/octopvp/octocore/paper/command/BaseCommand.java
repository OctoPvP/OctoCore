package net.octopvp.octocore.paper.command;

import net.octopvp.octocore.common.object.Disable;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseCommand {
    public BaseCommand(){
        if (this.getClass().isAnnotationPresent(Disable.class))
            return;
        OctoCore.getCommandFramework().registerCommands(this);
    }
    public OctoCore plugin = OctoCore.getInstance();
    public Map<String,SCommand> subCommands = new HashMap<>();
    public abstract CommandResult execute(Sender sender,String[] args);
    public List<String> tabComplete(Sender sender, String[] args){
        return null;
    }
    public void registerSubCommand(String name,SCommand command){
        subCommands.put(name,command);
    }

    private final String usageMessage = "";

    public String getUsageMessage() {
        return usageMessage;
    }

    public void setUsageMessage(String usageMessage) {
    }
    public void sendUsage(Sender sender){
        sender.sendMessage(CC.RED + "Usage: /" + getAnnotation().name() + " " + getAnnotation().usage());
    }
    public Command getAnnotation(){
        return this.getClass().getAnnotation(Command.class);
    }
}
