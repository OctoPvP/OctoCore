package net.octopvp.octocore.master.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {
    private static String html =  """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8"/>
                    <title>Logout</title>
                </head>
                <body>
                <h1>Logout</h1>
                <p>You have been logged out.</p>
                <a href="/login">Login</a>
                </body>
               """;
    @GetMapping("/auth/logout")
    public String clearCookies(HttpServletRequest request, HttpServletResponse response, Model model) {
        response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");
        response.setHeader("Location", "/auth/logout/success");
        return html;
    }
    @GetMapping("/auth/logout/success")
    public String success(HttpServletRequest request, HttpServletResponse response, Model model) {
        return html;
    }
}
