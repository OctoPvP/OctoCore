package net.octopvp.octocore.core.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.core.command.CommandResult;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class SysInfoCommand {
    @Command(name = "sysinfo")
    @Permission(Permissions.SYS_INFO)
    public CommandResult execute(CommandSender sender) {
        String msg = CC.SEPARATOR + CC.NL + CC.GREEN + "OS: " + Utilities.getSystemInfo().getOperatingSystem().getFamily() + CC.NL +
                CC.GREEN + "OS Family: " + Utilities.getSystemInfo().getOperatingSystem().getFamily() + CC.NL +
                CC.GREEN + "Hardware Model: " + Utilities.getSystemInfo().getHardware().getComputerSystem().getModel() + CC.NL +
                CC.GREEN + "Hardware Manufacturer: " + Utilities.getSystemInfo().getHardware().getComputerSystem().getManufacturer() + CC.NL +
                CC.GREEN + "Available memory: " + Utilities.getSystemInfo().getHardware().getMemory().getAvailable() + CC.NL +
                CC.GREEN + "Total memory: " + Utilities.getSystemInfo().getHardware().getMemory().getTotal() + CC.NL +
                CC.GREEN + "Total OS Processes: " + Utilities.getSystemInfo().getOperatingSystem().getProcessCount() + CC.NL +
                CC.GREEN + "Max Frequency: " + Utilities.getSystemInfo().getHardware().getProcessor().getMaxFreq() + CC.NL +
                CC.GREEN + "Processor Frequency: " + Arrays.toString(Utilities.getSystemInfo().getHardware().getProcessor().getCurrentFreq()) + CC.NL +
                CC.GREEN + "Processor count: " + Utilities.getSystemInfo().getHardware().getProcessor().getPhysicalProcessorCount() + CC.NL + CC.GREEN + "Do /sysinfo gui to see more" + CC.NL + CC.SEPARATOR;

        sender.sendMessage(msg);
        return CommandResult.SUCCESS;
    }
}
