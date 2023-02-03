package net.octopvp.octocore.master.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vexsoftware.votifier.model.Vote;
import lombok.Getter;
import net.badbird5907.lightning.annotation.EventHandler;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.interfaces.manager.*;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.redis.packets.VotePacket;
import net.octopvp.octocore.master.component.LightningHolder;
import net.octopvp.octocore.master.master.manager.DatabaseManager;
import net.octopvp.octocore.master.master.manager.PunishModule;
import net.octopvp.octocore.master.master.manager.ServerManager;
import net.octopvp.octocore.master.master.object.ServerStatus;
import net.octopvp.octocore.master.master.redis.RedisPackets;
import net.octopvp.octocore.master.master.util.AccountUtil;
import net.octopvp.octocore.master.master.votifier.NuVotifierMaster;
import net.octopvp.octocore.master.master.votifier.VotifierEvent;
import net.octopvp.octocore.master.models.Setting;
import net.octopvp.octocore.master.models.VoteModel;
import net.octopvp.octocore.master.repository.SettingRepository;
import net.octopvp.octocore.master.repository.VotesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.UUID;

@Getter
@Component
public class OctoCoreMaster {
    private static final Logger LOG = LoggerFactory
            .getLogger(OctoCoreMaster.class);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create(); public static Gson getGson() { return GSON; }
    @Getter
    private static RedisManager redisManager;
    @Value("${master.redis.hostname}")
    private String redisHostname;
    @Value("${master.redis.port}")
    private int redisPort;
    @Value("${master.redis.password}")
    private String redisPassword;

    @Autowired
    private ServerManager serverManager;

    @Autowired
    private DatabaseManager databaseManager;

    @Autowired
    private LightningHolder lightningHolder;

    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private AccountUtil accountUtil;

    @Autowired
    private PunishModule punishModule;

    public OctoCoreMaster() {
        LOG.info("Starting Master...");
        OctoCoreCommon.getInstance().init(GSON, new ServerImplementation() {
            @Override
            public void sendMessage(UUID uuid, String message) {
                throw new UnsupportedOperationException("Not Implemented.");
            }

            @Override
            public void sendMessage(String name, String message) {
                throw new UnsupportedOperationException("Not Implemented.");
            }

            @Override
            public void logError(String message, Object... placeholders) {
                LOG.error(StringUtils.replacePlaceholders(message, placeholders));
            }

            @Override
            public void logInfo(String message, Object... placeholders) {
                LOG.info(StringUtils.replacePlaceholders(message, placeholders));
            }

            @Override
            public void logDebug(String message, Object... placeholders) {
                LOG.debug(StringUtils.replacePlaceholders(message, placeholders));
            }

            @Override
            public void logWarn(String message, Object... placeholders) {
                LOG.warn(StringUtils.replacePlaceholders(message, placeholders));
            }

            @Override
            public IServerManager getServerManager() {
                return serverManager;
            }

            @Override
            public ClassLoader getClassLoader() {
                return OctoCoreMaster.class.getClassLoader();
            }

            @Override
            public String getServerName() {
                return "Master";
            }

            @Override
            public String getCommit() {
                return "N/A";
            }

            @Override
            public String getName(UUID uuid) {
                return accountUtil.getName(uuid);
            }

            @Override
            public IRankManager getRankManager() {
                throw new RuntimeException("Not implemented");
            }

            @Override
            public IPunishModule getPunishModule() {
                return punishModule;
            }

            @Override
            public IPlayerManager getPlayerManager() {
                throw new RuntimeException("Not implemented");
            }

            @Override
            public IDatabaseManager getDatabaseManager() {
                return databaseManager;
            }
        });
    }

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    private AccountUtil mojangAPI;

    @PostConstruct
    public void init() {
        LOG.info("Connecting to redis with hostname " + redisHostname + ":" + redisPort);
        redisManager = new RedisManager(
                redisHostname, redisPort, redisPassword, "net.octopvp.octocore.master.master.redis.impl",
                new RedisPackets()
        );

        lightningHolder.getEventBus().register(new Object() {
            @EventHandler
            public void onVote(VotifierEvent event) {
                NuVotifierMaster.getInstance().getScheduler().onPool(() -> {
                    Vote vote = event.getVote();
                    LOG.info("Vote received from " + vote.getUsername() + " on service " + vote.getServiceName() + " on " + vote.getAddress());
                    Optional<Setting> setting = settingRepository.findById("votes");
                    if (setting.isPresent()) {
                        setting.get().setValue(setting.get().getValue() + 1);
                        settingRepository.save(setting.get());
                    } else {
                        Setting newSetting = new Setting("votes", "1");
                        settingRepository.save(newSetting);
                    }
                    UUID uuid = mojangAPI.getUUID(vote.getUsername());
                    votesRepository.save(new VoteModel(vote.getUsername(), uuid, vote.getServiceName()));
                    new VotePacket(uuid).send();
                });
            }
        });
    }

    public ServerStatus getStatus() {
        return ServerStatus.OK;
    }
}
