package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class FloatingActionButton extends Div {
    public FloatingActionButton(String icon) {
        super();
        addClassName("fab-container");
        Span span = new Span(icon);
        span.addClassName("fab-icon");
        add(span);
    }
}
