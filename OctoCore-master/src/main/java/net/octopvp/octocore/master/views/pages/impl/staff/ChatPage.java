package net.octopvp.octocore.master.views.pages.impl.staff;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.octopvp.octocore.common.object.FixedList;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.master.master.manager.StaffChatModule;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.components.ScrollableMessageList;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

public abstract class ChatPage extends Page {
    @Autowired
    protected UserService userService;
    protected List<MessageListItem> messages = new FixedList<>(100);

    protected User user;
    protected ScrollableMessageList messageList;
    protected Function<ChatPacket, Void> updateCallback;
    @Autowired
    protected StaffChatModule staffChatModule;

    public abstract void submitChat(String message);

    @Override
    public void init() {
        user = userService.get();

        messageList = new ScrollableMessageList();
        messageList.getStyle().set("width", "100%")
                .set("max-height", "80vh") // TODO: somehow this doesn't work on smaller screens
                .set("overflow-y", "scroll");
        MessageInput messageInput = new MessageInput();
        messageInput.getStyle().set("width", "100%");
        messageInput.addSubmitListener(event -> {
            submitChat(event.getValue());
        });
        for (ChatPacket message : staffChatModule.getMessages().computeIfAbsent(getPacketClass(), aClass -> new FixedList<>(100))) {
            messages.add(getMessageItem(message));
        }
        messageList.setMessages(messages);

        // make it vertically scrollable
        messageList.getStyle()
                .set("overflow-y", "auto")
                .set("height", "100%")
                .set("width", "100%");


        VerticalLayout chatLayout = new VerticalLayout(messageList, messageInput);
        //chatLayout.setHeight("30em");
        chatLayout.setWidth("100%");
        chatLayout.expand(messageList);
        add(chatLayout);
    }


    public MessageListItem getMessageItem(ChatPacket packet) {
        Date date = new Date(packet.getTimestamp());
        TimeZone timeZone = user.getTimeZone();
        Instant instant = date.toInstant().atZone(timeZone.toZoneId()).toInstant();
        if (packet.isWeb()) {
            String userImage = packet.getWebProfilePic();
            return new MessageListItem(packet.getMessage(), instant, packet.getName(), userImage);
        } else {
            return new ScrollableMessageList.MinecraftMessageListItem(packet.getMessage(), packet.getServer(), instant, packet.getName(), false);
        }
    }

    public abstract Class<? extends ChatPacket> getPacketClass();

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        staffChatModule.getUpdateCallbacks().remove(updateCallback);
        updateCallback = null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        updateCallback = packet -> {
            if (packet.getClass() != getPacketClass()) {
                return null;
            }
            UI ui = attachEvent.getUI();
            if (ui == null) {
                NotificationUtils.create("There was an error updating the chat, please refresh the page.", NotificationVariant.LUMO_ERROR).open();
                return null;
            }
            ui.access(() -> {
                messages.add(getMessageItem(packet));
                messageList.setMessages(messages);
                messageList.scrollToBottom();
            });
            return null;
        };
        staffChatModule.getUpdateCallbacks().add(updateCallback);
    }
}
