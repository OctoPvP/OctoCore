package net.octopvp.octocore.master.views.pages.impl.staff;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.master.master.redis.impl.staff.StaffChatPacket;
import net.octopvp.octocore.master.views.MainLayout;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;
import java.util.function.Function;

@PageTitle("Staff Chat")
@Route(value = "staffchat", layout = MainLayout.class)
@RolesAllowed("USER")
public class StaffChatPage extends ChatPage {

    @Override
    public void submitChat(String message) {
        String name = user.getMinecraftName();
        StaffChatPacket packet = new StaffChatPacket(name, name, "WEB", message, new UUID(0, 0), System.currentTimeMillis());
        packet.setWeb(true);
        packet.setWebProfilePic(user.getProfilePictureURL());
        packet.send();
    }

    @Override
    public Class<? extends ChatPacket> getPacketClass() {
        return StaffChatPacket.class;
    }

}
