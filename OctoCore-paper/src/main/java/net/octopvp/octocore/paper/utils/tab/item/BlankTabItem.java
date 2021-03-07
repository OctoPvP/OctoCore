package net.octopvp.octocore.paper.utils.tab.item;

import net.octopvp.octocore.paper.utils.tab.util.Skin;
import net.octopvp.octocore.paper.utils.tab.util.Skins;
import lombok.ToString;

/**
 * A blank TextTabItem
 */
@ToString
public class BlankTabItem extends TextTabItem {
    public BlankTabItem(Skin skin) {
        super("", 1000, skin);
    }

    public BlankTabItem() {
        this(Skins.DEFAULT_SKIN);
    }
}
