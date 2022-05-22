package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;

import java.util.Arrays;

public class SysInfo {
    @Command(name = "sysinfo", permission = Permission.SYS_INFO, cooldown = 1)
    public CommandResult execute(Sender sender, String[] args) {
        StringBuilder msg = new StringBuilder(CC.SEPARATOR + CC.NL);
        msg.append(CC.GREEN + "OS: " + Utilities.getSystemInfo().getOperatingSystem().getFamily()).append(CC.NL);
        msg.append(CC.GREEN + "OS Family: " + Utilities.getSystemInfo().getOperatingSystem().getFamily()).append(CC.NL);
        msg.append(CC.GREEN + "Hardware Model: " + Utilities.getSystemInfo().getHardware().getComputerSystem().getModel()).append(CC.NL);
        msg.append(CC.GREEN + "Hardware Manufacturer: " + Utilities.getSystemInfo().getHardware().getComputerSystem().getManufacturer()).append(CC.NL);
        msg.append(CC.GREEN + "Available memory: " + Utilities.getSystemInfo().getHardware().getMemory().getAvailable()).append(CC.NL);
        msg.append(CC.GREEN + "Total memory: " + Utilities.getSystemInfo().getHardware().getMemory().getTotal()).append(CC.NL);
        msg.append(CC.GREEN + "Total OS Processes: " + Utilities.getSystemInfo().getOperatingSystem().getProcessCount()).append(CC.NL);
        msg.append(CC.GREEN + "Max Frequency: " + Utilities.getSystemInfo().getHardware().getProcessor().getMaxFreq()).append(CC.NL);
        msg.append(CC.GREEN + "Processor Frequency: " + Arrays.toString(Utilities.getSystemInfo().getHardware().getProcessor().getCurrentFreq())).append(CC.NL);
        msg.append(CC.GREEN + "Processor count: " + Utilities.getSystemInfo().getHardware().getProcessor().getPhysicalProcessorCount()).append(CC.NL).append(CC.GREEN + "Do /sysinfo gui to see more").append(CC.NL + CC.SEPARATOR);

        sender.sendMessage(msg.toString());
        return CommandResult.SUCCESS;
    }


}
