package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.master.master.manager.PlayerManager;
import net.octopvp.octocore.master.master.manager.ServerManager;
import org.springframework.beans.factory.annotation.Autowired;

public class PlayerName extends HorizontalLayout {

    private String playerName;
    private static final String HEAD_URL = "https://mc-heads.net/avatar/%id%";

    private boolean showOnlineIcon = false;

    private Image image;
    private Div statusCircle;
    public PlayerName(String name) {
        this(name, false);
    }

    public PlayerName(String name, boolean showOnline) {
        this.playerName = name;
        this.showOnlineIcon = showOnline;
        Div div = new Div();
        div.getStyle().set("position", "relative");
        image = new Image(getHeadUrl(), playerName);
        image.setHeight("32px");

        statusCircle = new Div();

        statusCircle.setWidth("10px");
        statusCircle.setHeight("10px");
        statusCircle.getStyle()
                //.set("border", "2px solid #999")
                .set("background-clip", "padding-box")
                .set("border-radius", "50%")
                .set("bottom","5px")
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

        add(div, new Span(name));
    }

    public boolean isOnline() {
        return ServerManager.getInstance().isPlayerOnline(playerName);
    }

    public void updateStatusCircle() {
        statusCircle.getStyle().set("background-color", isOnline() ? "limegreen" : "gray");
    }

    public String getHeadUrl() {
        return HEAD_URL.replace("%id%", playerName);
    }
}
