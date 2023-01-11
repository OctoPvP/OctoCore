package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class PlayerName extends HorizontalLayout {
    private String playerName;
    private static final String HEAD_URL = "https://mc-heads.net/avatar/%id%";
    public PlayerName(String name) {
        this.playerName = name;
        add(new Image(getHeadUrl(), playerName),new Span(name));
    }
    public String getHeadUrl() {
        return HEAD_URL.replace("%id%", playerName);
    }
}
