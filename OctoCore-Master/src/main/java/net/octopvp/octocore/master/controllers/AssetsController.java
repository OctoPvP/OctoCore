package net.octopvp.aetheriacoremaster.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assets")
public class AssetsController {
    @Value("${dev}")
    private boolean dev;

    @GetMapping("/js/izitoast.js")
    public String eventbus() {
        if (dev)
            return "../static/dist/js/izitoast.js";
        else return "../static/dist/js/izitoast.min.js";
    }

    @GetMapping("/css/izitoast.css")
    public String eventbuscss() {
        return "../static/dist/css/izitoast.min.css";
    }
}
