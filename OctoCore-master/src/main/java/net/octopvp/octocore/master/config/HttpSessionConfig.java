package net.octopvp.octocore.master.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Configuration
public class HttpSessionConfig {
    private static final Map<String, SessionInfo> sessions = new HashMap<>();

    public List<SessionInfo> getActiveSessions() {
        return new ArrayList<>(sessions.values());
    }

    public boolean isSessionValid(String sessionId) {
        SessionInfo sessionInfo = sessions.get(sessionId);
        if (sessionInfo == null) {
            return false;
        }
        return !sessionInfo.isRevoked();
    }

    public String getUserIDFromSession(String sessionId) {
        return sessions.get(sessionId).getUserId();
    }

    public List<SessionInfo> getActiveSessionsWithoutRevoked() {
        List<SessionInfo> activeSessions = new ArrayList<>();
        for (SessionInfo session : sessions.values()) {
            if (!session.isRevoked()) {
                activeSessions.add(session);
            }
        }
        return activeSessions;
    }

    public void updateUser(HttpSession session, String userId, String username, HttpServletRequest httpServletRequest) {
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (sessions.containsKey(session.getId())) {
            SessionInfo sessionInfo = sessions.get(session.getId());
            sessionInfo.setUserId(userId);
            sessionInfo.setUsername(username);
            sessionInfo.setUserAgent(userAgent);
        } else {
            SessionInfo info = new SessionInfo(session, userId, username);
            sessions.put(session.getId(), info);
            info.setUserAgent(userAgent);
        }
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent hse) {
                sessions.put(hse.getSession().getId(), new SessionInfo(hse.getSession()));
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent hse) {
                sessions.remove(hse.getSession().getId());
            }
        };
    }

    @Getter
    @Setter
    private static class SessionInfo {
        private HttpSession httpSession;
        private String userId, username;
        private String userAgent;

        public SessionInfo(HttpSession httpSession, String userId, String username) {
            this.httpSession = httpSession;
            this.userId = userId;
            this.username = username;
        }

        public SessionInfo(HttpSession httpSession) {
            this.httpSession = httpSession;
        }

        public boolean isRevoked() {
            return false; // TODO
            //return RevokedTokenInterceptor.getRevokedTokens().contains(httpSession.getId());
        }
    }
}
