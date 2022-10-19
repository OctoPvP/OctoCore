package net.octopvp.aetheriacoremaster.interceptor;

import lombok.Getter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

//@Component
public class RevokedTokenInterceptor implements HandlerInterceptor {
    @Getter
    private static final Set<String> revokedTokens = new java.util.HashSet<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //System.out.println("preHandle");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    //check if the token is revoked
                    if (revokedTokens.contains(cookie.getValue())) {
                        //if it is, redirect to the login page, and remove the cookie
                        Cookie cookieToRemove = new Cookie("JSESSIONID", cookie.getValue());
                        cookieToRemove.setMaxAge(0);
                        response.addCookie(cookieToRemove);
                        response.sendRedirect("/");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
