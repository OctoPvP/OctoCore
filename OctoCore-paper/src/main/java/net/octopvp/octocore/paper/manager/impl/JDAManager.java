package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.AuditLogEntry;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.utils.jda.Embed;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class JDAManager extends Manager {
    @Getter
    private static JDAManager instance;
    @Getter
    private static final boolean enabled = getConfig().getBoolean("master.discord.enabled");

    public class ReadyListener extends ListenerAdapter{
        @Override
        public void onReady(@NotNull ReadyEvent event) {
            super.onReady(event);
            jdaReady = true;
            Logger.info("Sending message \"Master control - Started\" to discord master server logs id:" + OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status"));
            jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status")).sendMessage(Embed.setTimestamp(Embed.green().setTitle("Master Control - Started")).build()).queue();
        }
    }
    public class MessageListener extends ListenerAdapter{
        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            super.onMessageReceived(event);
            if(event.isFromGuild()){
                if(!event.getAuthor().isBot()){
                    if(event.getMessage().getChannel().getIdLong() == 808700022879289414l) {
                        JsonObject jsonObject = new JsonObject();
                        Color color = event.getMember().getColor();
                        jsonObject.addProperty("name", StringUtils.fromRGB(color.getRed(),color.getGreen(),color.getBlue()) + event.getAuthor().getName());
                        jsonObject.addProperty("message",event.getMessage().getContentDisplay());
                        jsonObject.addProperty("role",getHighestRole(event.getMember()));
                        jsonObject.addProperty("tag",event.getAuthor().getAsTag());
                        OctoCore.getInstance().getRedisData().write(JedisAction.DISCORD_STAFF_CHAT,jsonObject);
                    }
                    if(event.getMessage().getChannel().getIdLong() == 808700037290786886l) {
                        JsonObject jsonObject = new JsonObject();
                        Color color = event.getMember().getColor();
                        jsonObject.addProperty("name", StringUtils.fromRGB(color.getRed(),color.getGreen(),color.getBlue()) + event.getAuthor().getName());
                        jsonObject.addProperty("message",event.getMessage().getContentDisplay());
                        jsonObject.addProperty("role",getHighestRole(event.getMember()));
                        jsonObject.addProperty("tag",event.getAuthor().getAsTag());
                        OctoCore.getInstance().getRedisData().write(JedisAction.DISCORD_ADMIN_CHAT,jsonObject);
                    }
                }
            }
        }
        public String getHighestRole(Member member){
            List<Role> roles = member.getRoles();
            for (Role role : roles) {
                if(role.getColor() == null)
                    continue;
                else return role.getName();
            }
            return "Unknown";
        }
    }
    @Getter
    @Setter
    private static JDA jda = null;
    @Getter
    private boolean jdaReady = false;
    @Override
    public void init(OctoCore plugin) {
        if(OctoCore.isMaster()){
            if (!enabled)
                return;
            instance = this;
            Logger.debug("Setting up JDA");
            try {
                jda = JDABuilder.createDefault(/*OctoCore.getInstance().getConfig().getString("master.discord.token")*/ "ODM0ODE0MTEzMjg4NjgzNjAy.YIGXOg.wBPa4fZ9d2U_rD-34ddmZNM3THg")
                        .addEventListeners(new ReadyListener(),new MessageListener())
                        .build();
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void disable() {
        if (OctoCore.isMaster()) {
            if (!isEnabled())
                return;
            Logger.info("Sending message \"Master control - Stopped\" to discord master server logs id:" + OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status"));
            jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.master-ctrl-status")).sendMessage(Embed.setTimestamp(Embed.red().setTitle("Master Control - Stopped")).build()).queue();
            jda.shutdown();
        }
    }
    public void sendAuditLogMsg(AuditLogEntry entry){
        if (!isEnabled())
            return;
        EmbedBuilder builder = Embed.setTimestamp(Embed.warn().setTitle(entry.getType()));
        entry.getEntries().keySet().forEach(key -> builder.addField(key,entry.getEntries().get(key),false));
        jda.getTextChannelById(OctoCore.getInstance().getConfig().getString("master.discord.channels.audit")).sendMessage(builder.build()).queue();
    }
    public static JsonObject serialize(JsonObject in,AuditLogEntry entry){
        in.addProperty("entry",OctoCore.getGson().toJson(entry));
        return in;
    }
    public static void sendDiscordSC(String n, String server, String message){
        if (!isEnabled())
            return;
        String name = ChatColor.stripColor(n);
        if(getInstance().isJdaReady()){
            if(JDAManager.getJda() == null)
                Logger.debug("JDA is null!");
            Tasks.run(()-> Objects.requireNonNull(JDAManager.getJda().getTextChannelById(808700022879289414L)).sendMessage("```\n" + name + " (" + server + ") " + CC.ARROW_RIGHT + " " + message + "```").queue());
        }
    }
    public static void sendDiscordAC(String n, String server, String message){
        if (!isEnabled())
            return;
        String name = ChatColor.stripColor(n);
        if(getInstance().isJdaReady()){
            Tasks.run(()-> Objects.requireNonNull(JDAManager.getJda().getTextChannelById(808700037290786886L)).sendMessage("```\n" + name + " (" + server + ") " + CC.ARROW_RIGHT + " " + message + "```").queue());
        }
    }

    public static AuditLogEntry deSerialize(JsonObject in){
        return new Gson().fromJson(in,AuditLogEntry.class);
    }
}
