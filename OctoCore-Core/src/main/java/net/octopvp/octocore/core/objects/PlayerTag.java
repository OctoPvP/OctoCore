package net.octopvp.octocore.core.objects;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.TagManager;
import org.bukkit.Material;

import java.util.UUID;

@Getter
@Setter
public class PlayerTag {
    private String name, tag, description;
    private UUID id;
    private Material material = Material.NAME_TAG;

    public PlayerTag(String name, String tag, String desc) {
        this.name = name;
        this.tag = tag;
        this.description = desc;
        this.id = UUID.randomUUID();
    }

    public PlayerTag setMaterial(Material m) {
        this.material = m;
        return this;
    }

    public PlayerTagBuilder toBuilder() {
        return new PlayerTagBuilder(this);
    }

    public void save() {
        TagManager.saveTag(this);
    }

    @Override
    public String toString() {
        return OctoCore.getGson().toJson(this);
    }

    public Component getTagComponent() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(tag);
    }
}
