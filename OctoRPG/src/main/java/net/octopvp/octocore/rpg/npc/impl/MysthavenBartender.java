package net.octopvp.octocore.rpg.npc.impl;

import net.octopvp.octocore.rpg.npc.BaseNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MysthavenBartender extends BaseNPC {
    @Override
    public String getName() {
        return "Bartender";
    }

    @Override
    public String getId() {
        return "MYSTHAVEN_BARTENDER";
    }

    @Override
    public EntityType getDisplayEntity() {
        return EntityType.PLAYER;
    }
    
    @Override
    public String getSkinTexture() {
        // https://minesk.in/1f9aa7b176bc47f9b32770a0c0a54004
        return "ewogICJ0aW1lc3RhbXAiIDogMTcwMzMwNDM3ODkyNSwKICAicHJvZmlsZUlkIiA6ICI3NThmYTYzMTAwZDk0MGY3OWViZmVhMjA4ZWE3YjY4NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbGVuTlMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY4MGE4MTEyMmFjM2E4ZjMxZmVjOThiMjBjMDVhNDkyNWQxZjA5YTRmMmM3ZWYyM2MyOTFkZmFjYzQ4YzFlMiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    }

    @Override
    public String getSkinSignature() {
        return "pkqCMqG1e8kSOpoeBqGfGO/oGzNJWoZlOIGA5CW4UAI2rO/inH5xPPj83GXmX/1qBR6EaxqvJHqYchpfGMD3zZr459I17qN2THs4tlgtIEFwSPAXkjidhv3B33qGMlSsiYyBD+CuxWRzzyYaE5rjpDyESnKnY9s7G8tt8+wUuv1hnZFWjxHRJhfYuCTN0jpyGhE2xGDQ0XkqsbGmd7gBVmd5s5rgwpqlGNHFxRDSwNSUYf8FpUnSrkzors8RJsW3uhnudYkGT9nPeKkrkrAC6p1Vz0e6iQtfqVVeJgNPfAfguqVsVtHs6TKemZ6zzLoGkbEmAWsH36S3BaM3+wX5hKVpKV16QZrj3UVGhLyPELdfpfpbBhBHwlYWyWX03d1TWMH73o3CaFQHV3HseEwTvg56ux26Njfg0uWyrB0PS6eU/VeUer0tFe4VeNjdKTVDpEAqr12aT3t2qOkahle6wVzX92O/0Gx+R4IFU3PFZ7aTMACXV44in+C3OvB30JDkJZ13mRCs2EvtdKuwvIZBt2QNryjYP8W38k6D1uCmMuf3fNMeASPg+MjtXN5bpmkDhGaVj4PFFnvP+9DlUKrdT9bOWWPn7yqElndYS0foF2Z+Tyl4eL/ZiAGWVo/K7P3N3rYkZ34HjoZb71QoltUwFSi1NJ4fiOZbyQrJ9anI/W4=";
    }

    @Override
    public List<String> getGossipLines(Player player) {
        List<String> gossip = new ArrayList<>();
        gossip.add("Want a drink?");
        return gossip;
    }
}
