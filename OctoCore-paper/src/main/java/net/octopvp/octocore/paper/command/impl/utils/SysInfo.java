package net.octopvp.octocore.paper.command.impl.utils;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.permission.Permission;

import java.util.Arrays;
import java.util.List;

public class SysInfo implements BaseCommand {
    @Command(name = "sysinfo",permission = Permission.SYS_INFO,cooldown = 1)
    public CommandResult execute(Sender sender, String[] args) {
        StringBuilder msg = new StringBuilder(CC.SEPARATOR + CC.NL);
        msg.append(CC.GREEN + "OS: " + HardwareUtils.getSystemInfo().getOperatingSystem().getFamily()).append(CC.NL);
        msg.append(CC.GREEN + "OS Family: " + HardwareUtils.getSystemInfo().getOperatingSystem().getFamily()).append(CC.NL);
        msg.append(CC.GREEN + "Hardware Model: " + HardwareUtils.getSystemInfo().getHardware().getComputerSystem().getModel()).append(CC.NL);
        msg.append(CC.GREEN + "Hardware Manufacturer: " + HardwareUtils.getSystemInfo().getHardware().getComputerSystem().getManufacturer()).append(CC.NL);
        msg.append(CC.GREEN + "Available memory: " + HardwareUtils.getSystemInfo().getHardware().getMemory().getAvailable()).append(CC.NL);
        msg.append(CC.GREEN + "Total memory: " + HardwareUtils.getSystemInfo().getHardware().getMemory().getTotal()).append(CC.NL);
        msg.append(CC.GREEN + "Total OS Processes: " + HardwareUtils.getSystemInfo().getOperatingSystem().getProcessCount()).append(CC.NL);
        msg.append(CC.GREEN + "Max Frequency: " + HardwareUtils.getSystemInfo().getHardware().getProcessor().getMaxFreq()).append(CC.NL);
        msg.append(CC.GREEN + "Processor Frequency: " + Arrays.toString(HardwareUtils.getSystemInfo().getHardware().getProcessor().getCurrentFreq())).append(CC.NL);
        msg.append(CC.GREEN + "Processor count: " + HardwareUtils.getSystemInfo().getHardware().getProcessor().getPhysicalProcessorCount()).append(CC.NL).append(CC.GREEN + "Do /sysinfo gui to see more").append(CC.NL + CC.SEPARATOR);

        sender.sendMessage(msg.toString());
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
