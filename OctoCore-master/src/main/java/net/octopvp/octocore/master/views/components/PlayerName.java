package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import lombok.Getter;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.master.master.manager.ServerManager;

@Getter
public class PlayerName extends HorizontalLayout {

    private final String playerName;
    public static final String HEAD_URL = "https://mc-heads.net/avatar/";

    private boolean showOnlineIcon = false;

    private final Image image;
    private Div statusCircle;

    public PlayerName(String name) {
        this(name, false, -1, false);
    }

    public PlayerName(String name, boolean bedrock) {
        this(name, false, -1, bedrock);
    }

    public PlayerName(String name, boolean showOnlineIcon, boolean bedrock) {
        this(name, showOnlineIcon, -1, bedrock);
    }

    public PlayerName(String name, boolean showOnlineIcon, boolean grid, boolean bedrock) {
        this(name, showOnlineIcon, grid ? 0.35 : -1, bedrock);
    }

    public PlayerName(String name, boolean showOnline, double paddingEm, boolean bedrock) {
        this.playerName = name;
        this.showOnlineIcon = showOnline;
        Div div = new Div();
        div.getStyle().set("position", "relative");
        image = new Image(getHeadUrl(), playerName);
        image.setHeight("32px");

        if (showOnline) {
            statusCircle = new Div();

            statusCircle.setWidth("10px");
            statusCircle.setHeight("10px");
            statusCircle.getStyle()
                    //.set("border", "2px solid #999")
                    .set("background-clip", "padding-box")
                    .set("border-radius", "50%")
                    .set("bottom", "0.7em")
                    .set("right", "0")
                    .set("position", "absolute");
            updateStatusCircle();
            statusCircle.setId("status-icon-" + name);

            GlobalPlayer player = ServerManager.getInstance().getGlobalPlayer(name);

            if (player != null) {
                Tooltip.forComponent(statusCircle)
                        .withText("Online - " + player.getServer());
            } else {
                Tooltip.forComponent(statusCircle)
                        .withText("Offline");
            }

            div.add(image, statusCircle);
        } else {
            div.add(image);
        }
        Span span = new Span(bedrock ? "*" + name : name);
        if (paddingEm > 0) {
            span.getStyle().set("padding-top", paddingEm + "em");
        }
        add(div, span);
    }

    public boolean isOnline() {
        return ServerManager.getInstance().isPlayerOnline(playerName);
    }

    public void updateStatusCircle() {
        statusCircle.getStyle().set("background-color", isOnline() ? "limegreen" : "gray");
    }

    public String getHeadUrl() {
        return HEAD_URL + playerName;
    }
}
