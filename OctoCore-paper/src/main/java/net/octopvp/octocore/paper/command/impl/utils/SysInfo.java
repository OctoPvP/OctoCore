package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.List;

public class SysInfo implements BaseCommand {
    @Command(name = "sysinfo")
    public CommandResult execute(Sender sender, String[] args) {
        StringBuilder msg = new StringBuilder(CC.SEPARATOR + "\n");
        msg.append(CC.GREEN + "OS: " + HardwareUtils.getSystemInfo().getOperatingSystem().getFamily()).append("\n");
        msg.append(CC.GREEN + "Available memory: " + HardwareUtils.getSystemInfo().getHardware().getMemory().getAvailable()).append("\n");
        msg.append(CC.GREEN + "Total memory: " + HardwareUtils.getSystemInfo().getHardware().getMemory().getTotal()).append("\n");
        msg.append(CC.GREEN + "Processor count: " + HardwareUtils.getSystemInfo().getHardware().getProcessor().getPhysicalProcessorCount()).append("\n" + CC.SEPARATOR);
        sender.sendMessage(msg.toString());
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
