package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.AuditLogEntry;
import net.octopvp.octocore.paper.objects.AuditLogType;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.jda.Embed;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class JDAManager extends ListenerAdapter implements Manager {
    @Getter
    private JDA jda = null;
    @Getter
    private boolean jdaReady = false;
    @Override
    public void init(OctoCore plugin) {
        if(OctoCore.isMaster()){
            Logger.debug("Setting up JDA");
            try {
                jda = new JDABuilder(OctoCore.getInstance().getConfig().getString("master.discord.token"))
                        .addEventListeners(this)
                        .build();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disable(OctoCore plugin) {
        if(OctoCore.isMaster()){
            Logger.info("Sending message \"Master control - Stopped\" to discord master server logs id:" + OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status"));
            jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status")).sendMessage(Embed.setTimestamp(Embed.red().setTitle("Master Control - Stopped")).build()).queue();
            jda.shutdown();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);
        jdaReady = true;
        Logger.info("Sending message \"Master control - Started\" to discord master server logs id:" + OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status"));
        jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status")).sendMessage(Embed.setTimestamp(Embed.green().setTitle("Master Control - Started")).build()).queue();
    }
    public void sendAuditLogMsg(AuditLogEntry entry){
        EmbedBuilder builder = Embed.setTimestamp(Embed.warn().setTitle(entry.getType()));
        entry.getEntries().keySet().forEach(key -> builder.addField(key,entry.getEntries().get(key),false));
        jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.audit")).sendMessage(builder.build()).queue();
    }
    public static JsonObject serialize(JsonObject in,AuditLogEntry entry){
        in.addProperty("entry",new Gson().toJson(entry));
        return in;
    }
    public static AuditLogEntry deSerialize(JsonObject in){
        return new Gson().fromJson(in,AuditLogEntry.class);
    }
}
