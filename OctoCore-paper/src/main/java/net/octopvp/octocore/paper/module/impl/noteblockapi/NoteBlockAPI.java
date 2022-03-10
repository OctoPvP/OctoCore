package net.octopvp.octocore.paper.module.impl.noteblockapi;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main class; contains methods for playing and adjusting songs for players
 */
public class NoteBlockAPI implements Module {

    private static final OctoCore plugin = OctoCore.getInstance();
    private static NoteBlockAPI nbsAPI;
    private final Map<UUID, ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer>> playingSongs = new ConcurrentHashMap<UUID, ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer>>();
    private final Map<UUID, Byte> playerVolume = new ConcurrentHashMap<UUID, Byte>();
    private final HashMap<Plugin, Boolean> dependentPlugins = new HashMap<>();
    private boolean disabling = false;

    /**
     * Returns true if a Player is currently receiving a song
     *
     * @param player
     * @return is receiving a song
     */
    public static boolean isReceivingSong(Player player) {
        return isReceivingSong(player.getUniqueId());
    }

    /**
     * Returns true if a Player with specified UUID is currently receiving a song
     *
     * @param uuid
     * @return is receiving a song
     */
    public static boolean isReceivingSong(UUID uuid) {
        ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer> songs = nbsAPI.playingSongs.get(uuid);
        return (songs != null && !songs.isEmpty());
    }

    /**
     * Stops the song for a Player
     *
     * @param player
     */
    public static void stopPlaying(Player player) {
        stopPlaying(player.getUniqueId());
    }

    /**
     * Stops the song for a Player
     *
     * @param uuid
     */
    public static void stopPlaying(UUID uuid) {
        ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer> songs = nbsAPI.playingSongs.get(uuid);
        if (songs == null) {
            return;
        }
        for (net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer songPlayer : songs) {
            songPlayer.removePlayer(uuid);
        }
    }

    /**
     * Sets the volume for a given Player
     *
     * @param player
     * @param volume
     */
    public static void setPlayerVolume(Player player, byte volume) {
        setPlayerVolume(player.getUniqueId(), volume);
    }

    /**
     * Sets the volume for a given Player
     *
     * @param uuid
     * @param volume
     */
    public static void setPlayerVolume(UUID uuid, byte volume) {
        nbsAPI.playerVolume.put(uuid, volume);
    }

    /**
     * Gets the volume for a given Player
     *
     * @param player
     * @return volume (byte)
     */
    public static byte getPlayerVolume(Player player) {
        return getPlayerVolume(player.getUniqueId());
    }

    /**
     * Gets the volume for a given Player
     *
     * @param uuid
     * @return volume (byte)
     */
    public static byte getPlayerVolume(UUID uuid) {
        Byte byteObj = nbsAPI.playerVolume.get(uuid);
        if (byteObj == null) {
            byteObj = 100;
            nbsAPI.playerVolume.put(uuid, byteObj);
        }
        return byteObj;
    }

    public static ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer> getSongPlayersByPlayer(Player player) {
        return getSongPlayersByPlayer(player.getUniqueId());
    }

    public static ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer> getSongPlayersByPlayer(UUID player) {
        return nbsAPI.playingSongs.get(player);
    }

    public static void setSongPlayersByPlayer(Player player, ArrayList<net.octopvp.octocore.paper.module.impl.noteblockapi.songplayer.SongPlayer> songs) {
        setSongPlayersByPlayer(player.getUniqueId(), songs);
    }

    public static void setSongPlayersByPlayer(UUID player, ArrayList<SongPlayer> songs) {
        nbsAPI.playingSongs.put(player, songs);
    }

    public static NoteBlockAPI getAPI() {
        return nbsAPI;
    }

    @Override
    public void onEnable(OctoCore plugin) {
        nbsAPI = this;

        new NoteBlockPlayerMain().onEnable();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Plugin[] plugins = plugin.getServer().getPluginManager().getPlugins();
            Type[] types = new Type[]{PlayerRangeStateChangeEvent.class, SongDestroyingEvent.class, SongEndEvent.class, SongStoppedEvent.class};
            for (Plugin plugin1 : plugins) {
                ArrayList<RegisteredListener> rls = HandlerList.getRegisteredListeners(plugin1);
                for (RegisteredListener rl : rls) {
                    Method[] methods = rl.getListener().getClass().getDeclaredMethods();
                    for (Method m : methods) {
                        Type[] params = m.getParameterTypes();
                        param:
                        for (Type paramType : params) {
                            for (Type type : types) {
                                if (paramType.equals(type)) {
                                    dependentPlugins.put(plugin1, true);
                                    break param;
                                }
                            }
                        }
                    }

                }
            }
        }, 1);
    }

    @Override
    public void onDisable(OctoCore plugin) {
        disabling = true;
        NoteBlockPlayerMain.plugin.onDisable();
    }

    public void doSync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    public void doAsync(Runnable runnable) {
        Tasks.runAsync(runnable);
    }

    public boolean isDisabling() {
        return disabling;
    }

}
