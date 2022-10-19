package net.octopvp.aetheriacoremaster.controllers;

import net.octopvp.aetheriacoremaster.config.HttpSessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private HttpSessionConfig sessionConfig;

    @GetMapping("/test")
    public String test(@RequestParam("cookie") String cookie) {
        System.out.println("Cookie: " + cookie);
        return "UID is " + sessionConfig.getUserIDFromSession(cookie);
    }
}
