package net.octopvp.octocore.paper.module.impl.auth;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.object.HashedAddress;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.module.impl.auth.conversation.SetupPrompt;
import net.octopvp.octocore.paper.objects.AuditLogEntry;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import net.octopvp.octocore.paper.utils.HandleError;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class AuthModule implements Module {
    @Getter
    private static SecretGenerator secretGenerator;
    @Getter
    private static TimeProvider timeProvider;
    @Getter
    private static CodeGenerator codeGenerator;
    @Getter
    private static CodeVerifier verifier;
    @Getter
    private static HashMap<UUID, Integer> triesLeft;
    @Getter
    private static boolean serverAuthEnabled;
    @Getter
    private static HashMap<UUID, String> settingUpAuth = new HashMap<>();

    public static boolean verify(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }

    public static boolean isAuthed(Player player) {
        PlayerData pdata = PlayerManager.getProfile(player.getUniqueId());
        if (pdata == null)
            return true;
        if (pdata.getLastAuthedIp() == null)
            return true;
        if (pdata.isAuthEnabled()) {
            return pdata.getLastAuthedIp().equals(player.getAddress().getHostName());
        } else return true;
    }

    public static boolean has2faEnabled(UUID uuid) {
        return PlayerManager.getProfile(uuid).isAuthEnabled();
    }

    public static void force2fa(Player player) {
        if (isServerAuthEnabled()) {
            PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
            playerData.setLastAuthedIp(new HashedAddress(player.getAddress().getHostString()));
            player.sendMessage(Lang.AUTH_SUCCESS.getMsg());
        }
    }

    public static boolean handle2FARequest(Player player, String code) {
        if (settingUpAuth.containsKey(player.getUniqueId())) {
            if (verify(settingUpAuth.get(player.getUniqueId()), code)) {
                player.sendMessage(Lang.AUTH_SETUP_SUCCESS.getMsg());
                PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
                playerData.setAuthEnabled(true);
                playerData.setAuthSecret(settingUpAuth.get(player.getUniqueId()));
                playerData.setLastAuthedIp(new HashedAddress(player.getAddress().getHostString()));
                settingUpAuth.remove(player.getUniqueId());
                return true;
                //TODO update globalplayer
            }
            player.sendMessage(Lang.AUTH_SETUP_INCORRECT.toString());
            return false;
        }
        if (has2faEnabled(player.getUniqueId()) && !isAuthed(player)) {
            PlayerData pdata = PlayerManager.getProfile(player.getUniqueId());
            if (verify(pdata.getAuthSecret(), code)) {
                pdata.setLastAuthedIp(new HashedAddress(player.getAddress().getHostString()));
                player.sendMessage(Lang.AUTH_SUCCESS.getMsg());
                triesLeft.remove(player.getUniqueId());
                return true;
            } else {
                int left = triesLeft.get(player.getUniqueId()) - 1;
                if (left <= 0) {
                    //TODO admin alert "<staff> failed 2fa"
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban --sender=OctoCore-AuthModule -s " + player.getName() + " 2h Failed 2fa");

                    AuditLogEntry entry = new AuditLogEntry("2fa failed", player.getName(), AuditLogType.AUTH_FAIL);
                    OctoCore.getInstance().getJdaManager().sendAuditLogMsg(entry);
                    triesLeft.remove(player.getUniqueId());
                    return true;
                }
                player.sendMessage(Lang.AUTH_DENIED.getMsg(left));
                //im so smart lol (not really)
                triesLeft.remove(player.getUniqueId());
                triesLeft.put(player.getUniqueId(), left);
                return false;
            }
        }
        return true;
    }

    public static void handleJoin(Player player) {
        Tasks.runLater(() -> {
            PlayerData pdata = PlayerManager.getProfile(player.getUniqueId());
            if (pdata == null)
                return;
            if (!pdata.isAuthEnabled())
                return;
            if (pdata.getLastAuthedIp().equals(player.getAddress().getHostName())) {
                player.sendMessage(Lang.AUTH_NO_NEED_JOIN_SAME_IP.getMsg());
                return;
            } else {
                //TODO admin alert "<staff> joined on new ip"
                player.sendMessage(Lang.PLEASE_AUTH.getMsg());
            }
        }, 10l);
    }

    public static void enableAuth(Player player) {
        String secret = secretGenerator.generate();
        QrData data = new QrData.Builder()
                .label(player.getName())
                .secret(secret)
                .issuer("OctoPvP - OctoCore V" + OctoCore.getInstance().getDescription().getVersion())
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        try {
            new AuthMapManager(player, generator.generate(data)).giveMap();
        } catch (QrGenerationException e) {
            e.printStackTrace();
            ErrorData ed = new ErrorData();
            ed.addDescription("Happened when " + player.getName() + " tried to enable 2fa");
            HandleError.handlePlayerError(ed, player, e, false);
            return;
        }
        TextComponent textComponent = new TextComponent(Lang.AUTH_WAITING.getMsg());
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(secret)}));
        player.sendMessage(textComponent);
        settingUpAuth.put(player.getUniqueId(), secret);
        OctoCore.getConversationFactory().withFirstPrompt(new SetupPrompt()).withLocalEcho(false).buildConversation(player).begin();
    }

    @Override
    public void onEnable(OctoCore plugin) {
        if (OctoCore.getServerType() == ServerType.HUB || OctoCore.getServerType() == ServerType.MASTER) {
            serverAuthEnabled = true;
            //save memory :D (dont need those objects if the server isn't hub or master)
            secretGenerator = new DefaultSecretGenerator();
            timeProvider = new SystemTimeProvider();
            codeGenerator = new DefaultCodeGenerator();
            verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
            triesLeft = new HashMap<>();
            Bukkit.getServer().getPluginManager().registerEvents(new AuthListener(), plugin);
            OctoCore.getCommandFramework().registerCommands(new AuthCommand());
            OctoCore.getCommandFramework().registerCommands(new ForceAuthCommand());
        } else serverAuthEnabled = false;
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }
}
