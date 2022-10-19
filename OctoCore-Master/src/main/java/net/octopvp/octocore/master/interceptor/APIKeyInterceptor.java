package net.octopvp.aetheriacoremaster.interceptor;

import net.octopvp.aetheriacoremaster.repositories.APIRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class APIKeyInterceptor implements HandlerInterceptor {
    private static final String INVALID_TOKEN = "{\"success\": false, \"message\": \"Invalid token or not logged in.\"}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //check if we are on the /api/ path and not /api/pub or /api/authenticated
        String path = request.getRequestURI();
        if ((path.startsWith("/api/authenticated") || path.startsWith("/api/pub")) || !path.startsWith("/api")) {
            return true;
        }
        String parameter = request.getParameter("token");
        if (isAuthenticated(parameter)) return true;
        response.setContentType("application/json");
        response.getWriter().write(INVALID_TOKEN);

        return false;
    }

    public APIKeyInterceptor(APIRepository repository) {
        this.repository = repository;
    }
    private APIRepository repository;

    public boolean isAuthenticated(String token) {
        if (token == null || token.isEmpty()) {
            return SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    //when Anonymous Authentication is enabled
                    !(SecurityContextHolder.getContext().getAuthentication()
                            instanceof AnonymousAuthenticationToken);
        }
        return repository.existsById(token);
    }
}
