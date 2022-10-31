package net.octopvp.octocore.master.views.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationUtils { // https://vaadin.com/docs/latest/components/notification/
    public static Notification create(String text, NotificationVariant variant, Button... buttons) {
        return create(text, variant, Notification.Position.BOTTOM_END, Arrays.asList(buttons));
    }

    public static Notification create(String text, NotificationVariant variant, Notification.Position position, List<Button> buttons) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setPosition(position);

        Icon icon = VaadinIcon.CHECK_CIRCLE.create();
        Div info = new Div(new Text(text));

        List<Component> components = new ArrayList<>();
        components.add(icon);
        components.add(info);
        for (Button button : buttons) {
            button.getStyle().set("margin", "0 0 0 var(--lumo-space-l)");
            components.add(button);
        }
        components.add(createCloseBtn(notification));
        HorizontalLayout layout = new HorizontalLayout(components.toArray(new Component[0]));
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);

        return notification;
    }

    public static Button createCloseBtn(Notification notification) {
        Button closeBtn = new Button(
                VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        return closeBtn;
    }
}
