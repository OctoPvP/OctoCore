package net.octopvp.octocore.master.master.votifier;

import com.vexsoftware.votifier.VoteHandler;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.net.VotifierServerBootstrap;
import com.vexsoftware.votifier.net.VotifierSession;
import com.vexsoftware.votifier.net.protocol.v1crypto.RSAIO;
import com.vexsoftware.votifier.net.protocol.v1crypto.RSAKeygen;
import com.vexsoftware.votifier.platform.LoggingAdapter;
import com.vexsoftware.votifier.platform.VotifierPlugin;
import com.vexsoftware.votifier.platform.scheduler.VotifierScheduler;
import com.vexsoftware.votifier.util.KeyCreator;
import com.vexsoftware.votifier.util.TokenUtil;
import lombok.Getter;
import net.octopvp.aetheriacoremaster.components.LightningHolder;
import net.octopvp.aetheriacoremaster.master.votifier.models.VotifierConfig;
import net.octopvp.aetheriacoremaster.master.votifier.scheduler.MasterScheduler;
import net.octopvp.aetheriacoremaster.repositories.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.Key;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class NuVotifierMaster implements VoteHandler, VotifierPlugin {
    @Getter
    private static NuVotifierMaster instance;
    public NuVotifierMaster() {
        instance = this;
    }
    /**
     * The server bootstrap.
     */
    private VotifierServerBootstrap bootstrap;

    /**
     * The RSA key pair.
     */
    private KeyPair keyPair;

    /**
     * Keys used for websites.
     */
    private Map<String, Key> tokens = new HashMap<>();

    private VotifierScheduler scheduler;
    private LoggingAdapter pluginLogger;
    private VotifierConfig config;

    private static Thread thread;

    public static final BlockingQueue<SyncWrapper> runSyncQueue = new LinkedBlockingQueue<>();

    public static class SyncWrapper {
        private Runnable runnable;
        private CompletableFuture<?> future;

        public SyncWrapper(Runnable runnable, CompletableFuture<?> future) {
            this.runnable = runnable;
            this.future = future;
        }

        private long expectedRunTime = -1;

        public SyncWrapper setExpectedRunTime(long expectedRunTime) {
            this.expectedRunTime = expectedRunTime;
            return this;
        }
    }

    @PostConstruct
    public void init() {
        getLogger().info("Initializing Votifier...");
        thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < runSyncQueue.size(); i++) {
                    SyncWrapper wrapper = runSyncQueue.poll();
                    if (wrapper.expectedRunTime != -1 && wrapper.expectedRunTime >= System.currentTimeMillis()) {
                        runSyncQueue.add(wrapper);
                        continue;
                    }
                    getLogger().info("Running sync task...");
                    wrapper.runnable.run();
                    wrapper.future.complete(null);
                }
            }
        }, "Votifier Main");
        thread.start();
        runSyncQueue.add(new SyncWrapper(() -> {
            if (!loadAndBind()) {
                throw new RuntimeException("Failed to load/bind votifier");
            }
        }, new CompletableFuture<>()));
    }

    private boolean loadAndBind() {
        scheduler = new MasterScheduler();
        pluginLogger = new LoggingAdapter() {
            @Override
            public void error(String s) {
                getLogger().error(s);
            }

            @Override
            public void error(String s, Object... objects) {
                getLogger().error(s, objects);
            }

            @Override
            public void warn(String s) {
                getLogger().warn(s);
            }

            @Override
            public void warn(String s, Object... objects) {
                getLogger().warn(s, objects);
            }

            @Override
            public void info(String s) {
                getLogger().info(s);
            }

            @Override
            public void info(String s, Object... objects) {
                getLogger().info(s, objects);
            }
        };


        // Handle configuration.
        config = VotifierConfig.load();

        /*
         * Use spring bind ip Do not use InetAddress.getLocalHost() as it most
         * likely will return the main server address instead of the address
         * assigned to the server.
         */
        String hostAddr = config.getBind();
        if (hostAddr == null || hostAddr.length() == 0)
            hostAddr = "0.0.0.0";

        /*
         * Create configuration file if it does not exist; otherwise, load it
         */
        if (VotifierConfig.isFirstTime()) {
            try {
                // First time run - do some initialization.
                getLogger().info("Configuring Votifier for the first time...");

                // Load and manually replace variables in the configuration.
                String token = TokenUtil.newToken();
                config.setHost(config.getHost().replace("%ip%", hostAddr));
                HashMap<String, String> tokens = new HashMap<>();
                tokens.put("default", token);
                config.setTokens(tokens);
                config.save();

                /*
                 * Remind hosted server admins to be sure they have the right
                 * port number.
                 */
                getLogger().info("------------------------------------------------------------------------------");
                getLogger().info("Assigning NuVotifier to listen on port 8192. If you are hosting Craftbukkit on a");
                getLogger().info("shared server please check with your hosting provider to verify that this port");
                getLogger().info("is available for your use. Chances are that your hosting provider will assign");
                getLogger().info("a different port, which you need to specify in config.yml");
                getLogger().info("------------------------------------------------------------------------------");
                getLogger().info("Your default NuVotifier token is " + token + ".");
                getLogger().info("You will need to provide this token when you submit your server to a voting");
                getLogger().info("list.");
                getLogger().info("------------------------------------------------------------------------------");
            } catch (Exception ex) {
                getLogger().error("Error creating configuration file", ex);
                return false;
            }
        }

        File rsaDirectory = new File("rsa");

        /*
         * Create RSA directory and keys if it does not exist; otherwise, read
         * keys.
         */
        try {
            if (!rsaDirectory.exists()) {
                if (!rsaDirectory.mkdir()) {
                    throw new RuntimeException("Unable to create the RSA key folder " + rsaDirectory);
                }
                keyPair = RSAKeygen.generate(2048);
                RSAIO.save(rsaDirectory, keyPair);
            } else {
                keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception ex) {
            getLogger().error("Error reading configuration file or RSA tokens", ex);
            return false;
        }

        // Load Votifier tokens.

        if (config.getTokens() != null) {
            config.getTokens().forEach((website, token) -> {
                tokens.put(website, KeyCreator.createKeyFrom(token));
                getLogger().info("Loaded token for website: " + website);
            });
        } else {
            String token = TokenUtil.newToken();
            HashMap<String, String> tokens = new HashMap<>();
            tokens.put("default", token);
            config.setTokens(tokens);
            this.tokens.put("default", KeyCreator.createKeyFrom(token));
            config.save();
            getLogger().info("------------------------------------------------------------------------------");
            getLogger().info("No tokens were found in your configuration, so we've generated one for you.");
            getLogger().info("Your default Votifier token is " + token + ".");
            getLogger().info("You will need to provide this token when you submit your server to a voting");
            getLogger().info("list.");
            getLogger().info("------------------------------------------------------------------------------");
        }

        // Initialize the receiver.
        final String host = config.getHost();
        final int port = config.getPort();

        if (port >= 0) {
            final boolean disablev1 = config.isDisablev1();
            if (disablev1) {
                getLogger().info("------------------------------------------------------------------------------");
                getLogger().info("Votifier protocol v1 parsing has been disabled. Most voting websites do not");
                getLogger().info("currently support the modern Votifier protocol in NuVotifier.");
                getLogger().info("------------------------------------------------------------------------------");
            }

            this.bootstrap = new VotifierServerBootstrap(host, port, this, disablev1);
            this.bootstrap.start(error -> {
            });
        } else {
            getLogger().info("------------------------------------------------------------------------------");
            getLogger().info("Your Votifier port is less than 0, so we assume you do NOT want to start the");
            getLogger().info("votifier port server! Votifier will not listen for votes over any port, and");
            getLogger().info("will only listen for pluginMessaging forwarded votes!");
            getLogger().info("------------------------------------------------------------------------------");
        }
        return true;
    }

    public void halt() {
        if (bootstrap != null) {
            bootstrap.shutdown();
            bootstrap = null;
        }
    }

    public boolean reload() {
        try {
            halt();
        } catch (Exception ex) {
            getLogger().error("On halt, an exception was thrown. This may be fine!", ex);
        }

        if (loadAndBind()) {
            getLogger().info("Reload was successful.");
            return true;
        } else {
            try {
                halt();
                getLogger().error("On reload, there was a problem with the configuration. Votifier currently does nothing!");
            } catch (Exception ex) {
                getLogger().error("On reload, there was a problem loading, and we could not re-halt the server. Votifier is in an unstable state!", ex);
            }
            return false;
        }
    }

    private static Logger logger = LoggerFactory.getLogger(NuVotifierMaster.class);

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public Map<String, Key> getTokens() {
        return tokens;
    }

    @Override
    public KeyPair getProtocolV1Key() {
        return keyPair;
    }

    @Override
    public LoggingAdapter getPluginLogger() {
        return pluginLogger;
    }

    @Override
    public VotifierScheduler getScheduler() {
        return scheduler;
    }

    @Autowired
    private LightningHolder lightningHolder;

    @Autowired
    private SettingRepository settingRepository;

    @Override
    public void onVoteReceived(Vote vote, VotifierSession.ProtocolVersion protocolVersion, String s) {
        lightningHolder.getEventBus().callEvent(new VotifierEvent(vote));
    }
}
