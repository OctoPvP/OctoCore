package net.octopvp.octocore.rpg.item.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.BaseRPGItem;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.ItemType;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import net.octopvp.octocore.rpg.object.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BoagItem extends BaseRPGItem {
    @Override
    public String getName() {
        return "Boag";
    }

    @Override
    public String getId() {
        return "BOAG";
    }

    @Override
    public Material getMaterial() {
        return Material.CARROT;
    }

    @Override
    public String getDescription() {
        return "&fEating 50 of these will increase your vitality by 1!";
    }

    @Override
    public Rarity getRarity() {
        return Rarity.MYTHIC;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.FOOD;
    }

    @Override
    public void onConsume(PlayerItemConsumeEvent event, ItemStack item) {
        Player player = event.getPlayer();
        player.sendMessage(CC.translate("&2Ugh! That was disgusting!"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 2, 1));
        
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data != null) {
            data.setBoagsEaten(data.getBoagsEaten() + 1);
            if (data.getBoagsEaten() % 50 == 0) {
                player.sendMessage(CC.translate("&bYou have eaten " + data.getBoagsEaten() + " boags! Your Vitality has been increased by &a1&b!"));
                data.setBaseVitality(data.getBaseVitality() + 1);
                data.update();
            }
        }
    }
}
