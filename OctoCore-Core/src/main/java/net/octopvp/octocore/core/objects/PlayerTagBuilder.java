package net.octopvp.octocore.core.objects;

import lombok.Getter;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Material;

@Getter
public class PlayerTagBuilder {
    private Material material = Material.NAME_TAG;
    private String tagName = "", desc = "", tag = "";
    private PlayerTag base;

    public PlayerTagBuilder() {
    }

    public PlayerTagBuilder(PlayerTag tag) {
        this.material = tag.getMaterial();
        this.tagName = tag.getName();
        this.desc = tag.getDescription();
        this.tag = tag.getTag();
        this.base = tag;
    }

    public PlayerTagBuilder setTagName(String name) {
        this.tagName = name;
        return this;
    }

    public PlayerTagBuilder setTagDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public PlayerTagBuilder setTag(String tag) {
        this.tag = CC.translate(tag);
        return this;
    }

    public PlayerTagBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public PlayerTag getBase() {
        return base;
    }

    public PlayerTag build() {
        if (base != null) {
            base.setName(tagName);
            base.setDescription(desc);
            base.setTag(tag);
            base.setMaterial(material);
            return base;
        } else {
            return new PlayerTag(tagName, tag, desc).setMaterial(material);
        }
    }
}
