package net.octopvp.octocore.master.master;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vexsoftware.votifier.model.Vote;
import lombok.Getter;
import net.badbird5907.lightning.annotation.EventHandler;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.ServerImplementation;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.manager.IServerManager;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.redis.packets.VotePacket;
import net.octopvp.octocore.master.component.LightningHolder;
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

    @Getter
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private static RedisManager redisManager;
    @Value("${master.redis.hostname}")
    private String hostname;
    @Value("${master.redis.port}")
    private int port;
    @Value("${master.redis.password}")
    private String password;

    @Autowired
    private ServerManager serverManager;

    @Autowired
    private LightningHolder lightningHolder;

    @Autowired
    private SettingRepository settingRepository;

    public OctoCoreMaster() {
        LOG.info("Starting Master...");
        OctoCoreCommon.getInstance().init(GSON, new ServerImplementation() {
            @Override
            public void sendMessage(UUID uuid, String message) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendMessage(String name, String message) {
                throw new UnsupportedOperationException();
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
        });
    }

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    private AccountUtil mojangAPI;

    @PostConstruct
    public void init() {
        LOG.info("Connecting to redis with hostname " + hostname + ":" + port);
        OctoCoreCommon.getInstance().setRedisManager(redisManager = new RedisManager(
                hostname, port, password, "net.octopvp.octocore.master.master.redis.impl",
                new RedisPackets()
        ));

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
