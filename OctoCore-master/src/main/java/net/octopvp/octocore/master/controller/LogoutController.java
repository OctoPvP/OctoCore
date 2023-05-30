package net.octopvp.octocore.master.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    @GetMapping("/auth/logout")
    public String clearCookies(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Get all the existing cookies
        Cookie[] cookies = request.getCookies();
        // Loop through each cookie and set its max age to 0 to delete it
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
        // Invalidate the session to clear the JSESSIONID cookie
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "logout.html";
    }
}
