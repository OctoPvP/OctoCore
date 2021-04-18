package net.octopvp.octocore.paper.utils.tab;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;

public class NMSTab {
    public Object getField(Object object, String name) throws Exception {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    public void setField(Object packet, String field, Object value) throws Exception {
        Field f = packet.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(packet, value);
    }

    public int getPing(Player p) {
        return (((CraftPlayer)p).getHandle()).ping;
    }

    public void sendTabHF(Player p, String header, String footer) {
        try {
            if (!header.equals(""))
                header = "§0§1§2§3§4§5§6§7§8§9" + header;
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}"));
            setField(packet, "b", IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}"));
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changePing(Player p, String player, int score) {
        try {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
            setField(packet, "a", player);
            setField(packet, "b", "PingTab");
            setField(packet, "c", Integer.valueOf(score));
            setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterPing(Player p) {
        try {
            PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
            setField(packet, "a", "PingTab");
            setField(packet, "b", "ms");
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
            setField(packet, "d", Integer.valueOf(1));
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerPing(Player p) {
        try {
            PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
            setField(packet, "a", "PingTab");
            setField(packet, "b", "PingTab");
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
            setField(packet, "d", Integer.valueOf(0));
            PacketPlayOutScoreboardDisplayObjective packet2 = new PacketPlayOutScoreboardDisplayObjective();
            setField(packet2, "a", Integer.valueOf(0));
            setField(packet2, "b", "PingTab");
            PacketPlayOutScoreboardObjective packet3 = new PacketPlayOutScoreboardObjective();
            setField(packet3, "a", "PingTab");
            setField(packet3, "b", "ms");
            setField(packet3, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
            setField(packet3, "d", Integer.valueOf(2));
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet);
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet2);
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerTeam(Player to, Player member, String team, String prefix, String suffix) {
        if (prefix.length() > 16)
            prefix = prefix.substring(0, 16);
        if (suffix.length() > 16)
            suffix = suffix.substring(0, 16);
        try {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", team);
            setField(packet, "b", team);
            setField(packet, "c", prefix);
            setField(packet, "d", suffix);
            Field playerList = packet.getClass().getDeclaredField("g");
            playerList.setAccessible(true);
            Collection<String> col = (Collection<String>)playerList.get(packet);
            col.add(member.getName());
            setField(packet, "i", Integer.valueOf(69));
            (((CraftPlayer)to).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterTeam(String team) {
        try {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", team);
            setField(packet, "h", Integer.valueOf(1));
            setField(packet, "i", Integer.valueOf(69));
            for (Player all : Bukkit.getOnlinePlayers())
                (((CraftPlayer)all).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterTeam(String team, Player p) {
        try {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", team);
            setField(packet, "h", Integer.valueOf(1));
            setField(packet, "i", Integer.valueOf(69));
            (((CraftPlayer)p).getHandle()).playerConnection.sendPacket((Packet)packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePrefixSuffix(String team, Player member, String prefix, String suffix) {
        try {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (prefix.length() > 16)
                    prefix = prefix.substring(0, 16);
                if (suffix.length() > 16)
                    suffix = suffix.substring(0, 16);
                PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
                setField(packet, "a", team);
                setField(packet, "b", team);
                setField(packet, "c", prefix);
                setField(packet, "d", suffix);
                setField(packet, "h", Integer.valueOf(2));
                setField(packet, "i", Integer.valueOf(69));
                (((CraftPlayer)all).getHandle()).playerConnection.sendPacket((Packet)packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel(Player p) {
        return (((CraftPlayer)p).getHandle()).playerConnection.networkManager.channel;
    }

    public void setPlayerListName(Player p, String name) {
        for (Player all : Bukkit.getOnlinePlayers()) {
            String name2 = name;
            (((CraftPlayer)p).getHandle()).listName = CraftChatMessage.fromString(name2)[0];
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, new EntityPlayer[] { ((CraftPlayer)p).getHandle() });
            (((CraftPlayer)all).getHandle()).playerConnection.sendPacket((Packet)packet);
        }
        (((CraftPlayer)p).getHandle()).listName = CraftChatMessage.fromString(name)[0];
    }

    public Object addPlayerToListFor(Player to, Player nev, String name) {
        String name2 = name;
        (((CraftPlayer)nev).getHandle()).listName = CraftChatMessage.fromString(name2)[0];
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { ((CraftPlayer)nev).getHandle() });
        (((CraftPlayer)nev).getHandle()).listName = CraftChatMessage.fromString(name)[0];
        return packet;
    }

    public Object changePlayerListNameFor(Player to, Player changed, String name) {
        String name2 = name;
        (((CraftPlayer)changed).getHandle()).listName = CraftChatMessage.fromString(name2)[0];
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, new EntityPlayer[] { ((CraftPlayer)changed).getHandle() });
        (((CraftPlayer)changed).getHandle()).listName = CraftChatMessage.fromString(name)[0];
        return packet;
    }
}
