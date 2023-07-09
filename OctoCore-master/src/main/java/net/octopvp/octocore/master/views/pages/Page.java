package net.octopvp.octocore.master.views.pages;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;

public abstract class Page extends VerticalLayout {
    @PostConstruct
    public void preInit() {
        init();
        if (!shouldOverrideURL()) {
            return;
        }
        String path = getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        UI.getCurrent().getPage().executeJs("setTimeout(function() { window.history.pushState({}, '', '" + path + "'); }, 50);"); // TODO fix this with vaadin
    }

    public Page() {
        //preInit();
    }

    public abstract void init();

    public String getPath() {
        Route route = this.getClass().getAnnotation(Route.class);
        if (route == null)
            throw new NullPointerException("Route is null! override the getPath method in " + this.getClass().getName());
        return route.value();
    }

    public boolean shouldOverrideURL() {
        return !(this instanceof HasUrlParameter);
    }
}
