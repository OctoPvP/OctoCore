package net.octopvp.octocore.rpg.npc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseNPC implements NPCType {
    public abstract String getName();

    public abstract String getId();

    @Override
    public String getTypeId() {
        return getId();
    }

    public abstract EntityType getDisplayEntity();

    @Override
    public Optional<String> getTitle() {
        return Optional.empty();
    }

    @Override
    public List<Component> getGossip(Player player) {
        List<String> rawGossip = getGossipLines(player);
        if (rawGossip == null) return null;
        List<Component> components = new ArrayList<>();
        for (String s : rawGossip) {
            components.add(MiniMessage.miniMessage().deserialize(s).colorIfAbsent(NamedTextColor.YELLOW));
        }
        return components;
    }

    public List<String> getGossipLines(Player player) {
        return null;
    }

    @Override
    public DisplayData getDisplayData() {
        if (getDisplayEntity() == EntityType.PLAYER) {
            if (getSkinTexture() != null && getSkinSignature() != null) {
                return new DisplayData(getDisplayEntity(), getSkinTexture(), getSkinSignature());
            } else if (getSkin() != null) {
                return new DisplayData(getDisplayEntity(), getSkin());
            }
        }
        return new DisplayData(getDisplayEntity());
    }

    public String getSkin() {
        return null;
    }

    public String getSkinTexture() {
        return null;
    }

    public String getSkinSignature() {
        return null;
    }

    @Override
    public void onInteract(Player player) {
        afterGossip(player);
    }

    public void afterGossip(Player player) {

    }

    public boolean shouldWatchPlayer() {
        return true;
    }
}
