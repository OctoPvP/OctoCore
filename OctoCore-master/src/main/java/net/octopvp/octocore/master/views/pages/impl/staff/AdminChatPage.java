package net.octopvp.octocore.master.views.pages.impl.staff;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.master.master.redis.impl.staff.AdminChatPacket;
import net.octopvp.octocore.master.views.MainLayout;

import java.util.UUID;

@PageTitle("Admin Chat")
@Route(value = "adminchat", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminChatPage extends ChatPage {

    @Override
    public void submitChat(String message) {
        String name = user.getMinecraftName();
        AdminChatPacket packet = new AdminChatPacket(name, name, "WEB", message, new UUID(0, 0), System.currentTimeMillis());
        packet.setWeb(true);
        packet.setWebProfilePic(user.getProfilePictureURL());
        packet.send();
    }

    @Override
    public Class<? extends ChatPacket> getPacketClass() {
        return AdminChatPacket.class;
    }


}
