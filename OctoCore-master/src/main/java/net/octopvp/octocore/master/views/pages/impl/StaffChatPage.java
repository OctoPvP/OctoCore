package net.octopvp.octocore.master.views.pages.impl;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.object.FixedList;
import net.octopvp.octocore.master.master.manager.StaffChatModule;
import net.octopvp.octocore.master.master.redis.impl.StaffChatPacket;
import net.octopvp.octocore.master.models.User;
import net.octopvp.octocore.master.services.UserService;
import net.octopvp.octocore.master.views.MainLayout;
import net.octopvp.octocore.master.views.components.PlayerName;
import net.octopvp.octocore.master.views.components.ScrollableMessageList;
import net.octopvp.octocore.master.views.pages.Page;
import net.octopvp.octocore.master.views.util.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

@PageTitle("Staff Chat")
@Route(value = "staffchat", layout = MainLayout.class)
@RolesAllowed("USER")
public class StaffChatPage extends Page {
    private Function<StaffChatPacket, Void> updateCallback;

    @Autowired
    private StaffChatModule staffChatModule;
    @Autowired
    private UserService userService;

    private List<MessageListItem> messages = new FixedList<>(100);

    private User user;
    private ScrollableMessageList messageList;
    @Override
    public void init() {
        user = userService.get();
        messageList = new ScrollableMessageList();
        messageList.getStyle().set("width", "100%");
        messageList.getStyle().set("height", "100%");
        messageList.getStyle().set("overflow-y", "auto");
        MessageInput messageInput = new MessageInput();
        messageInput.getStyle().set("width", "100%");
        messageInput.addSubmitListener(event -> {

        });
        for (StaffChatPacket message : staffChatModule.getMessages()) {
            messages.add(getMessageItem(message));
        }
        messageList.setMessages(messages);
        VerticalLayout chatLayout = new VerticalLayout(messageList, messageInput);
        //chatLayout.setHeight("30em");
        chatLayout.setWidth("100%");
        chatLayout.expand(messageList);
        add(chatLayout);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        staffChatModule.getUpdateCallbacks().remove(updateCallback);
        updateCallback = null;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        updateCallback = packet -> {
            UI ui = attachEvent.getUI();
            if (ui == null) {
                NotificationUtils.create("There was an error updating the staff chat, please refresh the page.", NotificationVariant.LUMO_ERROR).open();
                return null;
            }
            ui.access(()-> {
                messages.add(getMessageItem(packet));
                messageList.setMessages(messages);
            });
            return null;
        };
        staffChatModule.getUpdateCallbacks().add(updateCallback);
    }

    public MessageListItem getMessageItem(StaffChatPacket packet) {
        Date date = new Date(packet.getTimestamp());
        TimeZone timeZone = user.getTimeZone();
        Instant instant = date.toInstant().atZone(timeZone.toZoneId()).toInstant();
        String userImage = PlayerName.HEAD_URL + packet.getUuid();
        MessageListItem item = new MessageListItem(packet.getMessage(), instant, packet.getName() + " (" + packet.getServer() + ")", userImage);
        return item;
    }
}
