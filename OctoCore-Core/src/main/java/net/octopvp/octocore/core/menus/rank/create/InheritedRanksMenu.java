package net.octopvp.octocore.core.menus.rank.create;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InheritedRanksMenu extends PaginatedMenu<PaginatedGui> {
    private final Menu<?> previousMenu;
    private final RankBuilder builder;
    private final ReturnableTypeCallback<RankBuilder> callback;
    private final RankBuilder startBuilder;
    private boolean showOnlyInherited = false;
    private boolean changed = false;

    @SneakyThrows
    public InheritedRanksMenu(Menu<?> previousMenu, RankBuilder builder, ReturnableTypeCallback<RankBuilder> callback) {
        this.previousMenu = previousMenu;
        this.builder = builder;
        this.callback = callback;
        this.startBuilder = builder.clone();
    }

    public GuiItem rankButton(Rank rankData) {
        // return ItemBuilder.from(Material.WOOL)
        // .durability((short) (rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())))\
        boolean wouldBeCircular = Rank.wouldHaveCircularInheritance(builder.getRank(), rankData) != null;
        return ItemBuilder.from(((rankData.isDefaultRank()) ? XMaterial.LIME_WOOL : WoolUtils.convertChatColorToWoolMaterial(rankData.getColor())).parseMaterial())
                .name(rankData.getDisplayName())
                .lore(
                        CC.SEPARATOR, CC.AQUA + "Weight" + CC.GRAY + ": " + CC.YELLOW + rankData.getWeight(),
                        CC.AQUA + "Inherited: " + CC.YELLOW + Arrays.toString(rankData.getInheritedRanksName()),
                        CC.AQUA + "Default: " + CC.YELLOW + rankData.isDefaultRank(), CC.AQUA + "Prefix: " + CC.YELLOW + rankData.getPrefix(),
                        CC.AQUA + "Changeable Color: " + CC.YELLOW + rankData.isChangableMainColor(),
                        CC.AQUA + "Purchasable: " + CC.YELLOW + rankData.isPurchasable(),
                        CC.SEPARATOR,
                        CC.YELLOW + (builder.getRank().getInheritedRanks().contains(rankData.getRankId()) ?
                                CC.RED + "Click to remove inherited rank" : (wouldBeCircular ?
                                CC.RED + "Cannot add as inherited rank, due to circular inheritance." :
                                "Click to add inherited rank"
                        ))

                ).asGuiItem(event -> {
                    if (builder.getRank().getInheritedRanks().contains(rankData.getRankId())) {
                        Logger.debug("Removing inherited rank " + rankData.getRankId() + " from " + builder.getRank().getRankId());
                        builder.removeInheritedRank(rankData.getRankId());
                    } else {
                        if (wouldBeCircular) {
                            List<Rank> circular = Rank.findCircularInheritancePre(builder.getRank(), rankData);
                            SoundUtil.playError((Player) event.getWhoClicked());
                            StringBuilder circularBuilder = new StringBuilder();
                            for (Rank rank : circular) {
                                circularBuilder.append(rank.getDisplayName()).append(CC.RED).append(" -> ");
                            }
                            circularBuilder.append(rankData.getDisplayName());
                            event.getWhoClicked().sendMessage(CC.RED + "You cannot add " + rankData.getDisplayName() + CC.RED + " as an inherited rank, as it would create a circular inheritance. (Circular inheritance: " + circularBuilder + CC.RED + ")");
                            return;
                        }
                        Logger.debug("Adding inherited rank " + rankData.getRankId() + " to " + builder.getRank().getRankId());
                        builder.addInheritedRank(rankData.getRankId());
                    }
                    changed = true;
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    open((Player) event.getWhoClicked());
                });
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (Rank rank : RankManager.getRanks().stream().filter(rank -> !rank.getRankId().equals(builder.getRank().getRankId())).toArray(Rank[]::new)) {
            if (showOnlyInherited) {
                if (builder.getRank().getInheritedRanks().contains(rank.getRankId())) items.add(rankButton(rank));
            } else items.add(rankButton(rank));
        }
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated().title("Choose inherited ranks").rows(6).create();
    }

    @Override
    public GuiItem getFilterButton() {
        return ItemBuilder.from(Material.HOPPER).name(CC.GREEN + "Filter").lore(CC.GRAY + "Click to show " + (showOnlyInherited ? "all ranks" : "only inherited ranks")).asGuiItem(event -> {
            showOnlyInherited = !showOnlyInherited;
            open((Player) event.getWhoClicked());
        });
    }

    @Override
    public Menu<?> getBackMenu() {
        return previousMenu;
    }
}
