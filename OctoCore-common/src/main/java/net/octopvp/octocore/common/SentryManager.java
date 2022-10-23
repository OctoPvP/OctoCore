package net.octopvp.octocore.common;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class SentryManager {
    public static void init(String sentryDsn) {
        if (sentryDsn == null || sentryDsn.isEmpty())
            return;
        Logger logger = LogManager.getRootLogger();
        Sentry.init(options -> {
            options.setDsn(sentryDsn);
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.setTracesSampleRate(1.0);
            // When first trying Sentry it's good to see what the SDK is doing:
            //options.setDebug(true);
            options.setServerName(OctoCoreCommon.getInstance().getServerName());
            options.setRelease(GitInfo.getCommit() + "/" + GitInfo.getBranch());
        });
        if (Sentry.isEnabled()) {
            logger.addAppender(new AppenderSkeleton() {
                @Override
                protected void append(LoggingEvent event) {
                    if (event.getLevel() == org.apache.log4j.Level.ERROR) {
                        SentryEvent e = new SentryEvent();
                        Message message = new Message();
                        message.setMessage(event.getRenderedMessage());
                        e.setLevel(SentryLevel.ERROR);
                        e.setMessage(message);
                        Sentry.captureEvent(e);
                    }
                    if (event.getLevel() == Level.FATAL) {
                        SentryEvent e = new SentryEvent();
                        Message message = new Message();
                        message.setMessage(event.getRenderedMessage());
                        e.setLevel(SentryLevel.FATAL);
                        e.setMessage(message);
                        Sentry.captureEvent(e);
                    }
                    if (event.getLevel() == Level.WARN) {
                        SentryEvent e = new SentryEvent();
                        Message message = new Message();
                        message.setMessage(event.getRenderedMessage());
                        e.setLevel(SentryLevel.WARNING);
                        e.setMessage(message);
                        Sentry.captureEvent(e);
                    }
                }

                @Override
                public void close() {
                }

                @Override
                public boolean requiresLayout() {
                    return false;
                }
            });
        }
    }

    public static boolean isEnabled() {
        return Sentry.isEnabled();
    }
}
