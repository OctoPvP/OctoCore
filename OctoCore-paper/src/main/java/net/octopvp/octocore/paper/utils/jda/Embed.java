package net.octopvp.octocore.paper.utils.jda;

import net.dv8tion.jda.api.EmbedBuilder;
import net.octopvp.octocore.paper.OctoCore;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class Embed {
    public static final String footer = "OctoCore V." + OctoCore.getInstance().getDescription().getVersion();

    public static EmbedBuilder info() {
        return new EmbedBuilder().setColor(new Color(54, 57, 63).getRGB()).setFooter(footer);
    }

    public static EmbedBuilder warn() {
        return new EmbedBuilder().setColor(Color.ORANGE.getRGB()).setFooter(footer);
    }

    public static EmbedBuilder error() {
        return new EmbedBuilder().setColor(Color.RED.getRGB()).setFooter(footer);
    }

    public static EmbedBuilder red() {
        return new EmbedBuilder().setColor(Color.RED.getRGB()).setFooter(footer);
    }

    public static EmbedBuilder green() {
        return new EmbedBuilder().setColor(new Color(0, 255, 68).getRGB()).setFooter(footer);
    }

    public static EmbedBuilder transparent() {
        return new EmbedBuilder().setColor(new Color(54, 57, 63).getRGB()).setFooter(footer);
    }

    public static EmbedBuilder setTimestamp(EmbedBuilder builder) {
        return builder.setTimestamp(ZonedDateTime.now());
    }

    public TemporalAccessor getTemporalAccessor() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(new Date().toString());
    }
}
