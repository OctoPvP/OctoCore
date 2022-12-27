package net.octopvp.octocore.core.utils;

import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;

public class PacketUtil {
    @Getter
    @Setter
    private static ProtocolManager protocolManager;
}
