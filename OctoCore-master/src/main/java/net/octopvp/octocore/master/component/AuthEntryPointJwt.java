package net.octopvp.octocore.master.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    private static final String UNAUTHORIZED = "{\"success\": false, \"message\": \"Error: Unauthorized\", \"code\": 401}";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        //response.setContentType("application/json");
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED);
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth") || path.equalsIgnoreCase("/login")) return;
        if (path.startsWith("/api")) {
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED);
        } else {
            response.sendRedirect("/login");
        }
    }
}
