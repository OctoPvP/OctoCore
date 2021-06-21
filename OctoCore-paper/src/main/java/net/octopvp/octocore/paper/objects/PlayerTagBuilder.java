package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Material;

@Getter
public class PlayerTagBuilder {
    private Material material = Material.NAME_TAG;
    private String tagName = "",desc = "",tag = "";
    public PlayerTagBuilder setTagName(String name){
        this.tagName = name;
        return this;
    }
    public PlayerTagBuilder setTagDesc(String desc){
        this.desc = desc;
        return this;
    }
    public PlayerTagBuilder setTag(String tag){
        this.tag = CC.translate(tag);
        return this;
    }
    public PlayerTagBuilder setMaterial(Material material){
        this.material = material;
        return this;
    }
    public PlayerTag build(){
        return new PlayerTag(tagName,tag,desc).setMaterial(material);
    }
}
