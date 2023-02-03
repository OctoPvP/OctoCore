package net.octopvp.octocore.master.views.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Getter;
import net.octopvp.octocore.master.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScrollableMessageList extends Scroller {
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
        setContent(vl);
    }

    public static class MessageItemComponent extends Div {
        private VerticalLayout vl = new VerticalLayout();
        private MessageListItem item;
        public MessageItemComponent(MessageListItem item) {
            this.item = item;
            Span name = new Span(item.getUserName() + " ");
            name.getStyle().set("font-weight", "bold");
            Span timestamp = new Span(User.dateFormat.format(new Date(item.getTime().toEpochMilli())));
            Div div = new Div(name, timestamp);
            vl.add(div);
            Span text = new Span(item.getText());
            vl.add(text);

            HorizontalLayout hl = new HorizontalLayout();
            if (item instanceof MinecraftMessageListItem i) {
                hl.add(i.getProfileComponent());
            } else {
                hl.add(new Avatar(item.getUserName(), item.getUserImage()));
            }
            hl.add(vl);
            add(hl);
        }
    }

    public static class MinecraftMessageListItem extends MessageListItem {
        private boolean showOnlineIndicator;
        public MinecraftMessageListItem(String text,
                                        java.time.Instant time,
                                        String userName, boolean showOnlineIndicator) {
            super(text, time, userName);
            this.showOnlineIndicator = showOnlineIndicator;
        }

        public boolean isShowOnlineIndicator() {
            return showOnlineIndicator;
        }
        public Component getProfileComponent() {
            return new PlayerName(getUserName(), showOnlineIndicator);
        }
    }
}
