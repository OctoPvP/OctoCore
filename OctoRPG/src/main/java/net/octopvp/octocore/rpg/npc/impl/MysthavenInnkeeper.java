package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MysthavenInnkeeper extends BaseNPC {
    @Override
    public String getName() {
        return "Innkeeper";
    }

    @Override
    public String getId() {
        return "MYSTHAVEN_INNKEEPER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }
    
    @Override
    public String getSkinTexture() {
        //https://minesk.in/da126d925ebe4aba8846e38401854c18
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwMzMwNTAwNDMxMCwKICAicHJvZmlsZUlkIiA6ICJlN2E1OWU2Yzk1NTY0M2IxYTYxZGI2NzNkNTA3ZjE5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJZSmNhdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNzExMTZkMzQzMTgyNGI3MDM3NmZhMTM1M2Y2Mjc4Y2UwYjQ3MTA5MGQ5ZTRlNGU4ZWM1ZDFjODk2NWJlMzEwIgogICAgfQogIH0KfQ==";
    }

    @Override
    public String getSkinSignature() {
        return "wiHPRqIwGbRdVEAqjSVT91+vwM+yD1gtOjAEf1mzEDbQGkNNUbMtoiyQhqLxdiC3PU2g0JVTBgiYQDTtNuRI7YXFLueLwVo8SbZo48fesWLvuQN8AGSzyzd6Y5H9AIx42aPB//p0HnjX6SpP9dcaICCIzMxIbHvmxNBBkA0ZyRdnAcEgeUPEmygN3ICZdAQmbVeiTmqap0bF0LfiGsONyIFtC4xA47VoaV6hy+vqEaEhFVCfro5Vvz21XFaHeSyeLYbthrB3OYlGVioO1mDQ/eay4xyNu8zFSYLO5a0kRLC2xjM83qkhahg9uK0wdJKfjPdxIWZ1fz8vE6AOSLhblec79+6eNWDP+8jodOpP91J78wDp9IuctbrSpwJhPDexTOxqtXUhduAAzanb3hqYXRZ2LeM4RpCHNIrZHIEF/VFI3Fc1EEZnzKPiQ0+AreRrnUi2aVWtOR9J2yQZi0Uuoa1JjCPtF14r8tYWFf1IhO/E8+pdECuVMwtDvbXRqd3K8m9di68FfzsDUkI5ubXN+4FXWDBqRQYJh8CNAZbdfCXxmMTOsi+hPm37oWLHgwFVesQg15x/WE1tx1NHsWQsGIW/DwoHQ/JYawCTA3C2EpgCLgSJ+xRvozHM4IgrdggjRIE3vnV3mDxq1+7j+NKSR7MN4Sbhwhc7/RpUhcikWI4=";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("Feel free to stay in one of our rooms.");
        return gossip;
    }
}
