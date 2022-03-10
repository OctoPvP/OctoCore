package net.octopvp.octocore.paper.utils.tab.item;

import net.octopvp.octocore.paper.utils.tab.util.Skin;
import net.octopvp.octocore.paper.utils.tab.util.Skins;

import java.util.Objects;

/**
 * A tab item with custom text, ping and skin.
 */
public class TextTabItem implements TabItem {
    private String text;
    private int ping;
    private Skin skin;

    private String newText;
    private int newPing;
    private Skin newSkin;

    public TextTabItem(String text) {
        this(text, 1000);
    }

    public TextTabItem(String text, int ping) {
        this(text, ping, Skins.DEFAULT_SKIN);
    }

    public TextTabItem(String text, int ping, Skin skin) {
        this.newText = text;
        this.newPing = ping;
        this.newSkin = skin;
        updateText();
        updatePing();
        updateSkin();
    }

    @Override
    public boolean updateText() {
        boolean update = !Objects.equals(this.text, this.newText);
        this.text = this.newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        boolean update = this.ping != this.newPing;
        this.ping = this.newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        boolean update = !Objects.equals(this.skin, this.newSkin);
        this.skin = this.newSkin;
        return update;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TextTabItem))
            return false;
        TextTabItem other = (TextTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
    }

    public String toString() {
        return "TextTabItem(text=" + this.text + ", ping=" + this.ping + ", skin=" + this.skin + ", newText=" + this.newText + ", newPing=" + this.newPing + ", newSkin=" + this.newSkin + ")";
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.newText = text;
    }

    public int getPing() {
        return this.ping;
    }

    public void setPing(int ping) {
        this.newPing = ping;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public void setSkin(Skin skin) {
        this.newSkin = skin;
    }
}
