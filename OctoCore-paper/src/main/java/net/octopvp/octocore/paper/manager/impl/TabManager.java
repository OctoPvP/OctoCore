package net.octopvp.octocore.paper.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.maps.pair.PairMap;
import net.octopvp.octocore.paper.utils.tab.DefaultTabHandler;
import net.octopvp.octocore.paper.utils.tab.TabHandler;
import net.octopvp.octocore.paper.utils.tab.item.TabItem;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class TabManager extends Manager {
    private static String value_color = CC.B + CC.GRAY;
    private static String title_color = CC.B + CC.AQUA;
    //private static Skin skin = Skins.getDot(ChatColor.GRAY);
    private static String header = "";
    private static String footer = "";
    private static HashMap<UUID, TableTabList> tablists = new HashMap<>();
    private static Map<UUID,TabHandler> customHandlers = new HashMap<>();
    @Getter
    @Setter
    private static TabHandler defaultTabHandler = new DefaultTabHandler();
    @Override
    public void init(OctoCore plugin) {
        header = ChatColor.translateAlternateColorCodes('&', OctoCore.getInstance().getConfig().getString("tab.header")).replace("\\n","\n");
        footer = ChatColor.translateAlternateColorCodes('&', OctoCore.getInstance().getConfig().getString("tab.footer").replace("\\n","\n"));
        if(plugin.getConfig().getBoolean("default-tab")){
            Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCore.getInstance(), TabManager::update,0L, OctoCore.getInstance().getConfig().getLong("update-tab-interval"));
        }
    }

    @Override
    public void disable() {

    }

    private static void sendTab(Player p, TabHandler handler) {
        AtomicReference<TableTabList> tab = new AtomicReference<>(tablists.get(p.getUniqueId()));
        if (tab.get() == null) {
            if (OctoCore.getTab().getTabLists().containsKey(p)) {
                OctoCore.getTab().destroyTabList(p); //destroy any existing tab lists to prevent exception
            }
            tab.set(OctoCore.getTab().newTableTabList(p));
            tablists.remove(p.getUniqueId()); //remove any tab
            tablists.put(p.getUniqueId(), tab.get());
        }
        PairMap<Integer, Integer, TabItem> map = handler.getTabItems(p);
        map.forEach((k, v, m) -> tab.get().set(k, v, m));
        String header = handler.getHeader(p).replace("\\n","\n"),footer = handler.getFooter(p).replace("\\n","\n");
        tab.get().setHeaderFooter(header,footer);
    }
    public static void update(){
        PlayerManager.getPlayerProfiles().forEach((uuid,profile)->{
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;
            TabHandler handler = getTabHandler(player);
            if (handler == null)
                return;
            sendTab(player,handler);
        });
    }
    public static void onJoin(Player p){
        if(OctoCore.getInstance().getConfig().getBoolean("default-tab")) {
            if(PlayerManager.getPlayerProfiles().containsKey(p.getUniqueId())){
                sendTab(p,getTabHandler(p));
            }
        }
        /*
        if(OctoCore.getInstance().getConfig().getBoolean("health-display"))
            p.setScoreboard(SetupOther.getScoreboard());
         */
    }
    public static TabHandler getTabHandler(Player p){
        TabHandler tabHandler = defaultTabHandler;
        if (customHandlers.get(p.getUniqueId()) != null)
            tabHandler = customHandlers.get(p.getUniqueId());
        return tabHandler;
    }
    public static void onLeave(Player p) {
        tablists.remove(p.getUniqueId());
        customHandlers.remove(p.getUniqueId());
    }
    public static void setCustomTabHandler(Player player,TabHandler tabHandler){
        customHandlers.put(player.getUniqueId(),tabHandler);
    }
}