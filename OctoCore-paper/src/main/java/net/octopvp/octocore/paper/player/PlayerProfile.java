package net.octopvp.octocore.paper.player;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import net.octopvp.spigot.knockback.KnockbackProfile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.github.paperspigot.Title;

import java.net.InetSocketAddress;
import java.util.*;

@Getter
@Setter
public class PlayerProfile {
    private UUID uuid;
    private PlayerData data;
    private boolean frozen;
    private long xp;
    long coins;
    private Player player;
    private String lastMessage, nick, prefix, mainColor;
    private boolean nicked;
    private TableTabList tab;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid;
        this.prefix = LuckpermsManager.getPrefix(uuid);
        this.mainColor = LuckpermsManager.getMainColor(uuid);
        this.player = Bukkit.getPlayer(uuid);
    }
}