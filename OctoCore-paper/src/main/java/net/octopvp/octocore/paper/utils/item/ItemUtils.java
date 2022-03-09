package net.octopvp.octocore.paper.utils.item;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
public class ItemUtils {
    @Getter
    private static ArrayList<Material> materials = new ArrayList<>();
    @Getter
    private static ArrayList<Material> blocks = new ArrayList<>();
    @Getter
    private static ArrayList<Material> items = new ArrayList<>();
    @Getter
    private static HashMap<String, Material> itemMap = new HashMap<>();

    static {
        for (Material material : Material.values()) {
            materials.add(material);
            itemMap.put(material.name(), material);
            if (material.isBlock())
                blocks.add(material);
        }
    }
}
