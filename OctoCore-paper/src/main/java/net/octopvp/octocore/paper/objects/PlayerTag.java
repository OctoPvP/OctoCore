package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.TagManager;
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
}
