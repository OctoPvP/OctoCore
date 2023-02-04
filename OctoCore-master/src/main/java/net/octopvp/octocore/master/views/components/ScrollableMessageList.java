package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import net.octopvp.octocore.master.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScrollableMessageList extends Div {
    @Getter
    private List<MessageListItem> messages;

    public ScrollableMessageList() {

    }

    public void addMessage(MessageListItem packet) {
        if (messages == null) messages = new ArrayList<>();
        messages.add(packet);
        update();
    }

    public void setMessages(List<MessageListItem> messages) {
        this.messages = messages;
        update();
    }

    private VerticalLayout vl = new VerticalLayout();

    public void update() {
        vl.removeAll();
        for (MessageListItem message : messages) {
            vl.add(new MessageItemComponent(message));
        }
        removeAll();
        add(vl);
    }

    public void scrollToBottom() {
        List<Component> components = vl.getChildren().toList();
        if (components.size() > 0) {
            Component last = components.get(components.size() - 1);
            last.scrollIntoView();
        }
    }

    public static class MessageItemComponent extends Div {
        private VerticalLayout vl = new VerticalLayout();
        private MessageListItem item;

        public MessageItemComponent(MessageListItem item) {
            this.item = item;
            Span timestamp = new Span(User.dateFormat.format(new Date(item.getTime().toEpochMilli())));
            timestamp.getStyle().set("color", "gray");
            Component avatar;
            String server = null;
            if (item instanceof MinecraftMessageListItem i) {
                avatar = i.getProfileComponent();
                server = i.getServer();
            } else {
                avatar = new Avatar(item.getUserName(), item.getUserImage());
            }
            Span name = new Span(item.getUserName() + (server != null ? " (" + server + ")" : "") + " ");
            name.getStyle().set("font-weight", "bold");
            HorizontalLayout avatarName = new HorizontalLayout(avatar, name, timestamp);
            vl.add(avatarName);
            Span text = new Span(item.getText());
            text.getStyle().set("inline-size", "90%");
            text.getStyle().set("overflow-wrap", "break-word");
            vl.add(text);
            vl.setPadding(false);
            add(vl);
        }
    }

    @Getter
    public static class MinecraftMessageListItem extends MessageListItem {
        private boolean showOnlineIndicator;
        private String server;

        public MinecraftMessageListItem(String text,
                                        String server,
                                        java.time.Instant time,
                                        String userName, boolean showOnlineIndicator) {
            super(text, time, userName);
            this.showOnlineIndicator = showOnlineIndicator;
            this.server = server;
        }

        public Component getProfileComponent() {
            Image image = new Image(PlayerName.HEAD_URL + getUserName(), getUserName() + "'s avatar");
            image.setHeight("32px");
            image.setWidth("32px");
            return image;
        }
    }
}
